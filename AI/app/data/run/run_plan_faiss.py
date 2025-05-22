from app.data.util.mysql_connector import execute_query
from app.data.util.plan_converter import convert_to_documents
from app.data.manager.faiss_manager import save_to_faiss
import os
import shutil
from app.config import PROJECT_ROOT

QUERY = """
    SELECT 
        p.plan_id,
        p.user_id,
        p.title,
        p.content,
        p.created_at,
        p.updated_at,
        p.start_date,
        p.end_date,
        p.view_count,
        p.is_public,

        COALESCE(like_counts.like_count, 0) as like_count,
        COALESCE(bookmark_counts.bookmark_count, 0) as bookmark_count,

        pa.attraction_id,
        pa.content as pa_content,
        pa.date as pa_date,
        pa.attraction_order as pa_attraction_order,

        a.attraction_name,
        a.addr1,
        a.addr2,
        a.attraction_category_id as category_id,
        ac.attraction_category_name as category_name,
        a.latitude,
        a.longitude

    FROM plan p
    
    LEFT JOIN (
        SELECT
            plan_id,
            COUNT(*) as like_count
        FROM plan_like
        GROUP BY plan_id
    ) like_counts ON p.plan_id = like_counts.plan_id

    LEFT JOIN (
        SELECT
            plan_id,
            COUNT(*) as bookmark_count
        FROM plan_bookmark
        GROUP BY plan_id
    ) bookmark_counts ON p.plan_id = bookmark_counts.plan_id

    LEFT JOIN plan_attraction pa ON p.plan_id = pa.plan_id

    LEFT JOIN attraction a ON pa.attraction_id = a.attraction_id

    LEFT JOIN attraction_category ac ON a.attraction_category_id = ac.attraction_category_id
    
    WHERE p.is_public = 1

    ORDER BY p.plan_id, pa.date, pa.attraction_order
"""

FAISS_DB_PATH = os.path.join(PROJECT_ROOT, "database/faiss_db")

def main():
    rows = execute_query(QUERY)
    plans = convert_to_documents(rows)
    vector_store_plan = save_to_faiss(plans, "여행 일정", FAISS_DB_PATH+"/plan")
    print(f"✅ 성공적으로 {len(plans)}개 관광지 저장 완료")
    print(plans[0])


if __name__=="__main__":
    # if os.path.exists(FAISS_DB_PATH):
    #     shutil.rmtree(FAISS_DB_PATH)
    #     print(f"{FAISS_DB_PATH} 폴더가 삭제되었습니다.")
    # else:
    #     print(f"{FAISS_DB_PATH} 폴더가 존재하지 않습니다.")
    main()