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
    import logging
    filters = build_qdrant_filter(request.allergies, request.dislikes)
    import pprint
    logging.info(f"[search_similar_foods] build_qdrant_filter must_not: {getattr(filters, 'must_not', None)}")

    # Qdrant에서 두 벡터 각각으로 검색

    recommended_results = search(client, recommended_vector, filters, limit=30)
    restricted_results = search(client, restricted_vector, filters, limit=30)

    # raw payload 예시(처음 2개)
    if recommended_results:
        logging.info(f"[search_similar_foods] recommended raw payload sample: {pprint.pformat(recommended_results[0].payload)}")
        if len(recommended_results) > 1:
            logging.info(f"[search_similar_foods] recommended raw payload sample2: {pprint.pformat(recommended_results[1].payload)}")
    if restricted_results:
        logging.info(f"[search_similar_foods] restricted raw payload sample: {pprint.pformat(restricted_results[0].payload)}")

    logging.info(f"[search_similar_foods] recommended_results: {len(recommended_results)}개, restricted_results: {len(restricted_results)}개")
    logging.info(f"[search_similar_foods] recommended 음식명: {[res.payload.get('name') for res in recommended_results]}")
    logging.info(f"[search_similar_foods] restricted 음식명: {[res.payload.get('name') for res in restricted_results]}")

    # 결과 정합 처리: 점수 계산, 중복 제거
    food_map = {}


    for res in recommended_results:
        payload = res.payload
        food_id = payload.get("food_id") or payload.get("id") or payload.get("name")
        logging.info(f"[search_similar_foods] 추천 후보 food_id: {food_id}, name: {payload.get('name')}")
        if food_id in food_map:
            logging.info(f"[search_similar_foods] (중복) 이미 존재하는 food_id: {food_id}, name: {payload.get('name')}")
        food_map[food_id] = FoodCandidate(
            id=food_id,
            name=payload["name"],
            nutrients=payload["nutrients"],  # Dict[str, float]
            ingredients=payload["ingredients"],
            score=0.0  # 점수는 나중에 계산
        )
        logging.info(f"[search_similar_foods] food_map 추가 (추천): id={food_id}, name={payload['name']}")


    for res in restricted_results:
        payload = res.payload
        food_id = payload.get("food_id") or payload.get("id") or payload.get("name")
        logging.info(f"[search_similar_foods] 제한 후보 food_id: {food_id}, name: {payload.get('name')}")
        if food_id in food_map:
            logging.info(f"[search_similar_foods] (중복) 이미 존재하는 food_id: {food_id}, name: {payload.get('name')}")
        if food_id not in food_map:
            food_map[food_id] = FoodCandidate(
                id=food_id,
                name=payload["name"],
                nutrients=payload["nutrients"],
                ingredients=payload["ingredients"],
                score=0.0
            )
            logging.info(f"[search_similar_foods] food_map 추가 (제한): id={food_id}, name={payload['name']}")

    # 점수 계산

    for food in food_map.values():
        food.score = compute_score(
            food.nutrients,
            request.personalizedIntake,
            request.statusList
        )
        logging.info(f"[search_similar_foods] 점수 계산: {food.name} → {food.score}")

    # 점수순 정렬 후 반환
    sorted_foods = sorted(food_map.values(), key=lambda x: x.score, reverse=True)
    logging.info(f"[search_similar_foods] 최종 후보 개수: {len(sorted_foods)} / 상위 30개 반환")
    for f in sorted_foods[:30]:
        logging.info(f"[search_similar_foods] 최종 후보: name={f.name}, score={f.score}, ingredients={f.ingredients}")
    return sorted_foods[:30]  # 최대 30개 반환
