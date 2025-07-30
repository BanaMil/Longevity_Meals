# fastapi-server/app/models.py

from pydantic import BaseModel
from typing import List, Dict


class HealthInfoRequest(BaseModel):
    gender: str
    height: float
    weight: float
    diseases: List[str]
    allergies: List[str]
    dislikes: List[str]


class DailyMealsResponse(BaseModel):
    breakfast: List[str]
    lunch: List[str]
    dinner: List[str]


class WeeklyMealsResponse(BaseModel):
    meals: Dict[str, DailyMealsResponse]
