from mysql_connector import execute_query
from document_converter import convert_to_documents
from chroma_manager import save_to_chroma
import os
import shutil

# 프로젝트 루트 설정
PROJECT_ROOT = os.path.dirname(os.path.abspath(__file__))

QUERY = """
    SELECT 
        a.attraction_id,
        a.attraction_name,
        a.attraction_category_id,
        ac.attraction_category_name,
        a.addr1,
        a.addr2,
        a.latitude,
        a.longitude,
        a.tel,
        a.homepage,
        a.overview,
        a.state_code,
        a.city_code
    FROM attraction a
    JOIN attraction_category ac 
        ON a.attraction_category_id = ac.attraction_category_id
"""

CHROMA_DB_PATH = os.path.join(PROJECT_ROOT, "chroma_db")

def main():
    rows = execute_query(QUERY)
    documents = convert_to_documents(rows)
    vector_store = save_to_chroma(documents, CHROMA_DB_PATH)
    print(f"✅ 성공적으로 {len(documents)}개 문서 저장 완료")

if __name__ == "__main__":
    if os.path.exists(CHROMA_DB_PATH):
        shutil.rmtree(CHROMA_DB_PATH)
        print(f"{CHROMA_DB_PATH} 폴더가 삭제되었습니다.")
    else:
        print(f"{CHROMA_DB_PATH} 폴더가 존재하지 않습니다.")
    main()
