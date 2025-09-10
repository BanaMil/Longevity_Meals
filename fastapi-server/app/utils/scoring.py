from typing import List
from app.models import PersonalizedIntake, StatusMapping

def compute_score(
    food_nutrients: dict,
    personalized_intake: List[PersonalizedIntake],
    status_list: List[StatusMapping]
) -> float:
    intake_map = {p.nutrient: p.amount for p in personalized_intake}
    weight_map = {s.nutrient: (s.status, s.weight) for s in status_list}

    score = 0.0
    count = 0


    import logging
    for nutrient, (relation, weight) in weight_map.items():
        food_val = food_nutrients.get(nutrient)
        target_val = intake_map.get(nutrient, 0)
        logging.info(f"[compute_score] nutrient: '{nutrient}', food_val: {food_val}, target_val: {target_val}")
        logging.info(f"[compute_score] food_nutrients.keys(): {list(food_nutrients.keys())}")
        logging.info(f"[compute_score] intake_map.keys(): {list(intake_map.keys())}")
        if food_val is None or target_val is None or target_val == 0:
            continue

        ratio = food_val / target_val
        if relation == "RECOMMENDED":
            score += max(1.0 - abs(1 - ratio), 0.0) * weight
        elif relation == "RESTRICTED":
            score += max(1.0 - ratio, 0.0) * weight

        count += 1

    return round(score / count, 4) if count > 0 else 0.0
