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

# 임베딩 모델 로드(한국어 특화)
model = SentenceTransformer("jhgan/ko-sroberta-multitask") 

# 컨테이너에서 실행중인 Qdrant 연결
qdrant = QdrantClient(host="qdrant", port=6333) 

# Qdrant 컬렉션 이름
COLLECTION_NAME = "food_data" 
# 음식 데이터에서 필터링에 사용할 필드명 (비선호/알러지 재료가 포함된 음식 제거용)
INGREDIENT_FIELD = "ingredient_texts"  

# 요청 데이터 스키마 정의
# /search API에 전달되는 요청 형식 정의
class StatusMapping(BaseModel):  # health_Info의 Status List
    nutrient: str
    status: Literal["RECOMMENDED", "RESTRICTED"]
    weight: float # 중요도 (가중치)
    modifier: float # 개인화 비율 (1.2배, 0.6배 등)

# 쿼리 데이터 스키마 정의
class SearchQuery(BaseModel): 
    diseases: List[str]
    allergies: List[str]
    dislikes: List[str]
    statusList: List[StatusMapping] # 사용자 맞춤 영양소 상태
    personalizedIntake: dict # {"단백질(g)": 36, ...} - 개인화된 섭취 기준 (절대 수치)

# 쿼리 텍스트 생성 (정량적 수치 기반)
def build_query_text(status_list: List[StatusMapping]) -> str:
    entries = []
    for s in status_list:
        entries.append(f"{s.nutrient} (중요도 {s.weight:.2f}) [{s.status}]")
    return "영양소 중요도 목록:\n" + ", ".join(entries)

# 후처리 점수 계산 함수 (수치 기반 비교)
def compute_score(item_nutrients: dict, target: dict, status_list: List[StatusMapping]) -> float:
    score = 0.0
    weight_sum = 0.0
    for status in status_list:
        name = status.nutrient
        weight = status.weight
        if name in target and name in item_nutrients:
             try:
                food_val = float(item_nutrients[name])     # 음식의 해당 영양소 값
                user_target = float(target[name])          # 사용자 목표 섭취량
                ratio = food_val / user_target if user_target != 0 else 0.0
                diff = 1 - abs(1 - ratio)                  # 이상적인 비율(1.0)과 얼마나 유사한지
                diff = max(0.0, min(diff, 1.0))            # 점수 범위 [0, 1]로 제한

                if status.status == "RECOMMENDED":
                    score += weight * diff                # 비율이 1에 가까울수록 가산점
                elif status.status == "RESTRICTED":
                    score += weight * (1 - diff)          # 비율이 작을수록 가산점
                weight_sum += weight
            except:
                continue
    return round(score / weight_sum, 4) if weight_sum > 0 else 0.0

# 벡터 기반 음식 검색 API
# /search 라는 주소에 POST 요청이 오면 실행되는 함수
@app.post("/search")
def search(query: SearchQuery):
    try:
        logging.info(f"[{datetime.datetime.now()}] 검색 요청 수신: {query.dict()}")

        # 1. 텍스트 임베딩 쿼리 생성 → 벡터 생성
        query_text = build_query_text(query.statusList)
        vector = model.encode(query_text).tolist()

        # 2. 알러지 및 비선호 재료 필터링 조건 생성 (Qdrant에서 제외할 재료들)
        excluded_ingredients = query.allergies + query.dislikes
        filters = Filter(
            must_not=[
                FieldCondition(key=INGREDIENT_FIELD, match=MatchValue(value=ing))
                for ing in excluded_ingredients
            ]
        )

        # 3. Qdrant 벡터 검색
        search_result = qdrant.search(
            collection_name=COLLECTION_NAME,
            search_request=SearchRequest(
                vector=vector,
                filter=filters,
                limit=10, # 상위 10개(조정 가능)
                with_payload=True
            )
        )

        # 4. 벡터 결과 + 수치 기반 후처리 점수 계산 및 통합
        results = []
        for item in search_result:
            nutrients = item.payload.get("nutrients", {})
            post_score = compute_score(nutrients, query.personalizedIntake, query.statusList)
            results.append({
                "id": item.id,
                "vector_score": round(item.score, 4), # Qdrant 벡터 유사도
                "post_score": post_score, # personalizedIntake 기준 수치 점수 계산
                "final_score": round((item.score + post_score) / 2, 4), # 평균 (final_score 기준으로 정렬 후 상위 추천 음식 반환)
                "name": item.payload.get("name", "이름 없음"),
                "ingredients": item.payload.get("ingredients", []),
                "nutrients": item.payload.get("nutrients", {})
            })

        # 5. 최종 점수를 기준으로 음식 정렬 후 반환
        results = sorted(results, key=lambda x: x["final_score"], reverse=True)
        return {"results": results}

    except Exception as e:
        logging.error(f"검색 실패: {str(e)}")
        raise HTTPException(status_code=500, detail="벡터 검색 중 오류 발생")


# 1. 벡터 유사도 검색(Qdrant) : statusList를 문장화 -> 임베딩 -> 벡터 유사도가 높은 음식 N개 검색
# 2. 후처리 점수 계산(FastAPI 내부) : 검색된 음식의 영양소 정보를 기준으로 사용자의 personalizedIntake 값과 비교하여 각 음식의 정량적 유사도 점수(post_score) 계산
# 3. 최종 추천 점수 계산 : final_score = (vector_score + post_score) / 2 로 1.과 2.의 평균을 계산

# 각 영양소에 대해 weight, modifier, status를 반영하여 수치 기반 비교 수행








# "단백질 30g인 음식만 찾아줘" → Qdrant 자체로는 불가
# "단백질 목표 섭취량이 36g인 사용자에게 적절한 음식은?" → post_score 계산으로 구현
#  정량적 기준은 검색 조건이 아닌 후처리 평가 기준으로 사용됨.
#  벡터 검색은 여전히 임베딩 기반 자연어 질의 처리에 의존

# 자연어로 음식 검색 vs. 정량적 기준으로 음식 검색(후처리 계산)