# faiss 에서는 안됨. chroma 에서만 가능

from langchain_community.vectorstores import FAISS
from langchain_openai import OpenAIEmbeddings
from langchain.chains.query_constructor.schema import AttributeInfo
from langchain.retrievers.self_query.base import SelfQueryRetriever
from langchain_openai import ChatOpenAI
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

# 메타데이터 필드 정보 생성
metadata_field_info = [
    AttributeInfo(
        name="addr1",
        description="Address line 1 of the attraction",
        type="string",
    ),
    AttributeInfo(
        name="addr2",
        description="The district (dong) information of the Attraction",
        type="String",
    ),
    AttributeInfo(
        name="overview",
        description="Overview of the attraction",
        type="String",
    ),
]

# LLM 정의
llm = ChatOpenAI(model="gpt-4o-mini", temperature=0)

# SelfQueryRetriever 생성
retriever = SelfQueryRetriever.from_llm(
    llm=llm,
    vectorstore=vectordb,
    document_contents="Brief summary of a cosmetic product",
    metadata_field_info=metadata_field_info,
    enable_limit=True,  # 검색 결과 제한 기능을 활성화합니다.
    search_kwargs={"k": 5},  # k 의 값을 2로 지정하여 검색 결과를 2개로 제한합니다.
)

# Self-query 검색
retriever.invoke("강릉 여행지 추천해줘")