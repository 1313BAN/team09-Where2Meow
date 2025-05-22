from tqdm import tqdm
from langchain_community.vectorstores import FAISS
from langchain_openai import OpenAIEmbeddings


def save_to_faiss(documents, category, persist_dir="./faiss_db", batch_size=100):
    # 입력 매개변수 검증
    if documents is None:
        print("오류: documents 매개변수가 None입니다.")
        return None

    if not isinstance(documents, list):
        print("오류: documents 매개변수는 리스트 형식이어야 합니다.")
        return None

    if len(documents) == 0:
        print("저장할 문서가 없습니다.")
        return None

    if not isinstance(category, str) or not category:
        print("오류: category 매개변수는 비어있지 않은 문자열이어야 합니다.")
        return None

    if batch_size <= 0:
        print("오류: batch_size는 양수여야 합니다.")
        return None

    try:
        embeddings = OpenAIEmbeddings()
    except Exception as e:
        print(f"OpenAI 임베딩 초기화 오류: {e}")
        return None

    # FAISS 벡터 스토어 생성
    vector_store = None
    try:
        for i in tqdm(range(0, len(documents), batch_size), desc=category+" FAISS 저장 진행률"):
            batch = documents[i : i + batch_size]
            if vector_store is None:
                vector_store = FAISS.from_documents(documents=batch, embedding=embeddings)
            else:
                vector_store.add_documents(batch)
    except Exception as e:
        print(f"FAISS 문서 추가 중 오류 발생: {e}")
        return None
    
    # FAISS 벡터 스토어 저장
    if vector_store is not None:
        try:
            vector_store.save_local(persist_dir)
            print(f"✅ FAISS 데이터베이스를 {persist_dir}에 성공적으로 저장했습니다.")
        except Exception as e:
            print(f"FAISS 저장 중 오류 발생: {e}")
            return None
        return vector_store
    else:
        print("저장할 문서가 없습니다.")
        return None
