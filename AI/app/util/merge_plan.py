import copy
from collections import defaultdict

def merge_plan(base_data, change_data):
    # 원본 데이터 복사
    result = copy.deepcopy(base_data)
    attractions = result["attractions"]

    # 1. 추가 작업 (add)
    for item in change_data["attractions"]:
        if item.get("mode") == "add":
            # 이미 uniqueKey가 있는지 확인 (중복 방지)
            if not any(a["uniqueKey"] == item["uniqueKey"] for a in attractions):
                # 삽입 위치 결정
                insert_idx = None
                for idx, a in enumerate(attractions):
                    # 같은 날짜, 같은 attractionOrder의 기준 찾기
                    if (
                        a["date"] == item["date"]
                        and abs(a["attractionOrder"] - (item["attractionOrder"] - 0.3))
                        < 1e-6
                    ):
                        insert_idx = idx + 1  # 뒤에 추가
                        break
                    elif (
                        a["date"] == item["date"]
                        and abs(a["attractionOrder"] - (item["attractionOrder"] + 0.3))
                        < 1e-6
                    ):
                        insert_idx = idx  # 앞에 추가
                        break
                # 삽입 위치에 추가, 없으면 끝에 추가
                del item["mode"]
                if insert_idx is not None:
                    attractions.insert(insert_idx, item)
                else:
                    attractions.append(item)

    # 2. 삭제 작업 (delete)
    for item in change_data["attractions"]:
        if item.get("mode") == "delete":
            attractions = [
                a for a in attractions if a["uniqueKey"] != item["uniqueKey"]
            ]

    # 3. 날짜별로 attractionOrder 재정렬
    date_dict = defaultdict(list)
    for a in attractions:
        date_dict[a['date']].append(a)

    new_attractions = []
    for date in sorted(date_dict.keys()):
        # 날짜별로 attractionOrder 기준 정렬
        sorted_list = sorted(date_dict[date], key=lambda x: x['attractionOrder'])
        # 1, 2, 3... 순서로 재할당 및 uniqueKey 재생성
        for i, a in enumerate(sorted_list, 1):
            a['attractionOrder'] = i
            a['uniqueKey'] = f"{date}_{i}_{a['attractionId']}"
            new_attractions.append(a)
    result["attractions"] = new_attractions
    return result
