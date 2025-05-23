from langchain_community.vectorstores import FAISS
from langchain.retrievers import EnsembleRetriever  # ★ 클래스 변경
from langchain_openai import OpenAIEmbeddings
from dotenv import load_dotenv
from config import PROJECT_ROOT
import os

load_dotenv()

# 경로 설정
FAISS_DB_PATH = os.path.join(PROJECT_ROOT, "database/faiss_db/")

embedding_function = OpenAIEmbeddings()

# 1. 벡터스토어 로드 (기존 코드 유지)
attraction_faiss = FAISS.load_local(
    folder_path=FAISS_DB_PATH + "attraction",
    embeddings=embedding_function,
    allow_dangerous_deserialization=True,
)

restaurant_faiss = FAISS.load_local(
    folder_path=FAISS_DB_PATH + "restaurant",
    embeddings=embedding_function,
    allow_dangerous_deserialization=True,
)

# 2. 개별 검색기 생성
attraction_retriever = attraction_faiss.as_retriever(search_kwargs={"k": 64})
restaurant_retriever = restaurant_faiss.as_retriever(search_kwargs={"k": 32})

# 3. 앙상블 검색기로 변경 ★ 핵심 수정 부분
retriever = EnsembleRetriever(
    retrievers=[attraction_retriever, restaurant_retriever],
    weights=[0.6, 0.4]  # 관광지:60%, 식당:40% 가중치
)
