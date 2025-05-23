from tqdm import tqdm
from langchain.schema import Document


def convert_to_documents(rows):
    attraction_documents = []
    restaurant_documents = []
    for row in tqdm(rows, desc="문서 생성 진행률"):
        if row is None:
            continue
        # 공통 page_content와 metadata 생성
        page_content = f"""
            attraction_id: {row.attraction_id}
            attraction_name: {row.attraction_name}
            category_id: {row.attraction_category_id}
            category_name: {row.attraction_category_name}
            latitude: {float(row.latitude) if row.latitude else None}
            longitude: {float(row.longitude) if row.longitude else None}
            addr1: {row.addr1 or ''}
            addr2: {row.addr2 or ''}
            """
        metadata = {
            "state_code": row.state_code,
            "city_code": row.city_code,
            "addr1": row.addr1 or "",
            "addr2": row.addr2 or "",
            "overview": row.overview,
            "tel": row.tel or "정보 없음",
            "homepage": row.homepage or "정보 없음",
        }

        # 카테고리에 따라 적절한 리스트에 추가
        if row.attraction_category_id == 39:
            restaurant_documents.append(
                Document(page_content=page_content, metadata=metadata)
            )
        else:
            attraction_documents.append(
                Document(page_content=page_content, metadata=metadata)
            )
    return attraction_documents, restaurant_documents


from data.util.mysql_connector import execute_query

if __name__ == "__main__":
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
    rows = execute_query(QUERY)
    attractions, restaurants = convert_to_documents(rows)
    print(f"✅ 성공적으로 {len(attractions)}개 관광지 문서 생성 완료")
    print(f"✅ 성공적으로 {len(restaurants)}개 음식점 문서 생성 완료")
    print(f"✅ 관광지 문서 예시: {attractions[0]}")
    print(f"✅ 음식점 문서 예시: {restaurants[0]}")
