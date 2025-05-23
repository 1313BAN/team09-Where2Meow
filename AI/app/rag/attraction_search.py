from langchain_community.vectorstores import FAISS
from langchain_openai import OpenAIEmbeddings, ChatOpenAI
import json
import os
from dotenv import load_dotenv
from config import PROJECT_ROOT

load_dotenv()  # .env 파일 로드

# 경로 설정
FAISS_DB_PATH = os.path.join(PROJECT_ROOT, "data/faiss_db")

# 임베딩 함수 생성
embedding_function = OpenAIEmbeddings()

try:
    vectordb = FAISS.load_local(
        folder_path=FAISS_DB_PATH,
        embeddings=embedding_function,
        allow_dangerous_deserialization=True,  # 주의: 신뢰할 수 있는 소스에서만 True로 설정
    )

    retriever = vectordb.as_retriever(
        search_type="similarity",
        search_kwargs={"k": 30, "lambda_mult": 0.6, "score_threshold": 0.8},
    )
except Exception as e:
    raise RuntimeError(f"FAISS 벡터 데이터베이스 로드 실패: {e}") from e


def parse_doc_to_dict(doc_content: str) -> dict:
    """
    doc_content에서 각 라인을 파싱하여 딕셔너리로 변환
    """
    result = {}
    for line in doc_content.splitlines():
        line = line.strip()
        if not line:
            continue
        if ":" in line:
            key, value = line.split(":", 1)
            key = key.strip()
            value = value.strip()
            # 숫자 변환 시도
            if key in ["attraction_id", "category_id"]:
                try:
                    value = int(value)
                except:
                    pass
            elif key in ["latitude", "longitude"]:
                try:
                    value = float(value)
                except:
                    pass
            result[key] = value
    return result


def query_to_location(query: str) -> str:
    llm = ChatOpenAI(model="gpt-4.1-mini", max_completion_tokens=2000, temperature=0.3)
    response = llm.invoke(
        f"'{query}' 이 질문에 대한 위치 정보를 제공해줘. 예시: '서울특별시 강남구'"
    ).content
    return response


def search_attraction(query: str) -> str:
    try:
        # 쿼리에서 위치 정보 추출
        location = query_to_location(query)

        # attraction 유사도 검색 실행
        attraction_result = retriever.invoke(
            location + "과(와) 근처 하루에 갈 수 있을만한 관광지를 추천해줘."
        )

        # 각 결과를 딕셔너리로 파싱
        attraction_dict_list = []
        for doc in attraction_result:
            doc_dict = parse_doc_to_dict(doc.page_content)
            attraction_dict_list.append(doc_dict)

        # JSON 형식의 문자열로 변환(한글, 들여쓰기, key순서유지)
        attraction_json = json.dumps(attraction_dict_list, ensure_ascii=False, indent=2)

        # # restaurant 유사도 검색
        # restaurant_result = retriever.invoke(location + " 근처 식당을 추천해줘. 반드시 category_id가 39인 것들만 추천해줘.")

        # # 각 결과를 딕셔너리로 파싱
        # restourant_dict_list = []
        # for doc in restaurant_result:
        #     doc_dict = parse_doc_to_dict(doc.page_content)
        #     restourant_dict_list.append(doc_dict)

        # # JSON 형식의 문자열로 변환(한글, 들여쓰기, key순서유지)
        # restourant_json = json.dumps(restourant_dict_list, ensure_ascii=False, indent=2)

        return attraction_json
    except Exception as e:
        error_message = f"관광지 검색 중 오류 발생: {e}"
        print(error_message)
        return json.dumps({"error": error_message}, ensure_ascii=False, indent=2)


if __name__ == "__main__":
    # 테스트용 쿼리
    test_query = "강릉 관광지"
    attractions = search_attraction(test_query)
    print(attractions)
    # print(restaurants)
