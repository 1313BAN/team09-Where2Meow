from sqlalchemy import create_engine, text
from data.config import DB_CONFIG

def get_mysql_engine():
    return create_engine(
        f"mysql+pymysql://{DB_CONFIG['user']}:{DB_CONFIG['password']}"
        f"@{DB_CONFIG['host']}:{DB_CONFIG['port']}/{DB_CONFIG['database']}"
    )

def execute_query(query):
    engine = get_mysql_engine()
    try:
        with engine.connect() as conn:
            result = conn.execute(text(query))
            return result.fetchall()
    except Exception as e:
        print(f"쿼리 실행 중 오류 발생: {e}")
        raise
