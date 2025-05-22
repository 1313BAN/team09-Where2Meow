from langchain_openai import ChatOpenAI
from langchain_core.prompts import ChatPromptTemplate
import os
from dotenv import load_dotenv
from app.rag.attraction_search import search_attraction
import json

load_dotenv()
# print(f"OPENAI_API_KEY from env: {os.environ.get('OPENAI_API_KEY')}")

itinerary_template = """
[사용자 요청] 
{query}

[관광지 정보] 
{attractions}

[여행 일정 생성 시 참고 사항]
{notice}
"""

create_notice = """
- 사용자 요청에 맞는 여행 일정을 생성합니다.
- 여행지는 관광지 정보에서 제공된 관광지들 중 category_id가 39가 아닌 정보에서만 선택합니다.
- 점심, 저녁 식사 장소를 포함하여 하루 일정을 구성합니다.
  - 점심, 저녁 식사 장소는 관광지 정보에서 제공된 관광지들 중 category_id가 39인 정보에서만 선택합니다.
  - 식사 시간은 1시간을 가정하여 작성합니다.
- 여행 일정은 사용자가 요청한 날짜를 기준으로 작성합니다.
- 여행 일정은 아침에 관광지 1곳 방문 후 점심 식사, 오후에 관광지 2곳 방문 후 저녁 식사, 저녁에 관광지 1곳 방문하는 형태로 작성합니다.
- 여행 일정은 이동시간을 고려하여 작성합니다.
- 사용자가 선호하는 여행 스타일을 바탕으로 여행일정을 작성합니다.
  - 사용자가 선호하는 여행 스타일이 없을 경우 보통의 사람들이 선호하는 여행 스타일을 바탕으로 여행일정을 작성합니다.
- 관광지와 식당의 중복은 최대한 피합니다.
"""

update_notice = """
"""

common_notice = """
출력문 공통 사항
1. 출력은 json 형식으로 작성합니다.
2. 예시에 제시된 json 형식에 맞춰서 작성합니다.
3. 다른 말은 제외하고 json만 작성합니다.

예시:
{
  "attractions": [
    {
      "uniqueKey": "2025-05-15_1_56644",
      "attractionId": 56644,
      "attractionName": "가회동성당",
      "categoryId": 12,
      "categoryName": "관광지",
      "date": "2025-05-15",
      "attractionOrder": 1
    },
    {
      "uniqueKey": "2025-05-15_2_56651",
      "attractionId": 56651,
      "attractionName": "강변스파랜드",
      "categoryId": 12,
      "categoryName": "관광지",
      "date": "2025-05-15",
      "attractionOrder": 2
    },
    {
      "uniqueKey": "2025-05-16_1_56644",
      "attractionId": 56644,
      "attractionName": "가회동성당",
      "categoryId": 12,
      "categoryName": "관광지",
      "date": "2025-05-16",
      "attractionOrder": 1
    },
  ]
}
"""


def gen(query: str, attractions: list, restaurants: str, method: str) -> str:
    llm = ChatOpenAI(model="gpt-4.1-mini", max_completion_tokens=2000, temperature=0.3)
    prompt = ChatPromptTemplate.from_template(itinerary_template)
    
    # attractions가 문자열인지 리스트인지 확인하고 적절히 처리
    try:
        if isinstance(attractions, list):
            if all(isinstance(item, dict) for item in attractions):
                # 딕셔너리 리스트인 경우 JSON 문자열로 변환
                attractions_str = json.dumps(attractions, ensure_ascii=False)
            else:
                # 문자열 리스트인 경우 조인
                attractions_str = ", ".join(attractions)
        else:
            # 이미 문자열인 경우 그대로 사용
            attractions_str = attractions
    except Exception as e:
        print(f"attractions 데이터 처리 중 오류 발생: {e}")
        attractions_str = str(attractions)

    # 프롬프트에 query와 attractions를 올바르게 삽입
    if method == "create":
        formatted_prompt = prompt.format(
            query=query,
            attractions=attractions_str,
            notice=create_notice + common_notice,
        )
    elif method == "update":
        formatted_prompt = prompt.format(
            query=query,
            attractions=attractions_str,
            notice=update_notice + common_notice,
        )
    response = llm.invoke(formatted_prompt).content
    return response

if __name__ == "__main__":
    # 테스트용 쿼리
    test_query = "강릉 3박 4일 여행스케줄을 짜줘"
    test_attraction_data = search_attraction(test_query)
    print("rag 완료")
    test_attraction_data = """
    [
        {
            "attraction_id": 60925,
            "attraction_name": "강릉향교",
            "category_id": 12,
            "category_name": "관광지",
            "latitude": 37.7635561721,
            "longitude": 128.8952470467,
            "addr1": "강원특별자치도 강릉시 명륜로 29",
            "addr2": "(교동)",
        },
        {
            "attraction_id": 68007,
            "attraction_name": "금릉경포대",
            "category_id": 12,
            "category_name": "관광지",
            "latitude": 34.7456277023,
            "longitude": 126.7091359422,
            "addr1": "전라남도 강진군 성전면 백운로 146-1",
            "addr2": "",
        },
        {
            "attraction_id": 60893,
            "attraction_name": "강릉 경포대",
            "category_id": 12,
            "category_name": "관광지",
            "latitude": 37.7955691591,
            "longitude": 128.8965126086,
            "addr1": "강원특별자치도 강릉시 경포로 365",
            "addr2": "(저동)",
        },
        {
            "attraction_id": 100484,
            "attraction_name": "명가왕족발 강릉",
            "category_id": 39,
            "category_name": "관광지",
            "latitude": 37.7601542366,
            "longitude": 128.9182811884,
            "addr1": "강원특별자치도 강릉시 월대산로 71",
            "addr2": "",
        },
        {
            "attraction_id": 98699,
            "attraction_name": "왕릉일가",
            "category_id": 39,
            "category_name": "관광지",
            "latitude": 37.6254181782,
            "longitude": 126.89794059,
            "addr1": "경기도 고양시 덕양구 서오릉로 334-91",
            "addr2": "",
        },
        {
            "attraction_id": 60901,
            "attraction_name": "강릉 명주동 거리",
            "category_id": 12,
            "category_name": "관광지",
            "latitude": 37.7511381142,
            "longitude": 128.8927254362,
            "addr1": "강원특별자치도 강릉시 명주동",
            "addr2": "",
        },
        {
            "attraction_id": 100085,
            "attraction_name": "강릉가자",
            "category_id": 39,
            "category_name": "관광지",
            "latitude": 37.7544822152,
            "longitude": 128.8949251342,
            "addr1": "강원특별자치도 강릉시 문화의길 3",
            "addr2": "",
        },
        {
            "attraction_id": 60908,
            "attraction_name": "강릉 안목해맞이공원",
            "category_id": 12,
            "category_name": "관광지",
            "latitude": 37.7743308995,
            "longitude": 128.9451477738,
            "addr1": "강원특별자치도 강릉시 견소동",
            "addr2": "287-6",
        },
        {
            "attraction_id": 60911,
            "attraction_name": "강릉 월화거리",
            "category_id": 12,
            "category_name": "관광지",
            "latitude": 37.7559015445,
            "longitude": 128.8974470859,
            "addr1": "강원특별자치도 강릉시 금성로11번길 9",
            "addr2": "",
        },
        {
            "attraction_id": 60892,
            "attraction_name": "강릉 3.1운동 기념공원",
            "category_id": 12,
            "category_name": "관광지",
            "latitude": 37.7972995865,
            "longitude": 128.9002177764,
            "addr1": "강원특별자치도 강릉시 저동",
            "addr2": "645",
        },
        {
            "attraction_id": 68905,
            "attraction_name": "전남 강진다원",
            "category_id": 12,
            "category_name": "관광지",
            "latitude": 34.7409031836,
            "longitude": 126.7061613195,
            "addr1": "전라남도 강진군 성전면 백운로 93-25",
            "addr2": "",
        },
        {
            "attraction_id": 100066,
            "attraction_name": "갈매기다방",
            "category_id": 39,
            "category_name": "관광지",
            "latitude": 37.870373343,
            "longitude": 128.8430040037,
            "addr1": "강원특별자치도 강릉시 연곡면 영진길 63",
            "addr2": "",
        },
        {
            "attraction_id": 100180,
            "attraction_name": "교동반점",
            "category_id": 39,
            "category_name": "관광지",
            "latitude": 37.758353952,
            "longitude": 128.8930029659,
            "addr1": "강원특별자치도 강릉시 강릉대로 205",
            "addr2": "(교동)",
        },
        {
            "attraction_id": 60905,
            "attraction_name": "강릉 선교장",
            "category_id": 12,
            "category_name": "관광지",
            "latitude": 37.7865588677,
            "longitude": 128.8850778068,
            "addr1": "강원특별자치도 강릉시 운정길 63",
            "addr2": "(운정동)",
        },
        {
            "attraction_id": 61239,
            "attraction_name": "등명낙가사(강릉)",
            "category_id": 12,
            "category_name": "관광지",
            "latitude": 37.7116837926,
            "longitude": 129.0068052775,
            "addr1": "강원특별자치도 강릉시 강동면 괘방산길 16",
            "addr2": "",
        },
        {
            "attraction_id": 60909,
            "attraction_name": "강릉 오금집",
            "category_id": 12,
            "category_name": "관광지",
            "latitude": 37.7563708685,
            "longitude": 128.8940418219,
            "addr1": "강원특별자치도 강릉시 강릉대로210번길 23-9 (임당동)",
            "addr2": "",
        },
        {
            "attraction_id": 61861,
            "attraction_name": "응봉산(강원)",
            "category_id": 12,
            "category_name": "관광지",
            "latitude": 37.0872205462,
            "longitude": 129.2001737646,
            "addr1": "강원특별자치도 삼척시 가곡면 풍곡리",
            "addr2": ", 경북 봉화군, 울진군",
        },
        {
            "attraction_id": 104590,
            "attraction_name": "우장군 고현점",
            "category_id": 39,
            "category_name": "관광지",
            "latitude": 34.8905028656,
            "longitude": 128.6204093383,
            "addr1": "경상남도 거제시 거제중앙로31길 16 (고현동)",
            "addr2": "1층",
        },
        {
            "attraction_id": 61152,
            "attraction_name": "대굴령마을",
            "category_id": 12,
            "category_name": "관광지",
            "latitude": 37.7229426427,
            "longitude": 128.8150638568,
            "addr1": "강원특별자치도 강릉시 성산면 성연로 17",
            "addr2": "",
        },
        {
            "attraction_id": 68487,
            "attraction_name": "성기동국민관광지",
            "category_id": 12,
            "category_name": "관광지",
            "latitude": 34.7554114459,
            "longitude": 126.6326202249,
            "addr1": "전라남도 영암군 군서면 왕인로 440",
            "addr2": "",
        },
        {
            "attraction_id": 60896,
            "attraction_name": "강릉 남산공원",
            "category_id": 12,
            "category_name": "관광지",
            "latitude": 37.7480551515,
            "longitude": 128.8934282457,
            "addr1": "강원특별자치도 강릉시 노암동",
            "addr2": "740-1",
        },
        {
            "attraction_id": 100090,
            "attraction_name": "강릉독도네꼬막본점",
            "category_id": 39,
            "category_name": "관광지",
            "latitude": 37.7593428738,
            "longitude": 128.9005689199,
            "addr1": "강원특별자치도 강릉시 옥천로 47",
            "addr2": "(옥천동)",
        },
        {
            "attraction_id": 60899,
            "attraction_name": "강릉 대기리마을",
            "category_id": 12,
            "category_name": "관광지",
            "latitude": 37.5883185884,
            "longitude": 128.7776435739,
            "addr1": "강원특별자치도 강릉시 왕산면 왕산로 1327",
            "addr2": "",
        },
        {
            "attraction_id": 96212,
            "attraction_name": "강원도해장국",
            "category_id": 39,
            "category_name": "관광지",
            "latitude": 35.5804277409,
            "longitude": 129.3445495574,
            "addr1": "울산광역시 중구 북부순환도로 864",
            "addr2": "",
        },
        {
            "attraction_id": 60917,
            "attraction_name": "강릉 해운정",
            "category_id": 12,
            "category_name": "관광지",
            "latitude": 37.7862090263,
            "longitude": 128.8911356052,
            "addr1": "강원특별자치도 강릉시 운정길 125",
            "addr2": "",
        },
        {
            "attraction_id": 60900,
            "attraction_name": "강릉 대도호부 관아",
            "category_id": 12,
            "category_name": "관광지",
            "latitude": 37.7532767444,
            "longitude": 128.8921226461,
            "addr1": "강원특별자치도 강릉시 임영로131번길 6",
            "addr2": "",
        },
        {
            "attraction_id": 60924,
            "attraction_name": "강릉항",
            "category_id": 12,
            "category_name": "관광지",
            "latitude": 37.7714035891,
            "longitude": 128.9517472703,
            "addr1": "강원특별자치도 강릉시 창해로14번길 55-11",
            "addr2": "",
        },
        {
            "attraction_id": 71328,
            "attraction_name": "강릉문화원",
            "category_id": 14,
            "category_name": "문화시설",
            "latitude": 37.7587674189,
            "longitude": 128.8738046819,
            "addr1": "강원특별자치도 강릉시 하슬라로 96",
            "addr2": "(교동)",
        },
        {
            "attraction_id": 61136,
            "attraction_name": "단경골 휴양지",
            "category_id": 12,
            "category_name": "관광지",
            "latitude": 37.6724226769,
            "longitude": 128.9164479523,
            "addr1": "강원특별자치도 강릉시 강동면 단경로 841",
            "addr2": "(강동면)",
        },
        {
            "attraction_id": 64148,
            "attraction_name": "경주 헌강왕릉",
            "category_id": 12,
            "category_name": "관광지",
            "latitude": 35.8021369114,
            "longitude": 129.240327382,
            "addr1": "경상북도 경주시 새남산길 62",
            "addr2": "",
        },
    ]
"""
    result = gen(test_query, test_attraction_data, "", "create")
    print(result)
