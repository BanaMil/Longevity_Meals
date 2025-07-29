import requests

url = "http://localhost:8000/search"

query = {
    "diseases": ["고혈압"],
    "allergies": ["우유", "계란"],
    "dislikes": ["버섯"],
    "statusList": [
        { "nutrient": "칼륨", "status": "RECOMMENDED", "weight": 1.2, "modifier": 1.0 },
        { "nutrient": "나트륨", "status": "RESTRICTED", "weight": 1.5, "modifier": 1.0 }
    ]
}

response = requests.post(url, json=query)

if response.status_code == 200:
    print("✅ 검색 결과:")
    for result in response.json().get("results", []):
        print(f"- {result['name']} (score: {result['score']})")
        print(f"  재료: {result['ingredients']}")
        print(f"  영양소: {result['nutrients']}")
else:
    print("❌ 오류:", response.status_code, response.text)

# fastapi 서버 실행하면 url 수정해서 
# python3 test_client.py 실행