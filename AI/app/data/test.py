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

# 데이터 확인
data = vectordb.get()
print("저장된 문서 개수:", len(data["ids"]))
print("샘플 메타데이터:", data["metadatas"][:2])
print("샘플 문서:", data["documents"][:2])

# 유사도 검색 실행
query = "강릉 관광지"
results = vectordb.similarity_search_with_relevance_scores(
    query, 
    score_threshold=0.7,
    k=5  # 최대 반환 개수
)


# 결과 출력
for idx, (doc, score) in enumerate(results):
    print(f"[{idx+1}] {doc.page_content}\n메타데이터: {doc.metadata}\n유사도: {score:.2f}\n")
