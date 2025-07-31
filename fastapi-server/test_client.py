import requests
import json

url = "http://localhost:8000/api/gpt/mealplan/weekly"
headers = {"Content-Type": "application/json"}

payload = {
    "user": {
        "userid": "abc123",
        "gender": "female",
        "height": 165,
        "weight": 55,
        "diseases": ["고혈압"],
        "allergies": ["우유", "계란"],
        "dislikes": ["버섯"],
        "statusList": [
            { "nutrient": "칼륨", "status": "RECOMMENDED", "weight": 1.2, "modifier": 1.0 },
            { "nutrient": "나트륨", "status": "RESTRICTED", "weight": 1.5, "modifier": 1.0 }
        ],
        "personalizedIntake": {
            "칼륨": 3500,
            "나트륨": 1500
        }
    },
    "foods": [
        {
            "name": "잡곡밥",
            "ingredients": ["현미", "보리", "쌀"],
            "nutrients": { "칼륨": 320, "나트륨": 10 },
            "score": 0.94
        },
        {
            "name": "된장국",
            "ingredients": ["된장", "두부", "호박"],
            "nutrients": { "칼륨": 450, "나트륨": 180 },
            "score": 0.89
        },
        {
            "name": "호박볶음",
            "ingredients": ["애호박", "양파"],
            "nutrients": { "칼륨": 210, "나트륨": 20 },
            "score": 0.91
        }
    ]
}

response = requests.post(url, headers=headers, data=json.dumps(payload))
print("✅ 응답 결과:\n", json.dumps(response.json(), indent=2, ensure_ascii=False))
