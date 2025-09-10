from fastapi import APIRouter, HTTPException
from pydantic import BaseModel
from app.models import HealthInfoRequest, DailyMealsResponse, WeeklyMealsResponse, MealPlanWeeklyRequest, FoodCandidate
from typing import List, Literal
from qdrant_client import QdrantClient
from qdrant_client.http.models import Filter, FieldCondition, MatchValue, SearchRequest
from app.utils.gpt_service import ask_chatgpt_weekly
from app.utils.vectorizer import vectorize_query, vectorize_query_from_health_info
from app.utils.filtering import build_filters
from app.utils.qdrant_client import search
from app.utils.scoring import compute_score
from app.utils.searcher import search_similar_foods
from app.models import FoodItem
import logging
import datetime
import json

router = APIRouter()

# 로깅 설정
logging.basicConfig(level=logging.INFO, format="%(asctime)s - %(levelname)s - %(message)s")

# Qdrant 설정
COLLECTION_NAME = "food_data"
INGREDIENT_FIELD = "ingredient_texts"
qdrant = QdrantClient(host="qdrant", port=6333)


@router.post("/mealplan", response_model=DailyMealsResponse)
def recommend_today_meal(request: HealthInfoRequest):
    # TODO: 벡터 생성 및 Qdrant 검색 후 가장 적절한 식단 구성
    return DailyMealsResponse(...)


@router.post("/mealplan/weekly", response_model=WeeklyMealsResponse)
def recommend_weekly_meal(request: HealthInfoRequest):
    try:
        recommended_vector, restricted_vector = vectorize_query_from_health_info(request)
        food_candidates = search_similar_foods(recommended_vector, restricted_vector, request)
        logging.info(f"[api.py] food_candidates 개수: {len(food_candidates)}")

        user_dict = request.dict()
        foods = [f.dict() for f in food_candidates]
        logging.info(f"[api.py] foods(dict 변환 후) 개수: {len(foods)}")

        gpt_results = ask_chatgpt_weekly(user_dict, foods)

        meals = {}
        for day_plan in gpt_results:
            date = day_plan["date"]
            meals[date] = DailyMealsResponse(
                breakfast=[FoodItem(**item) for item in day_plan["breakfast"]],
                lunch=[FoodItem(**item) for item in day_plan["lunch"]],
                dinner=[FoodItem(**item) for item in day_plan["dinner"]],
            )

        logging.info(f"✅ 최종 반환 결과: {json.dumps({'meals': {k: v.dict() for k, v in meals.items()}}, ensure_ascii=False, indent=2)}")
        return {"meals": meals}
    except Exception as e:
        logging.error(f"WEEKLY GPT 추천 실패: {str(e)}")
        raise HTTPException(status_code=500, detail="GPT 기반 식단 추천 중 오류 발생")




@router.post("/search/weighted")
def search_weighted(query: HealthInfoRequest):
    try:
        logging.info(f"[{datetime.datetime.now()}] 검색 요청 수신: {query.dict()}")

        # 벡터 생성 (vectorizer에서 모델 포함)
        vector = vectorize_query(query.statusList)

        # 필터 조건 생성 (알레르기 및 비선호 음식)
        filters = build_filters(query.allergies, query.dislikes)

        # Qdrant 검색
        search_result = search(qdrant, vector, filters)

        # 결과 처리 및 정량 점수 부여
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

        results.sort(key=lambda x: x["final_score"], reverse=True)
        return {"results": results}

    except Exception as e:
        logging.error(f"검색 실패: {str(e)}")
        raise HTTPException(status_code=500, detail="벡터 검색 중 오류 발생")
