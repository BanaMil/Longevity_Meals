def ensure_dict_format(meal: list) -> list:
    """
    GPT 응답 중 식단 리스트에서 문자열만 포함된 항목을 dict 형태로 변환합니다.
    """
    return [
        item if isinstance(item, dict) else {"name": item, "intake": 100}
        for item in meal
    ]
