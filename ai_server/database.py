from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker, declarative_base

# 환경변수로부터 DB 접속 정보 읽기
POSTGRES_DB = "mes_dev"
POSTGRES_USER = "mes_user"
POSTGRES_PASSWORD = "mes1234"
POSTGRES_HOST = "postgres"
POSTGRES_PORT = "5432"

DATABASE_URL = f"postgresql://{POSTGRES_USER}:{POSTGRES_PASSWORD}@{POSTGRES_HOST}:{POSTGRES_PORT}/{POSTGRES_DB}"

# SQLAlchemy 엔진과 세션 생성
engine = create_engine(DATABASE_URL)
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)

# 베이스 클래스 정의
Base = declarative_base()

# 요청 단위 세션 의존성
# DB 연결 시 세션을 생성하여 연결, 요청 처리가 완료되면 세션 종료
# DB 연결이 필요할 때마다 get_db() 함수를 호출하여 사용
def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()