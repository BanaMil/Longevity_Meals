# fastapi-server/app/api.py

from fastapi import APIRouter
from app.models import HealthInfoRequest, DailyMealsResponse, WeeklyMealsResponse
# 기타 필요한 import

router = APIRouter()

@router.post("/mealplan", response_model=DailyMealsResponse)
def recommend_today_meal(request: HealthInfoRequest):
    # 추천 로직 연결 예정
    ...

@router.post("/mealplan/weekly", response_model=WeeklyMealsResponse)
def recommend_weekly_meal(request: HealthInfoRequest):
    ...
