from sqlalchemy import create_engine, text
from app.data.config import DB_CONFIG

def get_mysql_engine():
    return create_engine(
        f"mysql+pymysql://{DB_CONFIG['user']}:{DB_CONFIG['password']}"
        f"@{DB_CONFIG['host']}:{DB_CONFIG['port']}/{DB_CONFIG['database']}"
    )

def execute_query(query):
    engine = get_mysql_engine()
    with engine.connect() as conn:
        result = conn.execute(text(query))
        return result.fetchall()
