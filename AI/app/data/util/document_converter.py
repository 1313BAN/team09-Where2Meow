from tqdm import tqdm
from langchain.schema import Document

def convert_to_documents(rows):
    attraction_documents = []
    restaurant_documents = []
    for row in tqdm(rows, desc="문서 생성 진행률"):
        if row is None:
            continue
        if row.attraction_id == 39:
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
                "addr1": row.addr1 or '',
                "addr2": row.addr2 or '',
                "overview": row.overview,
                "tel": row.tel or '정보 없음',
                "homepage": row.homepage or '정보 없음'
            }
            restaurant_documents.append(Document(page_content=page_content, metadata=metadata))
        else:
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
                "addr1": row.addr1 or '',
                "addr2": row.addr2 or '',
                "overview": row.overview,
                "tel": row.tel or '정보 없음',
                "homepage": row.homepage or '정보 없음'
            }
            attraction_documents.append(Document(page_content=page_content, metadata=metadata))
    return attraction_documents, restaurant_documents
