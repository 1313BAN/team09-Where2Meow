from langchain_community.vectorstores import FAISS
from langchain_openai import OpenAIEmbeddings
import os
from dotenv import load_dotenv
from app.config import PROJECT_ROOT

load_dotenv()  # .env 파일 로드

# 경로 설정
FAISS_DB_PATH = os.path.join(PROJECT_ROOT, "data/faiss_db")

# 임베딩 함수 생성
embedding_function = OpenAIEmbeddings()

# FAISS DB 불러오기
vectordb = FAISS.load_local(
    folder_path=FAISS_DB_PATH,
    embeddings=embedding_function,
    allow_dangerous_deserialization=True,
)

# 데이터베이스를 검색기로 사용하기 위해 retriever 변수에 할당
retriever = vectordb.as_retriever(
    search_type="mmr", 
    search_kwargs={
        "k": 10, 
        "fetch_k": 20, 
        "lambda_mult": 0.6,
        "score_threshold": 0.8
        }
    )

# 동적 설정
# 검색 설정을 지정. score_threshold 0.8 이상의 점수를 가진 문서만 반환
# config = {
#     "configurable": {
#         "search_type": "similarity_score_threshold",
#         "search_kwargs": {
#             "score_threshold": 0.8,
#         },
#     }
# }

# 관련 문서를 검색
docs = retriever.invoke("강릉 관광지")

for doc in docs:
    print(doc.page_content)
    print("=========================================================")