from pydantic import BaseModel
from typing import List, Dict, Literal

class HealthInfoRequest(BaseModel):
    diseases: List[str]
    allergies: List[str]
    dislikes: List[str]
    statusList: List[dict]  # or custom class

class DailyMealsResponse(BaseModel):
    breakfast: List[str]
    lunch: List[str]
    dinner: List[str]

class WeeklyMealsResponse(BaseModel):
    meals: Dict[str, DailyMealsResponse]
