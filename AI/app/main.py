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
    # plan 검색
    result = rag_plan(request.query)

    # 생성
    if not result:
        print("저장된 여행 계획을 찾지 못해서 여행 계획을 생성합니다.")
        result = gen(request.query, "create")

    response = result
    return response