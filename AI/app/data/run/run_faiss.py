from app.data.util.mysql_connector import execute_query
from app.data.util.document_converter import convert_to_documents
from app.data.manager.faiss_manager import save_to_faiss
import os
import shutil
from app.config import PROJECT_ROOT

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

FAISS_DB_PATH = os.path.join(PROJECT_ROOT, "database/faiss_db")

def main():
    rows = execute_query(QUERY)
    attractions, restaurants = convert_to_documents(rows)
    vector_store_attraction = save_to_faiss(attractions, "관광지", FAISS_DB_PATH+"/attraction")
    vector_store_restaurant = save_to_faiss(restaurants, "음식점", FAISS_DB_PATH+"/restaurant")
    print(f"✅ 성공적으로 {len(attractions)}개 관광지 저장 완료")
    print(f"✅ 성공적으로 {len(restaurants)}개 음식점 저장 완료")

if __name__ == "__main__":
    if os.path.exists(FAISS_DB_PATH):
        shutil.rmtree(FAISS_DB_PATH)
        print(f"{FAISS_DB_PATH} 폴더가 삭제되었습니다.")
    else:
        print(f"{FAISS_DB_PATH} 폴더가 존재하지 않습니다.")
    main()
