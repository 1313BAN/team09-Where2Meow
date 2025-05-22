from fastapi import FastAPI
from app.rag.plan import rag_plan
from pydantic import BaseModel
from app.generator.create_rag import gen

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
        result = gen(request.query, "create")

        # json 검사
    
    response = result
    return response