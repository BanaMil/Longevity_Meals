from openai import OpenAI
from typing import Dict, List
import os
import json
import logging
from datetime import datetime, timedelta
from dotenv import load_dotenv
from app.utils.formatter import ensure_dict_format

# .env 파일에서 환경 변수 불러오기
load_dotenv()
api_key = os.getenv("OPENAI_API_KEY")
if not api_key:
    raise RuntimeError("❌ OPENAI_API_KEY가 .env에 설정되어 있지 않습니다.")
client = OpenAI(api_key=api_key)

# ✅ 하루 단위 프롬프트 생성
def build_gpt_prompt_for_day(user: dict, foods: list, date: str) -> str:
    disease_str = ", ".join(user.get("diseases", []))
    allergy_str = ", ".join(user.get("allergies", []))
    dislike_str = ", ".join(user.get("dislikes", []))

    status_list = user.get("statusList", [])
    status_str = "\n".join([
        f"- {s['nutrient']}: {s['status']} (가중치 {s['weight']}, 비율 {s['modifier']})"
        for s in status_list
    ])

    intake_list = user.get("personalizedIntake", [])
    intake_str = "\n".join([f"- {item['nutrient']}: {item['amount']}" for item in intake_list])

    food_str = "\n".join([
        f"{i+1}. {f['name']}\n"
        f"   - 재료: {', '.join(f['ingredients'])}\n"
        f"   - 주요 영양소: {', '.join([f'{k}: {v}' for k, v in f['nutrients'].items()])}"
        for i, f in enumerate(foods)
    ]) if foods else "(음식 후보가 없습니다)"

    return f"""
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

요청:
- 위 정보를 기반으로 아래 날짜에 해당하는 하루 식단을 구성해주세요.
- 세 끼 식사(`breakfast`, `lunch`, `dinner`)를 포함해야 하며,
- 각 식사는 `밥`, `국`, `반찬 3가지`로 구성됩니다.
- 아래 JSON 형식과 완전히 일치해야 하며, 설명 없이 JSON 객체만 출력하세요.

형식:
{{
  "userid": "{user.get("userid", "unknown")}",
  "date": "{date}",
  "breakfast": [
    {{ "name": "음식명", "intake": 100 }},
    ...
  ],
  "lunch": [
    {{ "name": "음식명", "intake": 100 }},
    ...
  ],
  "dinner": [
    {{ "name": "음식명", "intake": 100 }},
    ...
  ]
}}

제약사항:
- intake는 정수 숫자만 사용하고 단위(g)는 제외해주세요.
- 알레르기 및 비선호 재료는 반드시 제외해주세요.
- 반드시 유효한 JSON 객체만 출력하세요. 주석, 설명, 공백 없이 출력해야 합니다.
"""


# ✅ 하루 식단 GPT 요청
def ask_chatgpt_for_day(user: dict, foods: list, date: str) -> dict:
    prompt = build_gpt_prompt_for_day(user, foods, date)
    try:
        response = client.chat.completions.create(
            model="gpt-4",
            messages=[
                {"role": "system", "content": "당신은 건강 식단 전문가입니다."},
                {"role": "user", "content": prompt}
            ],
            temperature=0.7,
            max_tokens=1200
        )
        content = response.choices[0].message.content.strip()
        logging.info(f"✅ GPT 응답 ({date}):\n{content}")
        try:
            day_plan = json.loads(content)
            return sanitize_day_plan(day_plan)  # ✅ 여기서 정제
        except json.JSONDecodeError:
            logging.error(f"⚠️ JSON 파싱 실패 (날짜 {date}): {content}")
            return {}
    except Exception as e:
        logging.error(f"GPT 요청 실패 ({date}): {e}")
        return {}


# ✅ 7일 반복 요청 → 전체 식단 리스트로 반환
def ask_chatgpt_weekly(user: dict, foods: list, start_date: str = None) -> List[Dict]:
    if start_date is None:
        start_date = datetime.today().date()
    else:
        start_date = datetime.strptime(start_date, "%Y-%m-%d").date()

    result = []
    for i in range(7):
        date_str = (start_date + timedelta(days=i)).isoformat()
        day_plan = ask_chatgpt_for_day(user, foods, date_str)

        if day_plan:
            # ✅ 각 식사 항목에 대해 dict 형태 보장
            day_plan["breakfast"] = ensure_dict_format(day_plan.get("breakfast", []))
            day_plan["lunch"] = ensure_dict_format(day_plan.get("lunch", []))
            day_plan["dinner"] = ensure_dict_format(day_plan.get("dinner", []))

            result.append(day_plan)

    return result
