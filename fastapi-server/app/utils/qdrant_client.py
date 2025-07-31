from qdrant_client import QdrantClient
from qdrant_client.http.models import SearchRequest

COLLECTION_NAME = "your_collection"  # 필요 시 주입 방식으로 개선 가능

def search(client: QdrantClient, vector, filters, limit=10):
    return client.search(
        collection_name=COLLECTION_NAME,
        search_request=SearchRequest(
            vector=vector,
            filter=filters,
            limit=limit,
            with_payload=True
        )
    )
