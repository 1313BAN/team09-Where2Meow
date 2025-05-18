from fastapi import FastAPI
from rag.plan import rag_plan
from rag.attraction import rag_attraction

app = FastAPI()

@app.get("/")
async def root():
    return {"message": "Hello World"}


@app.get("/items/{item_id}")
async def read_item(item_id: int, q: str = ""):
    return {"item_id": item_id, "q": q}

@app.post("/ai/create")
async def create_plan(query: str):
    result = rag_plan(query)
    if not result:
        result = rag_attraction(query)
        
    
    return result