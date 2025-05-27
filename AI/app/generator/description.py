from langchain_openai import ChatOpenAI

def generate_description(query: str, result: str) -> str:

    llm = ChatOpenAI(model="gpt-4o-mini", max_completion_tokens=2000, temperature=0.3)

    prompt = f"""
    [사용자 요청]
    {query}

    [요청 처리 결과]
    {result}

    - 사용자 요청에 대한 처리 결과가 저렇게 나왔는데, 이 결과에 대한 설명을 간단하게 해줘.
    - 사용자의 요청에 따라.. 라는 말은 안해도 돼.
    - 제공된 결과는 이라는 말도 안해도 돼.
    - 바로 처음부터 설명해줘.
    - 너가 이 요청 처리 결과를 생성한 것처럼 설명해줘.
    - 사용자의 요청이 수정이 경우 수정된 사항만 설명하면 돼.
    - 포맷은 마크다운 형식으로 해줘.
    - 설명은 200자 이내로 해줘.
    - 설명은 간단하게 해줘.
    - 설명은 사용자에게 친근하게 작성해줘.
    - 말 끝에 냥 같은 말을 붙여줘.
    - 이 여행의 컨셉은 어떤지, 왜 이런 결과가 나왔는지, 어떤 점을 고려했는지 등을 설명해줘.
    - 개조식으로 간단하게 설명한 뒤, 이후 친근한 설명을 해줘.
    - 좌우로 짧기 때문에 단락도 나누고 가독성이 좋게 작성해줘.
    - 
    """
    
    print("설명 생성중...")
    response = llm.invoke(prompt).content
    print("설명 생성 완료")

    # Placeholder for the actual implementation
    return response