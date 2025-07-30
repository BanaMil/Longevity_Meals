from typing import List, Dict
from models import HealthInfoRequest
from db import get_nutrient_list, get_disease_nutrient_relations

def build_query_vectors(health_info: HealthInfoRequest) -> Dict[str, List[float]]:
    """
    사용자의 질병 정보를 기반으로 권장/제한 영양소 벡터를 생성
    반환: {"recommended": List[float], "restricted": List[float]}
    """
    nutrient_list = get_nutrient_list()  # 예: ["탄수화물", "단백질", "지방", ...]
    index_map = {nutrient: idx for idx, nutrient in enumerate(nutrient_list)}
    vector_size = len(nutrient_list)

    recommended_vector = [0.0] * vector_size
    restricted_vector = [0.0] * vector_size

    for disease in health_info.diseases:
        relations = get_disease_nutrient_relations(disease)  # MongoDB에서 조회
        for r in relations:
            nutrient = r["nutrient"]
            relation = r["relation"].upper()
            modifier = r.get("modifier", 1.0)

            if nutrient in index_map:
                idx = index_map[nutrient]
                if relation == "RECOMMENDED":
                    recommended_vector[idx] += modifier
                elif relation == "RESTRICTED":
                    restricted_vector[idx] += modifier

    return {
        "recommended": recommended_vector,
        "restricted": restricted_vector
    }
