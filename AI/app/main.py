from fastapi import FastAPI
from rag.plan_search import rag_plan
from pydantic import BaseModel
from generator.create_rag import gen, query_to_location
from fastapi import HTTPException
from util.merge_plan import merge_plan
from generator.description import generate_description
from typing import Dict, Any
import json

app = FastAPI()


@app.get("/")
async def root():
    return {"message": "Hello World"}


@app.get("/items/{item_id}")
async def read_item(item_id: int, q: str = ""):
    return {"item_id": item_id, "q": q}


class CreatePlanRequest(BaseModel):
    query: str


@app.post("/ai/create")
async def create_plan(request: CreatePlanRequest):
    try:
        # plan 검색
        result = rag_plan(request.query)

        location = query_to_location(request.query)

        # 생성
        if not result:
            result = gen(request.query + f"여행 장소: {location}", "create")
            if not result:
                raise HTTPException(status_code=500, detail="생성된 결과가 없습니다")

        # 결과 개선
        update_prompt = f"""
        - 이 일정에서 지역이 {location}이 아닌 곳이 있으면 {location} 관광지나 음식점으로 교체합니다.
        - 여행 일정은 아침에 관광지 1곳 방문 후 점심 식사, 점심 식사 이후 오후에 관광지 2곳 방문 후 저녁 식사, 저녁 식사 이후 저녁에 관광지 1곳 방문하는 형태로 작성합니다.
        - 삭제한 장소와 추가한 장소의 개수는 같아야 합니다.
        - 이동 거리가 너무 먼 거리(차로 30분 이상)의 관광지도 교체합니다.
        """
        plan_updates = gen(
            update_prompt, "update", json.dumps(result, ensure_ascii=False)
        )

        if not plan_updates:
            raise HTTPException(status_code=500, detail="계획 업데이트에 실패했습니다")

        # 기존 계획과 병합
        result = merge_plan(result, plan_updates)

        # 설명 생성
        description = generate_description(
            request.query, json.dumps(result, ensure_ascii=False)
        )

        # 결과에 설명 추가
        result["description"] = description

        return result
    except Exception as e:
        raise HTTPException(
            status_code=500, detail=f"계획 생성 중 오류 발생: {str(e)}"
        ) from e


class UpdatePlanRequest(BaseModel):
    query: str
    plan: Dict[str, Any]


@app.post("/ai/update")
async def update_plan(request: UpdatePlanRequest):
    try:
        result = gen(
            request.query, "update", json.dumps(request.plan, ensure_ascii=False)
        )

        # 결과가 없으면 에러 처리
        if not result:
            raise HTTPException(status_code=500, detail="생성된 결과가 없습니다")

        # 기존 계획과 병합
        if request.plan:
            # 새로운 계획과 병합
            result = merge_plan(request.plan, result)

        # 설명 생성
        description = generate_description(
            request.query + "\n\n" + json.dumps(request.plan, ensure_ascii=False),
            json.dumps(result, ensure_ascii=False),
        )

        # 결과에 설명 추가
        result["description"] = description

        return result
    except Exception as e:
        raise HTTPException(
            status_code=500, detail=f"계획 업데이트 중 오류 발생: {str(e)}"
        ) from e
