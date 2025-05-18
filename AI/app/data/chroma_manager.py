from tqdm import tqdm
from langchain_chroma import Chroma
from langchain_openai import OpenAIEmbeddings


def save_to_chroma(documents, persist_dir="./chroma_db", batch_size=100):
    embeddings = OpenAIEmbeddings()
    vector_store = None
    for i in tqdm(range(0, len(documents), batch_size), desc="Chroma 저장 진행률"):
        batch = documents[i:i+batch_size]
        if vector_store is None:
            vector_store = Chroma.from_documents(
                documents=batch,
                embedding=embeddings,
                persist_directory=persist_dir,
                collection_name="attraction"
            )
        else:
            vector_store.add_documents(batch)
    return vector_store
