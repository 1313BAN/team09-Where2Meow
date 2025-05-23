from langchain_community.vectorstores import FAISS
from langchain_openai import OpenAIEmbeddings, ChatOpenAI
import json
import os
import re
import random
from dotenv import load_dotenv
from config import PROJECT_ROOT

load_dotenv()  # .env 파일 로드

# 경로 설정
FAISS_DB_PATH = os.path.join(PROJECT_ROOT, "database/faiss_db/plan")

# 임베딩 함수 생성
embedding_function = OpenAIEmbeddings()

# FAISS DB 불러오기
try:
    vectordb = FAISS.load_local(
        folder_path=FAISS_DB_PATH,
        embeddings=embedding_function,
        allow_dangerous_deserialization=True,
    )
    
    retriever = vectordb.as_retriever(
        search_type="similarity", 
        search_kwargs={
            "k": 30, # FAISS 벡터 데이터베이스에서 가장 유사한 상위 30개의 여행 일정을 가져옴 
            "lambda_mult": 0.6, # 높을수록 유사도 중시 
            "score_threshold": 0.8  # 유사도 점수 임계값
        }
    )
    print("✅ Plan FAISS DB 로드 완료")
except Exception as e:
    print(f"⚠️ Plan FAISS DB 로드 실패: {e}")
    retriever = None

def parse_doc_to_dict(doc_content: str) -> dict:
    """
    doc_content에서 각 라인을 파싱하여 딕셔너리로 변환
    """
    result = {}
    for line in doc_content.splitlines():
        line = line.strip()
        if not line:
            continue
        if ':' in line:
            key, value = line.split(':', 1)
            key = key.strip()
            value = value.strip()
            
            # 숫자 변환 시도
            if key in ["plan_id", "user_id", "view_count", "like_count", "bookmark_count", "duration_days", "created_month"]:
                try:
                    value = int(value)
                except:
                    pass
            elif key in ["popularity_score", "engagement_score", "completeness_score"]:
                try:
                    value = float(value)
                except:
                    pass
            elif key == "is_public":
                value = value == "1" or value.lower() == "true"
            elif key == "attractions":
                try:
                    value = json.loads(value)
                except:
                    pass
            
            result[key] = value
    return result

def parse_travel_query(query: str) -> dict:
    """
    여행 쿼리에서 위치, 날짜, 기간 등의 정보를 추출
    """
    llm = ChatOpenAI(
        model="gpt-4o-mini",
        max_completion_tokens=1000,
        temperature=0.1
    )
    
    prompt = f"""
다음 여행 관련 질문에서 정보를 추출해서 JSON 형태로 반환해주세요.

질문: "{query}"

추출할 정보:
1. location: 여행 지역/도시 (예: "부산", "제주도", "서울 강남구")
2. duration_type: 기간 타입 ("days", "nights", "weeks", "months", "unknown")
3. duration_number: 기간 숫자 (예: 2박3일이면 3, 1주일이면 7)
4. travel_style: 여행 스타일 (예: "가족여행", "혼자여행", "커플여행", "친구여행", "비즈니스")
5. season: 계절 정보가 있다면 (예: "봄", "여름", "가을", "겨울")
6. specific_date: 구체적인 날짜가 있다면 (예: "2024-06-01")

JSON 형식으로만 답변하고, 다른 설명은 하지 마세요.
정보가 없는 경우 null로 표시하세요.

예시:
{{
  "location": "부산",
  "duration_type": "days", 
  "duration_number": 3,
  "travel_style": "가족여행",
  "season": null,
  "specific_date": null
}}
    """
    
    try:
        response = llm.invoke(prompt).content.strip()
        
        # JSON 파싱 시도
        parsed_info = json.loads(response)
        
        # 기본값 설정
        default_info = {
            "location": None,
            "duration_type": "unknown",
            "duration_number": None,
            "travel_style": None,
            "season": None,
            "specific_date": None
        }
        
        # 기본값과 병합 (LLM 결과를 우선시)
        for key in default_info:
            if key not in parsed_info or parsed_info[key] is None:
                # LLM에서 추출하지 못한 경우에만 정규식 결과 사용
                if key != "location":  # 지역은 LLM 결과만 사용
                    regex_result = extract_basic_info_regex(query)
                    if regex_result.get(key) is not None:
                        parsed_info[key] = regex_result[key]
                    else:
                        parsed_info[key] = default_info[key]
                else:
                    parsed_info[key] = default_info[key]
        
        return parsed_info
        
    except json.JSONDecodeError:
        print(f"JSON 파싱 실패: {response}")
        # 폴백: 정규식으로 기간/스타일만 추출, 지역은 None
        regex_result = extract_basic_info_regex(query)
        return {
            "location": None,  # LLM 실패 시 지역은 추출하지 않음
            "duration_type": regex_result.get("duration_type", "unknown"),
            "duration_number": regex_result.get("duration_number"),
            "travel_style": regex_result.get("travel_style"),
            "season": regex_result.get("season"),
            "specific_date": None
        }
    except Exception as e:
        print(f"쿼리 파싱 오류: {e}")
        # 완전 실패 시에도 지역은 None
        regex_result = extract_basic_info_regex(query)
        return {
            "location": None,
            "duration_type": regex_result.get("duration_type", "unknown"),
            "duration_number": regex_result.get("duration_number"),
            "travel_style": regex_result.get("travel_style"),
            "season": regex_result.get("season"),
            "specific_date": None
        }

def extract_basic_info_regex(query: str) -> dict:
    """
    정규식을 사용한 폴백 정보 추출
    """
    result = {
        "location": None,  # LLM에서 추출된 값을 우선 사용
        "duration_type": "unknown",
        "duration_number": None,
        "travel_style": None,
        "season": None,
        "specific_date": None
    }
    
    # 기간 추출 (N박M일, N일, N박 등)
    duration_patterns = [
        r'(\d+)박\s*(\d+)일',  # 2박3일
        r'(\d+)박',            # 2박
        r'(\d+)일',            # 3일  
        r'(\d+)주',            # 1주
        r'(\d+)개월'           # 1개월
    ]
    
    for pattern in duration_patterns:
        match = re.search(pattern, query)
        if match:
            if '박' in pattern and '일' in pattern:
                # N박M일 패턴
                result["duration_number"] = int(match.group(2))  # 일수 사용
                result["duration_type"] = "days"
            elif '박' in pattern:
                # N박 패턴 → N+1일
                result["duration_number"] = int(match.group(1)) + 1
                result["duration_type"] = "days"
            elif '일' in pattern:
                result["duration_number"] = int(match.group(1))
                result["duration_type"] = "days"
            elif '주' in pattern:
                result["duration_number"] = int(match.group(1)) * 7
                result["duration_type"] = "days"
            elif '개월' in pattern:
                result["duration_number"] = int(match.group(1)) * 30
                result["duration_type"] = "days"
            break
    
    # 여행 스타일 추출
    if "가족" in query:
        result["travel_style"] = "가족여행"
    elif "혼자" in query or "솔로" in query:
        result["travel_style"] = "혼자여행"
    elif "커플" in query or "연인" in query:
        result["travel_style"] = "커플여행"
    elif "친구" in query:
        result["travel_style"] = "친구여행"
    
    # 계절 추출
    if "봄" in query:
        result["season"] = "봄"
    elif "여름" in query:
        result["season"] = "여름"
    elif "가을" in query:
        result["season"] = "가을"
    elif "겨울" in query:
        result["season"] = "겨울"
    
    return result

def build_search_query(info: dict, original_query: str) -> str:
    """
    추출된 정보를 바탕으로 검색 쿼리 구성
    """
    query_parts = []
    
    # 지역 정보
    if info.get("location"):
        query_parts.append(f"{info['location']} 여행")
    
    # 기간 정보
    if info.get("duration_number"):
        duration = info["duration_number"]
        if duration <= 1:
            query_parts.append("당일치기")
        elif duration <= 3:
            query_parts.append("단기 여행")
        elif duration <= 7:
            query_parts.append("일주일 여행")
        else:
            query_parts.append("장기 여행")
    
    # 여행 스타일
    if info.get("travel_style"):
        query_parts.append(info["travel_style"])
    
    # 계절 정보
    if info.get("season"):
        query_parts.append(f"{info['season']} 여행")
    
    # 쿼리 부분이 없으면 원본 쿼리 사용
    if not query_parts:
        return original_query
    
    # 검색 쿼리 조합
    search_query = " ".join(query_parts) + "와 관련성이 높은 여행 계획을 추천해줘"
    
    return search_query

def filter_plans_by_info(search_results: list, info: dict) -> list:
    """
    추출된 정보를 바탕으로 검색 결과 필터링
    """
    if not search_results:  # None 체크 추가
        return []
        
    filtered_results = []
    
    for doc in search_results:
        try:
            # 지역 필터링
            location_match = True
            if info.get("location"):
                location_tags = doc.metadata.get('location_tags', [])
                if location_tags is None:  # None 체크
                    location_tags = []
                    
                content = doc.page_content.lower() if doc.page_content else ""
                location_lower = info["location"].lower()
                
                # metadata 태그나 content에서 지역 매치 확인
                location_match = any(location_lower in tag.lower() for tag in location_tags) or location_lower in content
            
            # 기간 필터링 (±2일 허용)
            duration_match = True
            if info.get("duration_number"):
                plan_duration = doc.metadata.get('duration_days', 1)
                if plan_duration is None:  # None 체크
                    plan_duration = 1
                expected_duration = info["duration_number"]
                duration_match = abs(plan_duration - expected_duration) <= 2
            
            # 계절 필터링
            season_match = True
            if info.get("season"):
                plan_season = doc.metadata.get('season', '')
                if plan_season is None:  # None 체크
                    plan_season = ''
                season_match = info["season"] == plan_season
            
            # 모든 조건을 만족하는 경우만 포함
            if location_match and duration_match and season_match:
                filtered_results.append(doc)
                
        except Exception as e:
            print(f"필터링 오류: {e}")
            continue
    
    return filtered_results

def get_top_plans_by_popularity(search_results: list, top_n: int = 5) -> list:
    """
    검색 결과에서 인기도 순으로 상위 N개 추출
    """
    if not search_results:  # None 체크 추가
        return []
        
    plans_with_scores = []
    
    for doc in search_results:
        try:
            # metadata에서 인기도 점수 추출
            popularity_score = doc.metadata.get('popularity_score', 0) if doc.metadata else 0
            engagement_score = doc.metadata.get('engagement_score', 0) if doc.metadata else 0
            completeness_score = doc.metadata.get('completeness_score', 0) if doc.metadata else 0
            
            # None 체크
            if popularity_score is None:
                popularity_score = 0
            if engagement_score is None:
                engagement_score = 0
            if completeness_score is None:
                completeness_score = 0
            
            # 종합 점수 계산 (인기도 70% + 참여도 20% + 완성도 10%)
            total_score = (popularity_score * 0.7) + (engagement_score * 0.2) + (completeness_score * 0.1)
            
            plans_with_scores.append({
                'doc': doc,
                'score': total_score
            })
        except Exception as e:
            print(f"점수 계산 오류: {e}")
            continue
    
    # 점수 순으로 정렬하고 상위 N개 추출
    plans_with_scores.sort(key=lambda x: x['score'], reverse=True)
    top_plans = [item['doc'] for item in plans_with_scores[:top_n]]
    
    return top_plans

def select_random_plan(top_plans: list) -> dict:
    """
    상위 계획들 중에서 랜덤으로 하나 선택
    """
    if not top_plans:
        return None
    
    # 가중치를 둔 랜덤 선택 (상위 계획일수록 높은 확률)
    weights = [1.0 / (i + 1) for i in range(len(top_plans))]  # 1, 0.5, 0.33, 0.25, 0.2...
    selected_doc = random.choices(top_plans, weights=weights, k=1)[0]
    
    # Document를 딕셔너리로 변환
    plan_dict = parse_doc_to_dict(selected_doc.page_content)
    
    # metadata 정보도 추가
    plan_dict['metadata'] = selected_doc.metadata
    
    return plan_dict

def format_plan_response(plan_dict: dict) -> dict:  # str → dict로 변경
    """
    선택된 계획을 응답 형식으로 포맷팅
    """
    if not plan_dict:
        return {}  
    
    try:
        # 기본 정보 추출
        title = plan_dict.get('title', '제목 없음')
        content = plan_dict.get('content', '')
        start_date = plan_dict.get('start_date', '')
        end_date = plan_dict.get('end_date', '')
        attractions = plan_dict.get('attractions', [])
        
        # None 체크
        if attractions is None:
            attractions = []
        
        # 메타데이터 정보
        metadata = plan_dict.get('metadata', {})
        if metadata is None:
            metadata = {}
            
        duration_days = metadata.get('duration_days', 1)
        popularity_score = metadata.get('popularity_score', 0)
        location_tags = metadata.get('location_tags', [])
        
        # None 체크
        if duration_days is None:
            duration_days = 1
        if popularity_score is None:
            popularity_score = 0
        if location_tags is None:
            location_tags = []
        
        # attractions 형식 통일 (addr1 + addr2 → addr)
        formatted_attractions = []
        for attraction in attractions:
            formatted_attraction = {
                "uniqueKey": attraction.get("uniqueKey"),
                "attractionId": attraction.get("attractionId"),
                "attractionName": attraction.get("attractionName"),
                "addr": f"{attraction.get('addr1', '')} {attraction.get('addr2', '')}".strip() if attraction.get('addr1') or attraction.get('addr2') else attraction.get('addr', ''),
                "categoryId": attraction.get("categoryId"),
                "categoryName": attraction.get("categoryName"),
                "date": attraction.get("date"),
                "attractionOrder": attraction.get("attractionOrder")
            }
            formatted_attractions.append(formatted_attraction)
        
        # 응답 구성 - 딕셔너리로 반환
        response = {
            "attractions": formatted_attractions  
        }
        
        return response  
        
    except Exception as e:
        print(f"응답 포맷팅 오류: {e}")
        return {}  

def rag_plan(query: str) -> dict:  
    """
    RAG를 사용하여 관련된 인기 여행 계획 중 랜덤으로 하나 반환
    """
    if not retriever:
        print("⚠️ FAISS DB가 로드되지 않았습니다.")
        return {} 
    
    try:
        print(f"🔍 여행 계획 검색 중: {query}")
        
        # 1. 쿼리에서 상세 정보 추출
        info = parse_travel_query(query)
        print(f"📊 추출된 정보: {json.dumps(info, ensure_ascii=False)}")
        
        # 2. 추출된 정보로 검색 쿼리 구성
        search_query = build_search_query(info, query)
        print(f"🔎 검색 쿼리: {search_query}")
        
        # 3. 유사도 검색 실행
        search_results = retriever.invoke(search_query)
        
        if not search_results or len(search_results) == 0:  # None과 빈 리스트 모두 체크
            print("❌ 관련된 여행 계획을 찾을 수 없습니다.")
            return {}  
        
        print(f"📊 {len(search_results)}개의 유사 계획 발견")
        
        # 4. 추출된 정보로 필터링
        filtered_results = filter_plans_by_info(search_results, info)
        print(f"🎯 정보 필터링 후: {len(filtered_results)}개 계획")
        
        # 필터링 후 결과가 없으면 빈 딕셔너리 반환
        if not filtered_results:
            print("❌ 조건에 맞는 여행 계획이 없습니다.")
            return {}  
        
        # 5. 인기도 순으로 상위 N개 추출
        top_plans = get_top_plans_by_popularity(filtered_results, top_n=5)
        
        if not top_plans:
            print("❌ 인기도 계산 실패")
            return {} 
        
        print(f"🏆 상위 {len(top_plans)}개 인기 계획 선별")
        
        # 6. 상위 계획들 중 랜덤 선택
        selected_plan = select_random_plan(top_plans)
        
        if not selected_plan:
            print("❌ 계획 선택 실패")
            return {} 
        
        print(f"🎲 랜덤 선택된 계획: {selected_plan.get('title', '제목 없음')}")
        
        # 7. 응답 형식으로 포맷팅
        response = format_plan_response(selected_plan)
        
        if response:
            print("✅ 여행 계획 반환 성공")
            return response  # 딕셔너리 반환
        else:
            print("❌ 응답 포맷팅 실패")
            return {}  
        
    except Exception as e:
        print(f"❌ rag_plan 오류: {e}")
        return {}  

if __name__=="__main__":
    # 테스트용 쿼리 
    test_queries = [
        "강릉 여행 계획 추천해줘.",
        "부산 2박 3일 여행 일정",
        "강릉 가족여행 코스",
        "제주도 혼자 여행"
    ]

    for query in test_queries:
        print(f"\n" + "="*50)
        print(f"테스트 질문: {query}")
        print("="*50)

        plan = rag_plan(query)
        if plan:
            print(f"결과:\n{plan}")
        else:
            print("❌ 관련 계획 없음 - 새로운 계획 생성 필요")