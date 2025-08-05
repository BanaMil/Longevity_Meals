from qdrant_client import QdrantClient
from qdrant_client.http.models import SearchRequest

COLLECTION_NAME = "food_data"  # 실제 사용 중인 컬렉션 이름으로 수정

# 전역 클라이언트 인스턴스 (필요시 앱 전체에서 import하여 사용)
client = QdrantClient(host="qdrant", port=6333)

def search(client: QdrantClient, vector, filters, limit=10):
    return client.search(
        collection_name=COLLECTION_NAME,
        search_request=SearchRequest(
            query_vector=vector,
            query_filter=filters,
            limit=limit,
            with_payload=True
        )
    )
