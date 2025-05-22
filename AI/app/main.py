from fastapi import FastAPI
from rag.plan import rag_plan
from pydantic import BaseModel
from generator.create_rag import gen
from fastapi import HTTPException
from util.merge_plan import merge_plan
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

        # 생성
        if not result:
            result = gen(request.query, "create")

        # 결과가 없으면 에러 처리
        if not result:
            raise HTTPException(status_code=500, detail="생성된 결과가 없습니다")

        return result
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"계획 생성 중 오류 발생: {str(e)}") from e

class UpdatePlanRequest(BaseModel):
    query: str
    plan: Dict[str, Any]

@app.post("/ai/update")
async def update_plan(request: UpdatePlanRequest):
    try:
        result = gen(request.query, "update", json.dumps(request.plan))
        print(result)

        # 결과가 없으면 에러 처리
        if not result:
            raise HTTPException(status_code=500, detail="생성된 결과가 없습니다")
        
        # 기존 계획과 병합
        if request.plan:
            # 새로운 계획과 병합
            result = merge_plan(request.plan, result)

        return result
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"계획 업데이트 중 오류 발생: {str(e)}") from e
