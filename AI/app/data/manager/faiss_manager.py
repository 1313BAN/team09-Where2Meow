from tqdm import tqdm
from langchain_community.vectorstores import FAISS
from langchain_openai import OpenAIEmbeddings


def save_to_faiss(documents, category, persist_dir="./faiss_db", batch_size=100):
    embeddings = OpenAIEmbeddings()
    vector_store = None
    for i in tqdm(range(0, len(documents), batch_size), desc=category+" FAISS 저장 진행률"):
        batch = documents[i : i + batch_size]
        if vector_store is None:
            vector_store = FAISS.from_documents(documents=batch, embedding=embeddings)
        else:
            vector_store.add_documents(batch)
    if vector_store is not None:
        vector_store.save_local(persist_dir)
        return vector_store
    else:
        print("저장할 문서가 없습니다.")
        return None
