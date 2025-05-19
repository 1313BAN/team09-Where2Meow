from langchain_chroma import Chroma
from langchain_openai import OpenAIEmbeddings
import os
from dotenv import load_dotenv
load_dotenv()  # .env 파일 로드

# 경로 설정
PROJECT_ROOT = os.path.dirname(os.path.abspath(__file__))
CHROMA_DB_PATH = os.path.join(PROJECT_ROOT, "chroma_db")

# 임베딩 함수 생성
embedding_function = OpenAIEmbeddings()

# ChromaDB 연결
vectordb = Chroma(
    persist_directory=CHROMA_DB_PATH,
    embedding_function=embedding_function,
    collection_name="attraction"
)

def rag_attraction(query: str) -> str:
    # 유사도 검색 실행
    query = "강릉 3박 4일 여행스케쥴"
    result_set = vectordb.similarity_search_with_relevance_scores(
        query, 
        score_threshold=0.7,
        k=100  # 최대 반환 개수
    )

    # 결과 출력
    result = ""
    for idx, (doc, score) in enumerate(result_set):
        result += f"[{idx+1}] {doc.page_content}\n메타데이터: {doc.metadata}\n유사도: {score:.2f}\n\n"
    
    return result