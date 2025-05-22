from collections import defaultdict
from typing import Dict, Any

def merge_plan(original_data: Dict[str, Any], changes: Dict[str, Any]) -> Dict[str, Any]:
    # 1. 삭제 적용
    delete_keys = {change["uniqueKey"] for change in changes.get("attractions", []) if change.get("mode") == "delete"}
    attractions = [
        attr for attr in original_data.get("attractions", [])
        if attr["uniqueKey"] not in delete_keys
    ]

    # 2. 추가 적용
    existing_keys = {attr["uniqueKey"] for attr in attractions}
    for change in changes.get("attractions", []):
        if change.get("mode") == "add" and change["uniqueKey"] not in existing_keys:
            # mode 필드는 제외하고 추가
            new_attr = {k: v for k, v in change.items() if k != "mode"}
            attractions.append(new_attr)
            existing_keys.add(change["uniqueKey"])

    # 3. 날짜별 attractionOrder 재정렬
    attractions_by_date = defaultdict(list)
    for attr in attractions:
        attractions_by_date[attr["date"]].append(attr)

    result_attractions = []
    for date, attrs in attractions_by_date.items():
        # 기존 attractionOrder 기준 정렬(혹시 순서가 섞여있을 수 있으므로)
        attrs.sort(key=lambda x: x["attractionOrder"])
        for i, attr in enumerate(attrs, start=1):
            attr["attractionOrder"] = i
            result_attractions.append(attr)

    # date별로 묶인 결과를 다시 하나의 리스트로 합침
    # (정렬: date, attractionOrder 순)
    result_attractions.sort(key=lambda x: (x["date"], x["attractionOrder"]))

    return {"attractions": result_attractions}
