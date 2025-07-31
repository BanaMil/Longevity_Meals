from fastapi import APIRouter, HTTPException
from pydantic import BaseModel
from app.models import HealthInfoRequest, DailyMealsResponse, WeeklyMealsResponse, MealPlanWeeklyRequest, FoodCandidate
from typing import List, Literal
from app.utils.gpt_service import ask_chatgpt
from sentence_transformers import SentenceTransformer
from qdrant_client import QdrantClient
from qdrant_client.http.models import Filter, FieldCondition, MatchValue, SearchRequest
from app.utils.vectorizer import build_query_vectors
from app.utils.filtering import build_filters
from app.utils.qdrant_client import search
from app.utils.scoring import compute_score
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
def recommend_weekly_meal(payload: MealPlanWeeklyRequest):
    try:
        user_dict = payload.user.dict()
        foods = [food.dict() for food in payload.foods]

        gpt_response = ask_chatgpt(user_dict, foods)
        parsed = json.loads(gpt_response)

        meals = {}
        for day in parsed:
            date = day["date"]
            meals[date] = DailyMealsResponse(
                breakfast=[{"name": item["name"], "intake": item["intake"]} for item in day["breakfast"]],
                lunch=[{"name": item["name"], "intake": item["intake"]} for item in day["lunch"]],
                dinner=[{"name": item["name"], "intake": item["intake"]} for item in day["dinner"]],
            )

        return WeeklyMealsResponse(meals=meals)

    except Exception as e:
        logging.error(f"WEEKLY GPT 추천 실패: {str(e)}")
        raise HTTPException(status_code=500, detail="GPT 기반 식단 추천 중 오류 발생")



# Weighted 버전: post_score 기반 정량 점수 계산 포함
@router.post("/search/weighted")
def search_weighted(query: AdvancedSearchQuery):
    try:
        logging.info(f"[{datetime.datetime.now()}] 검색 요청 수신: {query.dict()}")

        query_text = build_query_text(query.statusList)
        vector = model.encode(query_text).tolist()

        filters = build_filters(query.allergies, query.dislikes)
        search_result = search(qdrant, vector, filters)

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

