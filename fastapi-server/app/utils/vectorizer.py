# app/utils/vectorizer.py

from typing import List
from sentence_transformers import SentenceTransformer
from app.models import StatusMapping

# 모델 초기화 (한 번만 로드됨)
model = SentenceTransformer("jhgan/ko-sroberta-multitask")


def build_query_text(status_list: List[StatusMapping]) -> str:
    """
    statusList로부터 쿼리 텍스트 구성 (영양소 상태 정보 기반)
    예시: 단백질 RECOMMENDED 0.9
    """
    lines = []
    for s in status_list:
        status_word = s.status.upper()  # "RECOMMENDED", "RESTRICTED"
        line = f"{s.nutrient} {status_word} {s.weight}"
        lines.append(line)
    return "\n".join(lines)


def vectorize_query(status_list: List[StatusMapping]) -> List[float]:
    """
    상태 정보를 기반으로 쿼리 벡터 생성
    """
    query_text = build_query_text(status_list)
    return model.encode(query_text).tolist()
