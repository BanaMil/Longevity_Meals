from fastapi import APIRouter, HTTPException
from pydantic import BaseModel
from app.models import HealthInfoRequest, DailyMealsResponse, WeeklyMealsResponse, SearchQuery, AdvancedSearchQuery
from typing import List, Literal
from sentence_transformers import SentenceTransformer
from qdrant_client import QdrantClient
from qdrant_client.http.models import Filter, FieldCondition, MatchValue, SearchRequest
import logging
import datetime

router = APIRouter()
# 로깅 설정
logging.basicConfig(level=logging.INFO, format="%(asctime)s - %(levelname)s - %(message)s")

# Qdrant 및 모델 설정
COLLECTION_NAME = "food_data"
INGREDIENT_FIELD = "ingredient_texts"

model = SentenceTransformer("jhgan/ko-sroberta-multitask")
qdrant = QdrantClient(host="qdrant", port=6333)

# 기존 식단 추천 API
@router.post("/mealplan", response_model=DailyMealsResponse)
def recommend_today_meal(request: HealthInfoRequest):
    # TODO: 벡터 생성 및 Qdrant 검색 후 가장 적절한 식단 구성
    return DailyMealsResponse(...)

@router.post("/mealplan/weekly", response_model=WeeklyMealsResponse)
def recommend_weekly_meal(request: HealthInfoRequest):
    return WeeklyMealsResponse(...)

# Simple 버전: 자연어 기반 벡터 검색
@router.post("/search/simple")
def search_simple(query: SearchQuery):
    try:
        logging.info(f"[{datetime.datetime.now()}] SIMPLE 검색 요청 수신: {query.dict()}")

        # 1. 자연어 쿼리 생성
        recommended = []
        restricted = []
        for s in query.statusList:
            level = "상" if s.weight >= 1.3 else "중" if s.weight >= 1.0 else "하"
            entry = f"{s.nutrient} (중요도 {level})"
            if s.status == "RECOMMENDED":
                recommended.append(entry)
            elif s.status == "RESTRICTED":
                restricted.append(entry)

        query_text = (
            f"이 사용자는 다음과 같은 영양소를 선호합니다:\n{', '.join(recommended)}\n\n"
            f"다음 영양소는 제한하고자 합니다:\n{', '.join(restricted)}"
        )

        # 2. 벡터 임베딩
        vector = model.encode(query_text).tolist()

        # 3. 알레르기 및 비선호 필터
        excluded = query.allergies + query.dislikes
        filters = Filter(
            must_not=[
                FieldCondition(key=INGREDIENT_FIELD, match=MatchValue(value=ing))
                for ing in excluded
            ]
        )

        # 4. Qdrant 벡터 검색
        search_result = qdrant.search(
            collection_name=COLLECTION_NAME,
            search_request=SearchRequest(
                vector=vector,
                filter=filters,
                limit=10,
                with_payload=True
            )
        )

        # 5. 결과 정리
        results = []
        for item in search_result:
            results.append({
                "id": item.id,
                "score": round(item.score, 4),
                "name": item.payload.get("name", "이름 없음"),
                "ingredients": item.payload.get("ingredients", []),
                "nutrients": item.payload.get("nutrients", {})
            })

        return {"results": results}

    except Exception as e:
        logging.error(f"SIMPLE 검색 실패: {str(e)}")
        raise HTTPException(status_code=500, detail="벡터 검색 중 오류 발생")

# Weighted 버전: post_score 기반 정량 점수 계산 포함

class PersonalizedIntake(BaseModel):
    nutrient: str
    amount: float  # 권장 섭취량

class AdvancedSearchQuery(BaseModel):
    diseases: List[str]
    allergies: List[str]
    dislikes: List[str]
    statusList: List[StatusMapping]
    personalizedIntake: List[PersonalizedIntake]


def compute_score(
    food_nutrients: dict,
    personalized_intake: List[PersonalizedIntake],
    status_list: List[StatusMapping]
) -> float:
    """
    음식의 영양소 정보와 사용자의 맞춤 섭취량 및 상태 정보를 기반으로 점수 계산.
    권장 영양소는 목표에 가까울수록 높게, 제한 영양소는 적게 포함될수록 높게.
    """
    intake_map = {p.nutrient: p.amount for p in personalized_intake}
    weight_map = {s.nutrient: (s.status, s.weight) for s in status_list}

    score = 0.0
    count = 0

    for nutrient, (relation, weight) in weight_map.items():
        food_val = food_nutrients.get(nutrient)
        target_val = intake_map.get(nutrient)
        if food_val is None or target_val is None or target_val == 0:
            continue

        ratio = food_val / target_val

        if relation == "RECOMMENDED":
            score += max(1.0 - abs(1 - ratio), 0.0) * weight  # 1에 가까울수록 높게
        elif relation == "RESTRICTED":
            score += max(1.0 - ratio, 0.0) * weight  # 작을수록 높게

        count += 1

    return round(score / count, 4) if count > 0 else 0.0

@router.post("/search/weighted")
def search_weighted(query: AdvancedSearchQuery):  # 기존 SearchQuery → AdvancedSearchQuery
    try:
        logging.info(f"[{datetime.datetime.now()}] 검색 요청 수신: {query.dict()}")

        query_text = build_query_text(query.statusList)
        vector = model.encode(query_text).tolist()

        excluded_ingredients = query.allergies + query.dislikes
        filters = Filter(
            must_not=[
                FieldCondition(key=INGREDIENT_FIELD, match=MatchValue(value=ing))
                for ing in excluded_ingredients
            ]
        )

        search_result = qdrant.search(
            collection_name=COLLECTION_NAME,
            search_request=SearchRequest(
                vector=vector,
                filter=filters,
                limit=10,
                with_payload=True
            )
        )

        results = []
        for item in search_result:
            nutrients = item.payload.get("nutrients", {})
            post_score = compute_score(nutrients, query.personalizedIntake, query.statusList)
            results.append({
                "id": item.id,
                "vector_score": round(item.score, 4),
                "post_score": post_score,
                "final_score": round((item.score + post_score) / 2, 4),
                "name": item.payload.get("name", "이름 없음"),
                "ingredients": item.payload.get("ingredients", []),
                "nutrients": item.payload.get("nutrients", {})
            })

        results = sorted(results, key=lambda x: x["final_score"], reverse=True)
        return {"results": results}

    except Exception as e:
        logging.error(f"검색 실패: {str(e)}")
        raise HTTPException(status_code=500, detail="벡터 검색 중 오류 발생")

