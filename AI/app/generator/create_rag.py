from langchain_openai import ChatOpenAI
from langchain.chains import RetrievalQA
import json
from dotenv import load_dotenv
from rag.attraction_rag import retriever
from validation.json_validation import validate_json

load_dotenv()

create_notice = """
- 사용자 요청에 맞는 여행 일정을 생성합니다.
- 반드시 사용자가 요청한 지역내에서 관광지와 음식점을 선택합니다.
  - 필요시 근처 가까운 지역(차로 30분 이내)은 추가가 가능합니다.
- 점심, 저녁 식사 장소를 포함하여 하루 일정을 구성합니다.
  - 점심, 저녁 식사 장소는 restaurans db에서 검색한 음식점 정보에서 제공된 관광지들 중 category_id가 39인 정보에서만 선택합니다.
  - 식사 시간은 1시간을 가정하여 작성합니다.
- 여행 일정은 사용자가 요청한 날짜를 기준으로 작성합니다.
- 여행 일정은 아침에 관광지 1곳 방문 후 점심 식사, 점심 식사 이후 오후에 관광지 2곳 방문 후 저녁 식사, 저녁 식사 이후 저녁에 관광지 1곳 방문하는 형태로 작성합니다.
- 반드시 관광지는 attrantion db에서 검색한 관광지 정보에서 제공된 관광지들 중 category_id가 39가 아닌 정보에서만 선택합니다.
- 여행 일정은 이동시간을 고려하여 작성합니다.
- 사용자가 선호하는 여행 스타일을 바탕으로 여행일정을 작성합니다.
  - 사용자가 선호하는 여행 스타일이 없을 경우 보통의 사람들이 선호하는 여행 스타일을 바탕으로 여행일정을 작성합니다.
- 관광지와 식당의 중복은 최대한 피합니다.

예시:
{
  "attractions": [
    {
      "uniqueKey": "2025-05-15_1_56644",
      "attractionId": 56644,
      "attractionName": "가회동성당",
      "addr": "서울특별시 종로구 북촌로 57 (가회동)",
      "lat": 37.5772,
      "lng": 127.0002,
      "categoryId": 12,
      "categoryName": "관광지",
      "date": "2025-05-15",
      "attractionOrder": 1,
      "content": ""
    },
    {
      "uniqueKey": "2025-05-15_2_56651",
      "attractionId": 56651,
      "attractionName": "강변스파랜드",
      "addr": "서울특별시 광진구 구의강변로 45 (구의동)",
      "lat": 37.5461,
      "lng": 127.0861,
      "categoryId": 12,
      "categoryName": "관광지",
      "date": "2025-05-15",
      "attractionOrder": 2,
      "content": ""
    },
    {
      "uniqueKey": "2025-05-16_1_56664",
      "attractionId": 56664,
      "attractionName": "경복궁",
      "addr": "서울특별시 종로구 사직로 161 (세종로)",
      "categoryId": 12,
      "categoryName": "관광지",
      "date": "2025-05-16",
      "attractionOrder": 1,
      "content": ""
    },
  ]
}
"""
update_notice = """
- 사용자가 요청한 여행 일정에 추가/삭제를 적용합니다.
- 반드시 기존 일정을 분석 후 사용자의 요구사항을 반영합니다.
- 추가 요청은 반드시 사용자가 요청한 일정에 포함됨 지역내에서 관광지와 음식점을 선택합니다.
  - 필요시 근처 가까운 지역은 추가가 가능합니다.
- 사용자가 수정 요청을 한 관광지 외의 관광지와 음식점은 기존 여행 일정에서 삭제하지 않습니다.
- 점심, 저녁 식사 장소를 포함하여 하루 일정을 구성합니다.
  - 점심, 저녁 식사 장소는 restaurans db에서 검색한 음식점 정보에서 제공된 관광지들 중 category_id가 39인 정보에서만 선택합니다.
  - 식사 시간은 1시간을 가정하여 작성합니다.
- 일정 삽입 시, 삽입 위치는 삽입 기준 일정의 앞이면 -0.3, 뒤면 +0.3로 설정합니다.
- 관광지와 식당의 중복은 최대한 피합니다.
- 삭제 요청은 반드시 사용자의 일정에 포함된 관광지와 음식점의 uniqueKey만 삭제 가능합니다.

예시:
{
    "attractions": [
        {
            "mode": "add",
            "uniqueKey": 사용자 일정에 맞는 유니크 키,
            "attractionId": 60896,
            "attractionName": "강릉 남산공원",
            "addr": "강원특별자치도 강릉시 노암동",
            "lat": 37.7551,
            "lng": 128.8761,
            "categoryId": 12,
            "categoryName": "관광지",
            "date": 사용자 요구에 맞는 날짜,
            "attractionOrder": 4.3,
            "content": ""
        },
        {
            "mode": "add",
            "uniqueKey": 사용자 일정에 맞는 유니크 키,
            "attractionId": 60896,
            "attractionName": "강릉 대기리마을",
            "addr": "강원특별자치도 강릉시 강릉대로 33",
            "lat": 37.5883,
            "lng": 128.7776,
            "categoryId": 12,
            "categoryName": "관광지",
            "date": 사용자 요구에 맞는 날짜,
            "attractionOrder": 0.7,
            "content": ""
        },
        {
            "mode": "delete",
            "uniqueKey": 사용자가 삭제 요청한 관광지의 유니크 키,
        }
    ]
}
"""

common_notice = """
출력문 공통 사항
1. 출력은 json 형식으로 작성합니다.
2. 반드시 예시에 제시된 json 형식에 맞춰서 작성합니다.
3. 다른 말은 제외하고 json만 작성합니다. (코드 블럭 사용금지)
4. 출력문은 반드시 포맷팅 해서 작성합니다.
5. query는 포함하지 않고 바로 attractions만 작성합니다.
"""


def make_prompt_template(query: str, notice: str, plan: str = "") -> str:
    return f"""
        [사용자 요청] 
        {query}

        {"[기존 일정]\n"+plan if len(plan) > 0 else ""}

        [여행 일정 생성 시 참고 사항]
        {notice}
    """

def query_to_location(query: str) -> str:
    llm = ChatOpenAI(model="gpt-4o-mini", max_completion_tokens=2000, temperature=0.3)
    response = llm.invoke(
        f"'{query}' 이 질문에 대한 위치 정보를 제공해줘. 예시: '서울특별시 강남구'. 반드시 지역명만 출력해줘"
    ).content
    return response


def gen(
    query: str, method: str, plan: str = "", retry_count: int = 0, max_retries: int = 10
) -> object:
    llm = ChatOpenAI(model="gpt-4.1-mini", max_completion_tokens=2000, temperature=0.3)
    # RetrievalQA 체인 생성
    qa_chain = RetrievalQA.from_chain_type(
        llm=llm, chain_type="stuff", retriever=retriever  # 혹은 "map_reduce" 등 선택
    )
    # 쿼리만 넘기면 자동으로 벡터DB에서 검색 후 LLM에 컨텍스트로 전달
    response = ""
    if method == "create":
        print("여행 계획 생성중...")
        prompt_template = make_prompt_template(query, create_notice + common_notice)
        # print(prompt_template)
        response = qa_chain.invoke(prompt_template)["result"]
        if not validate_json(response, "create"):
            # JSON 유효성 검사 통과 실패
            print("JSON 유효성 검사 실패. 재생성합니다...")
            if retry_count >= max_retries:
                raise ValueError(
                    f"JSON 생성 실패: 최대 재시도 횟수({max_retries})를 초과했습니다."
                )
            return gen(query, method, retry_count + 1, max_retries)
        # print(response)
    elif method == "update":
        print("여행 계획 업데이트 리소스 생성중...")
        prompt_template = make_prompt_template(query, update_notice + common_notice, plan)
        # print(prompt_template)
        response = qa_chain.invoke(prompt_template)["result"]
        if not validate_json(response, "update"):
            # JSON 유효성 검사 통과 실패
            print("JSON 유효성 검사 실패. 재생성합니다...")
            if retry_count >= max_retries:
                raise ValueError(
                    f"JSON 생성 실패: 최대 재시도 횟수({max_retries})를 초과했습니다."
                )
            return gen(query, method, plan, retry_count + 1, max_retries)

    # JSON 유효성 검사 통과
    print("JSON 유효성 검사 성공")

    print("생성 완료")
    print(response)

    return json.loads(response)


import time

if __name__ == "__main__":
    start = time.time()  # 시작 시간 기록

    # 테스트용 쿼리
    test_query = "강릉 2박 3일 여행스케줄을 짜줘"
    result = gen(test_query, "create")
    print(result)

    end = time.time()  # 끝난 시간 기록
    print(f"\n실행 시간: {end - start:.4f}초")
