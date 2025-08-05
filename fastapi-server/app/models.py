# fastapi-server/app/models.py

from pydantic import BaseModel
from typing import List, Dict, Literal

class StatusMapping(BaseModel):  # health_Info의 Status List
    nutrient: str
    status: Literal["RECOMMENDED", "RESTRICTED"]
    weight: float # 중요도 (가중치)
    modifier: float # 개인화 비율 (1.2배, 0.6배 등)

class PersonalizedIntake(BaseModel):
    nutrient: str
    amount: float  # 예: 단백질 36.0 (단위는 g)

class HealthInfoRequest(BaseModel):
    userid: str
    gender: str
    height: float
    weight: float
    diseases: List[str]
    allergies: List[str]
    dislikes: List[str]
    statusList: List[StatusMapping] # 사용자 맞춤 영양소 상태
    personalizedIntake: List[PersonalizedIntake] # {"단백질(g)": 36, ...} - 개인화된 섭취 기준 (절대 수치)

class FoodItem(BaseModel):
    name: str
    intake: float  # 단위: g

# 일일 식단 응답
class DailyMealsResponse(BaseModel):
    breakfast: List[FoodItem]
    lunch: List[FoodItem]
    dinner: List[FoodItem]

# 주간 식단 응답
class WeeklyMealsResponse(BaseModel):
    meals: Dict[str, DailyMealsResponse]

class Ingredient(BaseModel):
    name: str
    amount: str

# 음식 후보 (벡터 검색 결과 + 점수 포함)
class FoodCandidate(BaseModel):
    name: str
    ingredients: List[Ingredient]
    nutrients: Dict[str, float]
    score: float

# GPT를 위한 주간 식단 추천 요청 모델
class MealPlanWeeklyRequest(BaseModel):
    user: HealthInfoRequest
    foods: List[FoodCandidate]

