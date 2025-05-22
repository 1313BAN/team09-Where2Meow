from fastapi import FastAPI
from rag.plan import rag_plan
from pydantic import BaseModel
from generator.create_rag import gen

app = FastAPI()


@app.get("/")
async def root():
    return {"message": "Hello World"}


@app.get("/items/{item_id}")
async def read_item(item_id: int, q: str = ""):
    return {"item_id": item_id, "q": q}


class QueryRequest(BaseModel):
    query: str


@app.post("/ai/create")
async def create_plan(request: QueryRequest):
    try:
        # plan 검색
        result = rag_plan(request.query)

        # 생성
        if not result:
            result = gen(request.query, "create")

        # 결과가 없으면 에러 처리
        if not result:
            return {"error": "생성된 결과가 없습니다"}, 500

        return result, 200
    except Exception as e:
        return {"error": f"계획 생성 중 오류 발생: {str(e)}"}, 500
