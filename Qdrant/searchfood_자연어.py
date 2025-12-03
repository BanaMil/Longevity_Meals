from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from typing import List, Literal
from sentence_transformers import SentenceTransformer
from qdrant_client import QdrantClient
from qdrant_client.http.models import Filter, FieldCondition, MatchValue, SearchRequest
import logging
import datetime

app = FastAPI() # FastAPI 인스턴스 생성

# 로깅 설정
logging.basicConfig(level=logging.INFO, format="%(asctime)s - %(levelname)s - %(message)s")

model = SentenceTransformer("jhgan/ko-sroberta-multitask") # 임베딩 모델 로드(한국어 특화)
qdrant = QdrantClient(host="qdrant", port=6333) # 컨테이너에서 실행중인 Qdrant 연결

COLLECTION_NAME = "food_data" # Qdrant 컬렉션 이름
INGREDIENT_FIELD = "ingredient_texts"  # Qdrant 필터링에 사용할 필드 이름

# 사용자 Json 받을 데이터 모델 정의
# /search API에 전달되는 요청 형식 정의
class StatusMapping(BaseModel):  # health_Info의 Status List
    nutrient: str
    status: Literal["RECOMMENDED", "RESTRICTED"]
    weight: float
    modifier: float

class SearchQuery(BaseModel): # 쿼리 데이터 형식 정의
    diseases: List[str]
    allergies: List[str]
    dislikes: List[str]
    statusList: List[StatusMapping]

# 가중치 수준 계산 함수
def get_weight_level(weight: float) -> str:
    if weight >= 1.3:
        return "상"
    elif weight >= 1.0:
        return "중"
    else:
        return "하"

# 쿼리 텍스트 생성 함수
def build_query_text(status_list: List[StatusMapping]) -> str:
    recommended = []
    restricted = []
    # 권장 영양소는 +weight, 제한 영양소는 -weight 로 구성
    # 권장/제한 영양소를 나눠 자연어 텍스트로 구성 -> 임베딩(벡터화) -> Qdrant 벡터 쿼리로 사용
    for s in status_list:
        level = get_weight_level(s.weight)
        entry = f"{s.nutrient} (중요도 {level})"
        if s.status == "RECOMMENDED":
            recommended.append(entry)
        elif s.status == "RESTRICTED":
            restricted.append(entry)

    return (
        f"이 사용자는 다음과 같은 영양소를 선호합니다:\n{', '.join(recommended)}\n\n"
        f"다음 영양소는 제한하고자 합니다:\n{', '.join(restricted)}"
    )

# 벡터 기반 음식 검색 API
# /search 라는 주소에 POST 요청이 오면 실행되는 함수
@app.post("/search")
def search(query: SearchQuery):
    try:
        logging.info(f"[{datetime.datetime.now()}] 검색 요청 수신: {query.dict()}")

        # 1. 질의 문장 생성 및 임베딩
        query_text = build_query_text(query.statusList)
        vector = model.encode(query_text).tolist()

        # 2. 알레르기 + 비선호 재료 필터 설정
        excluded_ingredients = query.allergies + query.dislikes
        filters = Filter(
            must_not=[
                FieldCondition(key=INGREDIENT_FIELD, match=MatchValue(value=ing))
                for ing in excluded_ingredients
            ]
        )

        # 3. Qdrant 검색
        search_result = qdrant.search(
            collection_name=COLLECTION_NAME,
            search_request=SearchRequest(
                vector=vector,
                filter=filters,
                limit=10,
                with_payload=True
            )
        )

        # 4. 검색 결과 정리
        results = []
        for item in search_result:
            results.append({
                "id": item.id,
                "score": round(item.score, 4),  # 유사도 점수
                "name": item.payload.get("name", "이름 없음"),
                "ingredients": item.payload.get("ingredients", []),
                "nutrients": item.payload.get("nutrients", {})
            })

        return {"results": results}

    except Exception as e:
        logging.error(f"검색 실패: {str(e)}")
        raise HTTPException(status_code=500, detail="벡터 검색 중 오류 발생")

# 쿼리 텍스트
# "이 사용자는 ~ 선호합니다. ~ 제한합니다." 식의 자연어 텍스트

# weight만 사용하여 문장형 텍스트로 변환하여 임베딩