from openai import OpenAI
from typing import Dict, List
import os
import json
import logging

# OpenAI API 키
client = OpenAI(api_key="")

# 사용자 건강 정보와 음식 후보 리스트를 바탕으로 GPT에게 전달할 프롬프트(자연어)를 생성하는 함수
def build_gpt_prompt(user: dict, foods: list) -> str:
    # 질병, 알레르기, 비선호 음식 문자열 구성 (리스트 → 쉼표 구분 문자열)
    disease_str = ", ".join(user.get("diseases", []))
    allergy_str = ", ".join(user.get("allergies", []))
    dislike_str = ", ".join(user.get("dislikes", []))
    
    # 영양소 상태 목록 (RECOMMENDED / RESTRICTED 등) 포맷 구성
    status_list = user.get("statusList", [])
    status_str = "\n".join([
        f"- {s['nutrient']}: {s['status']} (가중치 {s['weight']}, 비율 {s['modifier']})"
        for s in status_list
    ])
    
    # 개인 맞춤 섭취 기준 (예: 단백질 36g 등) 포맷 구성
    intake_list = user.get("personalizedIntake", [])
    intake_str = "\n".join([f"- {item['nutrient']}: {item['amount']}" for item in intake_list])

    
    # 음식 후보 리스트 포맷 구성 (이름 + 재료 + 영양소)
    food_str = "\n".join([
    f"{i+1}. {f['name']}\n"
    f"   - 재료: {', '.join(f['ingredients'])}\n"
    f"   - 주요 영양소: {', '.join([f'{k}: {v}' for k, v in f['nutrients'].items()])}"
    for i, f in enumerate(foods)
    ]) if foods else "(음식 후보가 없습니다)"

# prompt 작성
    return f"""
당신은 건강 식단 전문가입니다.

[사용자 정보]
- 질병: {disease_str}
- 알레르기: {allergy_str}
- 비선호 음식: {dislike_str}

[영양소 상태]
{status_str}

[개인 맞춤 섭취량]
{intake_str}

[추천 가능한 음식 리스트]
{food_str}

위 정보를 기반으로 아래 형식처럼 JSON 형태의 7일 식단을 추천해주세요. 
형식은 다음과 같아야 합니다:

{{
  "userId": "{user.get("userId", "unknown")}",
  "date": "2025-06-10",
  "breakfast": [
    {{ "name": "음식명", "intake": g }}
  ],
  "lunch": [
    {{ "name": "음식명", "intake": g }}
  ],
  "dinner": [
    {{ "name": "음식명", "intake": g }}
  ]
}}

제한사항:
- 각 식사는 밥, 국, 반찬 3가지의 음식으로 구성해주세요.
- 7일 식단으로 7개의 json 파일을 만들어주세요.
- intake는 숫자만 기입하며 g 단위를 생략해주세요.
- 알레르기나 비선호 재료는 반드시 제외해주세요.
- JSON 외의 설명은 절대 출력하지 마세요.
"""

# GPT에게 프롬프트를 전송하고 응답받아 결과를 문자열로 반환하는 함수
def ask_chatgpt(user: dict, foods: list) -> str: 
    prompt = build_gpt_prompt(user, foods) #prompt 함수 호출
    try:
        response = client.chat.completions.create(
            model="gpt-4", # 사용할 GPT 모델
            messages=[
                {"role": "system", "content": "당신은 건강 식단 전문가입니다."},
                {"role": "user", "content": prompt}
            ],
            temperature=0.7, # 창의성 조절 (0.0: 고정, 1.0: 창의적)
            max_tokens=2200  # 최대 출력 길이 제한
        )
        content = response.choices[0].message.content
        # JSON 유효성 검증
        try:
            _ = json.loads(content)
        except json.JSONDecodeError:
            logging.warning("⚠️ GPT 응답이 JSON 형식이 아닙니다.")
        return content
    except Exception as e:
        logging.error(f"GPT API 요청 실패: {e}")
        raise RuntimeError("GPT 응답 생성 중 오류가 발생했습니다.")


# build_gpt_prompt(user, foods)
# → 사용자 건강 정보와 음식 리스트를 바탕으로 GPT에게 요청할 프롬프트 문자열을 생성합니다.

# ask_chatgpt(user, foods)
# → 해당 프롬프트를 GPT에게 보내고, 식단 추천 결과를 받아옵니다.

# 반환 결과는 문자열로 구성된 아침/점심/저녁 식단표