from langchain_community.vectorstores import FAISS
from langchain_openai import OpenAIEmbeddings
import os
from dotenv import load_dotenv
from app.config import PROJECT_ROOT

load_dotenv()

FAISS_DB_PATH = os.path.join(PROJECT_ROOT, "data/faiss_db")
embedding_function = OpenAIEmbeddings()
vectordb = FAISS.load_local(
    folder_path=FAISS_DB_PATH,
    embeddings=embedding_function,
    allow_dangerous_deserialization=True,
)
retriever = vectordb.as_retriever(
    search_type="similarity",
    search_kwargs={"k": 30, "lambda_mult": 0.6, "score_threshold": 0.8}
)
