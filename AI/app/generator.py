from langchain_openai import ChatOpenAI
from langchain_core.prompts import ChatPromptTemplate
from typing import Dict, Union
import os
from dotenv import load_dotenv
load_dotenv()
# print(f"OPENAI_API_KEY from env: {os.environ.get('OPENAI_API_KEY')}")

itinerary_template = """
[사용자 요청] {query}
[관광지 정보] {attractions}

1. 3박4일 일정으로 각 날짜별 테마 설정
2. 이동 시간 고려한 관광지 배치(오전/오후/저녁)
3. 카테고리별 균형 잡힌 구성(역사, 자연, 음식 등)
4. 현지 교통 상황 반영
5. 휴식 시간 포함
"""

def generate_itinerary(query: str, attractions: list) -> str:
    llm = ChatOpenAI(
        model="gpt-4.1-mini",
        max_completion_tokens=2000,
        temperature=0.3
    )
    prompt = ChatPromptTemplate.from_template(itinerary_template)
    attractions_str = ", ".join(attractions)
    response = llm.invoke(f"query: {query}, attractions: {attractions_str}").content
    # response = llm.invoke(f"test").content
    return str(response)

def gen(query: str, rag_data: str) -> Dict[str, Union[str, list]]:
    """반환 타입을 명시적으로 지정"""
    try:
        attractions = [
            line.split("attraction_name:")[1].strip()
            for block in rag_data.split('\n\n')
            for line in block.split('\n')
            if "attraction_name:" in line
        ][:10]
        return {
            "status": "success",
            "data": generate_itinerary(query, attractions),
            "attractions": attractions  # 디버깅용
        }
    except Exception as e:
        return {"status": "error", "message": str(e)}
    
if __name__ == "__main__":
    # 테스트용 쿼리
    test_query = "강릉 3박 4일 여행스케쥴"
    test_rag_data = """
    [1] attraction_name: 강릉 경포대
    attraction_category_name: 자연
    attraction_id: 1
    attraction_category_id: 1
    addr1: 강원도 강릉시 경포로 365
    addr2: 경포대 해수욕장
    latitude: 37.7749
    longitude: 128.8767
    tel: 033-123-4567
    homepage: www.gyeongpodae.com
    overview: 아름다운 바다와 경치를 자랑하는 관광지입니다.
    
    [2] attraction_name: 강릉 주문진 수산시장
    attraction_category_name: 음식
    attraction_id: 2
    attraction_category_id: 2
    addr1: 강원도 강릉시 주문진읍 해안로 1234
    addr2: 주문진 해변 근처
    latitude: 37.7510
    longitude: 128.8910
    tel: 033-987-6543
    homepage: www.jumunjinmarket.com
    overview: 신선한 해산물을 맛볼 수 있는 시장입니다.
    
    [3] attraction_name: 강릉 오죽헌
    attraction_category_name: 역사
    attraction_id: 3
    attraction_category_id: 3
    addr1: 강원도 강릉시 오죽헌길 24
    addr2: 오죽헌
    latitude: 37.7590
    longitude: 128.9030
    tel: 033-456-7890
    homepage: www.ojukheon.com
    overview: 율곡 이이가 태어난 곳으로 역사적인 가치가 높은 장소입니다.
    """

    result = gen(test_query, test_rag_data)
    print(result)
