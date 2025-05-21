from fastapi import FastAPI
from rag.plan import rag_plan
from AI.app.rag.attraction_search import rag_attraction
from pydantic import BaseModel
from AI.app.generator.generator_rag import gen

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
    # RAG 처리
    rag_result = rag_plan(request.query)
    if not rag_result:
        rag_result = rag_attraction(request.query)
    
    # 생성
    result = gen(request.query, rag_result)
    
    # json 검사
    
    
    response = None
    return response