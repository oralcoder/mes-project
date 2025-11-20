from fastapi import FastAPI, Body, Form, Request, Depends, UploadFile, File
import joblib
import pandas as pd
import tensorflow as tf
from tensorflow import keras
from fastapi.middleware.cors import CORSMiddleware
import requests
from database import Base, engine
from vector import Vector
from sqlalchemy import text
from sqlalchemy.orm import Session
from sqlalchemy.exc import SQLAlchemyError
from database import get_db
import tempfile
from embedder import embed_text, embed_pdf_to_vectors

app = FastAPI()

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # 개발용, 프로덕션에서는 특정 도메인만
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

@app.on_event("startup")
def startup_event():
    Base.metadata.create_all(bind=engine)
    setup_global_ai_assets()
    

def setup_global_ai_assets():
    try:
        app.state.ai_models = {
            "rf_on_time_model": joblib.load("ai_models/rf_on_time_model.pkl"),
            "dnn_on_time_model": keras.models.load_model("ai_models/dnn_on_time_model.keras"),
            "dnn_on_time_scaler": joblib.load("ai_models/dnn_on_time_scaler.pkl"),
        }
        print("AI 리소스 로드 완료")
    except Exception as e:
        print(f"AI 리소스 로드 실패: {e}")


@app.get("/")
def root():
		# 해당 요청(http://localhost:8000/)에 대해 JSON 형식의 응답을 반환
    return {"message": "AI Server is running"}

@app.post("/is_on_time")
def predict_is_on_time(data: dict = Body(...)):
    print("Received data for is_on_time prediction:", data)
 
    model = app.state.ai_models["rf_on_time_model"]
    
    df = pd.DataFrame([{
        "product_id": data.get("productId"),
        "quantity": data.get("quantity"),
        "orderDate": data.get("orderDate"),
        "dueDate": data.get("dueDate"),
    }])
    # 예측에 사용할 특징 선택
    df['month'] = pd.to_datetime(df['orderDate']).dt.month
    df['day_of_week'] = pd.to_datetime(df['orderDate']).dt.dayofweek
    df['days_to_due_minutes'] = (pd.to_datetime(df['dueDate']) - pd.to_datetime(df['orderDate'])).dt.total_seconds() / 60.0
    
    features = df[['product_id', 'quantity', 'month', 'day_of_week', 'days_to_due_minutes']]
    # 예측 수행
    prediction = model.predict(features)
    is_on_time = int(prediction[0] == 1)
    print(df['days_to_due_minutes'])
    print(f"Predicted is_on_time: {is_on_time}")   
    
    return {"is_on_time": is_on_time}

''' DNN
@app.post("/is_on_time")
def predict_is_on_time(data: dict = Body(...)):
    print("Received data for is_on_time prediction:", data)
 
    model = app.state.ai_models["dnn_on_time_model"]
    scaler = app.state.ai_models["dnn_on_time_scaler"]
    
    df = pd.DataFrame([{
        "product_id": data.get("productId"),
        "quantity": data.get("quantity"),
        "orderDate": data.get("orderDate"),
        "dueDate": data.get("dueDate"),
    }])
    # 예측에 사용할 특징 선택
    df['month'] = pd.to_datetime(df['orderDate']).dt.month
    df['day_of_week'] = pd.to_datetime(df['orderDate']).dt.dayofweek
    df['days_to_due_minutes'] = (pd.to_datetime(df['dueDate']) - pd.to_datetime(df['orderDate'])).dt.total_seconds() / 60.0
    
    features = df[['product_id', 'quantity', 'month', 'day_of_week', 'days_to_due_minutes']]
    # 특징 스케일링
    features_scaled = scaler.transform(features)
    # 예측 수행
    prediction = model.predict(features_scaled)
    is_on_time = int(prediction[0][0] >= 0.5)
    
    print(f"Predicted is_on_time: {is_on_time}")   
    
    return {"is_on_time": is_on_time}
'''    

@app.post("/ai_chat")
def ai_chat(data: dict = Body(...)):
    model = data.get("model", "gemma3:4b")
    message = data.get("message", "")
    response_message = generate_local_llm_chat_response(model, message)
    return {"message": response_message} 

def generate_local_llm_chat_response(model: str, message: str) -> str:
    try:
        url = "http://ollama:11434/api/chat"

        payload = {
            "model": model,
            "messages": [{"role": "user", "content": message}],
            "temperature": 0.7,
            "stream": False
        }

        response = requests.post(url, json=payload)
        response.raise_for_status()
        
        data = response.json()
      
        content = data.get("message", {}).get("content", "")
        
        return content.strip() if content else "응답을 받지 못했습니다."

    except Exception as e:
        print(f"Error: {str(e)}")  # 추가
        return f"Error occurred (Local): {str(e)}"

@app.post("/rag/upload")
async def upload_document(
    request: Request, 
    db: Session = Depends(get_db), 
    upload_file: UploadFile = File(...)
):
    result_message = await process_document_and_store_vectors(db=db, upload_file=upload_file)
    return {"message": result_message}

@app.post("/rag/chat")
async def rag_chat(
    request: Request, 
    user_input: str = Form(...),
    model: str = Form(...),
    db: Session = Depends(get_db)
):
    response_message = generate_rag_response(db, user_input, model)
    return {"message": response_message}

async def process_document_and_store_vectors(db: Session, upload_file: UploadFile) -> str:
    try:
        with tempfile.NamedTemporaryFile(delete=False, suffix=".pdf") as tmp:
            contents = await upload_file.read()
            tmp.write(contents)
            tmp_path = tmp.name

        # 문단별 텍스트와 벡터 리스트 추출
        texts, vectors = embed_pdf_to_vectors(tmp_path)
        print(f"[DEBUG] 문단 수: {len(texts)}")
        
        for idx, (text, vector) in enumerate(zip(texts, vectors)):
            print(f"[DEBUG] 저장 중: 문단 {idx + 1} - 길이 {len(text)}")
            doc_vector = Vector(
                content=text,
                embedding=vector
            )
            db.add(doc_vector)
            
        db.commit()
        return f"Successfully stored {len(vectors)} vectors."
    except SQLAlchemyError as e:
        db.rollback()
        return f"Database error: {str(e)}"
    except Exception as e:
        return f"Error occurred: {str(e)}"


def generate_rag_response(db: Session, query: str, model: str) -> str:
    try:
        query_vector = embed_text(query)
        vector_str = '[' + ','.join(map(str, query_vector)) + ']'

        result = db.execute(
            text(f"""
            SELECT id, content, embedding <=> '{vector_str}'::vector AS distance
            FROM vectors
            ORDER BY distance ASC
            LIMIT 3
            """)
        ).fetchall()

        # 컨텍스트 생성
        context = "\n".join([row[1] for row in result])
        prompt = f"Answer based on the following context:\n{context}\n\nQ: {query}\nA:"

        # Ollama API 호출
        url = "http://ollama:11434/api/chat"

        payload = {
            "model": model,
            "messages": [{"role": "user", "content": prompt}],
            "temperature": 0.7,
            "stream": False
        }

        response = requests.post(url, json=payload)
        response.raise_for_status()
        
        data = response.json()
      
        content = data.get("message", {}).get("content", "")
        
        return content.strip() if content else "응답을 받지 못했습니다."

    except Exception as e:
        print(f"Error (RAG): {str(e)}")
        return f"Error occurred (RAG): {str(e)}"