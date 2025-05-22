from langchain.community.vectorstores import FAISS
from langchain_openai import OpenAIEmbeddings, ChatOpenAI
import json
import os
from dotenv import load_dotenv
from app.config import PROJECT_ROOT

load_dotenv()  # .env 파일 로드

# 경로 설정
FAISS_DB_PATH = os.path.join(PROJECT_ROOT, "database/faiss_plan_db")

# 임베딩 함수 생성
embedding_function = OpenAIEmbeddings()

# FAISS DB 불러오기
vectordb = FAISS.load_local(
    folder_path=FAISS_DB_PATH,
    embeddings=embedding_function,
    allow_dangerous_deserialization=True,
)

retriever = vectordb.as_retriever(
    search_type="similarity", 
    search_kwargs={
        "k": 30, 
        "lambda_mult": 0.6,
        "score_threshold": 0.8
        }
    )

def rag_plan(query: str) -> str:
    return ""