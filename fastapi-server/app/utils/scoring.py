from typing import List
from app.models import PersonalizedIntake, StatusMapping

def compute_score(
    food_nutrients: dict,
    personalized_intake: List[PersonalizedIntake],
    status_list: List[StatusMapping]
) -> float:

    import logging

    def normalize_key(key):
        import re
        # Remove units and content in parentheses/brackets
        key = re.sub(r"[\(\[].*?[\)\]]", "", key)
        # Remove all spaces and special characters
        key = re.sub(r"[\s\-_/]", "", key)
        key = key.lower()
        # Canonical mapping for common nutrients
        mapping = {
            '비타민e': '비타민e',
            '비타민e(mg)': '비타민e',
            '비타민 e': '비타민e',
            '비타민b1': '비타민b1',
            '비타민b2': '비타민b2',
            '비타민b6': '비타민b6',
            '비타민b12': '비타민b12',
            '비타민c': '비타민c',
            '비타민a': '비타민a',
            '비타민d': '비타민d',
            '엽산': '엽산',
            '아연': '아연',
            '칼슘': '칼슘',
            '철': '철',
            '마그네슘': '마그네슘',
            '칼륨': '칼륨',
            '나트륨': '나트륨',
            '에너지': '에너지',
            '단백질': '단백질',
            '지방': '지방',
            '탄수화물': '탄수화물',
            '당류': '당류',
            '식이섬유': '식이섬유',
            '포화지방산': '포화지방산',
            '트랜스지방산': '트랜스지방산',
            '불포화지방': '불포화지방',
            '오메가3지방산': '오메가3지방산',
            '콜레스테롤': '콜레스테롤',
        }
        # Try to map to canonical form
        for variant, canon in mapping.items():
            if variant in key:
                return canon
        return key

    norm_food_nutrients = {normalize_key(k): v for k, v in food_nutrients.items()}
    norm_intake_map = {normalize_key(k): v for k, v in {p.nutrient: p.amount for p in personalized_intake}.items()}

    score = 0.0
    count = 0

    weight_map = {s.nutrient: (s.status, s.weight) for s in status_list}


    for nutrient, (relation, weight) in weight_map.items():
        norm_nutrient = normalize_key(nutrient)
        food_val = norm_food_nutrients.get(norm_nutrient)
        target_val = norm_intake_map.get(norm_nutrient, 0)
        logging.info(f"[compute_score] nutrient: '{nutrient}' (정규화: '{norm_nutrient}'), food_val: {food_val}, target_val: {target_val}")
        logging.info(f"[compute_score] food_nutrients.keys(): {list(norm_food_nutrients.keys())}")
        logging.info(f"[compute_score] intake_map.keys(): {list(norm_intake_map.keys())}")
        if food_val is None or target_val is None or target_val == 0:
            continue
        ratio = food_val / target_val
        if relation == "RECOMMENDED":
            score += max(1.0 - abs(1 - ratio), 0.0) * weight
        elif relation == "RESTRICTED":
            score += max(1.0 - ratio, 0.0) * weight
        count += 1

    return round(score / count, 4) if count > 0 else 0.0
