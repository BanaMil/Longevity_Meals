from qdrant_client.http.models import Filter, FieldCondition, MatchValue

INGREDIENT_FIELD = "ingredients"

def build_filters(allergies: list, dislikes: list) -> Filter:
    excluded = allergies + dislikes
    return Filter(
        must_not=[
            FieldCondition(key=INGREDIENT_FIELD, match=MatchValue(value=ing))
            for ing in excluded
        ]
    )
