from tqdm import tqdm
from langchain.schema import Document
from collections import defaultdict
import json

def convert_to_documents(rows):
    plan_documents = []
    plan_dict = defaultdict(list)

    for row in tqdm(rows, desc="문서 생성 진행률"):
        if row is None:
            continue

        # 여행지 정보 하나 생성
        if hasattr(row, 'attraction_id') and row.attraction_id:
            unique_key = f"{row.pa_date}_{row.pa_attraction_order}_{row.attraction_id}"
            attraction_info = {
                "uniqueKey": unique_key,
                "attractionId": row.attraction_id,
                "attractionName": row.attraction_name,
                "addr1": row.addr1 or "",
                "addr2": row.addr2 or "",
                "categoryId": row.category_id,
                "categoryName": row.category_name,
                "date": str(row.pa_date),
                "attractionOrder": row.pa_attraction_order
            }
            plan_dict[row.plan_id].append((row, attraction_info))
        else:
            # 여행지 없는 일정도 있을 수 있음
            plan_dict[row.plan_id].append((row, None))

    for plan_id, items in plan_dict.items():
        base_row = items[0][0]
        attractions = [info for _, info in items if info]

        # JSON 형태로 변환하여 저장
        page_content = f"""plan_id: {base_row.plan_id}
user_id: {base_row.user_id}
title: {base_row.title}
content: {base_row.content}
created_at: {base_row.created_at}
updated_at: {base_row.updated_at}
start_date: {base_row.start_date}
end_date: {base_row.end_date}
view_count: {base_row.view_count}
is_public: {base_row.is_public}
like_count: {base_row.like_count}
bookmark_count: {base_row.bookmark_count}
attractions: {json.dumps(attractions, ensure_ascii=False)}"""

        # 지역 태그 추출 (관광지 주소에서)
        location_tags = []
        for attraction in attractions:
            addr1 = attraction.get('addr1', '')
            addr2 = attraction.get('addr2', '')
            full_addr = f"{addr1} {addr2}".strip()
            if full_addr:
                addr_parts = full_addr.split()
                if addr_parts:
                    location_tags.extend(addr_parts[:2])  # 시/도, 시/군/구
        location_tags = list(set(location_tags))  # 중복 제거

        # 여행 일수 계산
        duration_days = 1
        if base_row.start_date and base_row.end_date:
            duration_days = (base_row.end_date - base_row.start_date).days + 1

        # 인기도 점수 계산 (like * 2 + bookmark * 3 + view * 0.1)
        popularity_score = (base_row.like_count * 2) + (base_row.bookmark_count * 3) + (base_row.view_count * 0.1)
        
        # 참여도 점수 계산
        total_engagement = base_row.like_count + base_row.bookmark_count + base_row.view_count
        engagement_score = total_engagement / max(1, base_row.view_count) if base_row.view_count > 0 else 0

        # 계절 추출 (start_date 기준)
        season = "기타"
        if base_row.start_date:
            month = base_row.start_date.month
            if month in [3, 4, 5]:
                season = "봄"
            elif month in [6, 7, 8]:
                season = "여름"
            elif month in [9, 10, 11]:
                season = "가을"
            elif month in [12, 1, 2]:
                season = "겨울"

        # 식당 포함 여부
        has_restaurants = any(attraction.get('categoryId') == 39 for attraction in attractions)

        # 완성도 점수 (제목, 내용, 관광지 유무)
        completeness_score = 0
        if base_row.title: completeness_score += 0.3
        if base_row.content: completeness_score += 0.3
        if attractions: completeness_score += 0.4

        # 생성 월 (최신성)
        created_month = base_row.created_at.month if base_row.created_at else 1

        metadata = {            
            # 인기도 관련
            "popularity_score": popularity_score,
            "engagement_score": engagement_score,
            
            # 검색 필터링용
            "location_tags": location_tags,
            "duration_days": duration_days,
            "season": season,
            "has_restaurants": has_restaurants,
            
            # 품질 지표
            "completeness_score": completeness_score,
            "created_month": created_month,
        }

        plan_documents.append(Document(page_content=page_content, metadata=metadata))

    return plan_documents

from data.util.mysql_connector import execute_query

if __name__=="__main__":
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

    rows = execute_query(QUERY)
    plans = convert_to_documents(rows)
    
    print(f"✅ 성공적으로 {len(plans)}개 여행 일정 문서 생성 완료")
    if plans:
        print(f"✅ 여행 일정 예시: {plans[0]}")
    else:
        print("⚠️ 생성된 여행 일정이 없습니다.")