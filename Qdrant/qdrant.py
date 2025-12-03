from pymongo import MongoClient
from sentence_transformers import SentenceTransformer
from qdrant_client import QdrantClient
from qdrant_client.models import PointStruct
import os

# MongoDB 연결
mongo_client = MongoClient("mongodb+srv://Capstone:csd25-1qwe@cluster0.ars7meo.mongodb.net/Longevity_Meals?retryWrites=true&w=majority")
db = mongo_client["Longevity_Meals"]
collection = db["foodDB"]

# Qdrant 연결 (컨테이너 내부에서 'qdrant'로 접근)
qdrant = QdrantClient(host="qdrant", port=6333)

# 임베딩 모델 로딩(사전학습된 문장 임베딩 모델)
# 음식명 + 레시피 + 영양소 -> 벡터로 변환
model = SentenceTransformer("all-MiniLM-L6-v2")

# foodDB 불러오기
docs = list(collection.find({}))
points =[]

# 음식 정보 추출
for i, doc in enumerate(docs):
    name = doc.get("식품명", "")
    category = doc.get("식품대분류명", "")
    ingredients = doc.get("재료", [])
    image_url = doc.get("image_url", "")

    # 영양성분 필드 추출
    nutrients = {}
    nutrient_keys = [
        "에너지(kcal)", "단백질(g)", "지방(g)", "탄수화물(g)", "당류(g)", "식이섬유(g)",
        "칼슘(mg)", "철(mg)", "칼륨(mg)", "나트륨(mg)", "비타민 A(μg RAE)",
        "비타민 C(mg)", "비타민 D(μg)", "콜레스테롤(mg)", "포화지방산(g)", "트랜스지방산(g)",
        "비타민 B6 (mg)", "비타민 B12(μg)", "엽산(μg DFE)", "불포화지방(g)", "오메가3 지방산(g)",
        "마그네슘(mg)"
    ]
    # 영양소 key와 value를 nutrients 딕셔너리에 저장
    for key in nutrient_keys:
        try:
            val = doc.get(key)
            nutrients[key] = float(val) if val not in ("", None) else 0.0
        except:
            nutrients[key] = 0.0

    # 레시피 텍스트 합치기
    recipe_steps = [doc[key] for key in sorted(doc.keys()) if key.startswith("레시피")]
    recipe_text = " ".join(recipe_steps)
        
     # 벡터 임베딩용 텍스트 구성
    ingredient_names = [item["name"] for item in ingredients if "name" in item]
    text_input = f"""
    음식명: {name}
    분류: {category}
    주재료: {', '.join(ingredient_names)}
    영양정보: {', '.join([f"{k}: {v}" for k, v in nutrients.items()])}
    레시피: {recipe_text}
    """

    # 임베딩(벡터) 생성
    vector = model.encode(text_input).tolist()

    # Qdrant에 저장할 point
    # payload : 음식의 메타데이터 -> Qdrant 검색 시 결과로 함께 반환됨
    points.append(PointStruct(
        id=i,
        vector=vector,
        payload={
            "mongo_id": str(doc["_id"]),
            "name": name,
            "category": category,
            "ingredients": ingredients,
            "nutrients": nutrients,
            "image_url": image_url,
            "recipe": recipe_text
        }
    ))

    # Qdrant에 저장 (1회만 실행, 기존 삭제됨)
if points:
    qdrant.recreate_collection(
        collection_name="food_data",
        vectors_config={"size": len(points[0].vector), "distance": "Cosine"}
    )
    qdrant.upsert(collection_name="food_data", points=points)
    print(f"{len(points)} vectors uploaded to Qdrant.")
else:
    print("No documents found in MongoDB collection.")
