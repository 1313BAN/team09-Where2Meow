from tqdm import tqdm
from langchain_chroma import Chroma
from langchain_openai import OpenAIEmbeddings


def save_to_chroma(
    documents, persist_dir="./chroma_db", collection_name="attraction", batch_size=100):
    try:
        embeddings = OpenAIEmbeddings()
        vector_store = None
        for i in tqdm(range(0, len(documents), batch_size), desc="Chroma 저장 진행률"):
            batch = documents[i : i + batch_size]
            try:
                if vector_store is None:
                    vector_store = Chroma.from_documents(
                        documents=batch,
                        embedding=embeddings,
                        persist_directory=persist_dir,
                        collection_name=collection_name,
                    )
                else:
                    vector_store.add_documents(batch)
            except Exception as e:
                print(f"배치 처리 중 오류 발생 (인덱스 {i}-{i+batch_size}): {e}")
                if vector_store:
                    vector_store.persist()
                raise
        vector_store.persist()
        return vector_store
    except Exception as e:
        print(f"Chroma 저장 중 오류 발생: {e}")
        raise


# from tqdm import tqdm
# from langchain_chroma import Chroma
# from langchain_openai import OpenAIEmbeddings
# from langchain_community.document_transformers import EmbeddingsRedundantFilter

# def save_to_chroma(documents, persist_dir="./chroma_db", batch_size=100):
#     embeddings = OpenAIEmbeddings()
#     redundant_filter = EmbeddingsRedundantFilter(embeddings=embeddings, similarity_threshold=0.95)  # threshold 조정 가능
#     vector_store = None
#     for i in tqdm(range(0, len(documents), batch_size), desc="Chroma 저장 진행률"):
#         batch = documents[i:i+batch_size]
#         # 중복 문서 필터링
#         filtered_batch = list(redundant_filter.transform_documents(batch))
#         if vector_store is None:
#             vector_store = Chroma.from_documents(
#                 documents=filtered_batch,
#                 embedding=embeddings,
#                 persist_directory=persist_dir,
#                 collection_name="attraction"
#             )
#         else:
#             vector_store.add_documents(filtered_batch)
#     return vector_store
