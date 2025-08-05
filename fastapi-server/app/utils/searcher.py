from typing import List
from qdrant_client import QdrantClient
from qdrant_client.http.models import Filter, SearchRequest, FieldCondition, MatchValue
from app.models import HealthInfoRequest, FoodCandidate
from app.utils.qdrant_client import search, client
from app.utils.scoring import compute_score  # 점수 계산 함수 (있다면)

COLLECTION_NAME = "food_data"  # 실제 Qdrant 컬렉션 이름으로 수정

def build_qdrant_filter(allergies: List[str], dislikes: List[str]) -> Filter:
    """
    알레르기 및 비선호 재료를 포함한 음식은 제외하는 Qdrant 필터 생성
    """
    conditions = []
    for keyword in allergies + dislikes:
        conditions.append(
            FieldCondition(
                key="ingredients",  # Qdrant payload 필드명
                match=MatchValue(value=keyword)
            )
        )

    if conditions:
        return Filter(
            must_not=conditions
        )
    else:
        return Filter(must=[])


def search_similar_foods(
    recommended_vector: List[float],
    restricted_vector: List[float],
    request: HealthInfoRequest
) -> List[FoodCandidate]:
    """
    권장/제한 벡터 기반 Qdrant 검색 및 음식 후보 정제
    """
    filters = build_qdrant_filter(request.allergies, request.dislikes)

    # Qdrant에서 두 벡터 각각으로 검색
    recommended_results = search(client, recommended_vector, filters, limit=30)
    restricted_results = search(client, restricted_vector, filters, limit=30)

    # 결과 정합 처리: 점수 계산, 중복 제거
    food_map = {}

    for res in recommended_results:
        payload = res.payload
        food_id = payload["food_id"]
        food_map[food_id] = FoodCandidate(
            id=food_id,
            name=payload["name"],
            nutrients=payload["nutrients"],  # Dict[str, float]
            ingredients=payload["ingredients"],
            score=0.0  # 점수는 나중에 계산
        )

    for res in restricted_results:
        payload = res.payload
        food_id = payload["food_id"]
        if food_id not in food_map:
            food_map[food_id] = FoodCandidate(
                id=food_id,
                name=payload["name"],
                nutrients=payload["nutrients"],
                ingredients=payload["ingredients"],
                score=0.0
            )

    # 점수 계산
    for food in food_map.values():
        food.score = compute_score(
            food.nutrients,
            request.personalizedIntake,
            request.statusList
        )

    # 점수순 정렬 후 반환
    sorted_foods = sorted(food_map.values(), key=lambda x: x.score, reverse=True)
    return sorted_foods[:30]  # 최대 30개 반환
