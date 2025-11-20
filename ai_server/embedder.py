import requests
from file_loader import load_pdf_by_paragraph

OLLAMA_URL = "http://ollama:11434/api/embeddings"
EMBEDDING_MODEL = "nomic-embed-text"

def embed_text(text: str) -> list[float]:
    """단일 텍스트를 임베딩"""
    try:
        payload = {
            "model": EMBEDDING_MODEL,
            "prompt": text
        }
        
        response = requests.post(OLLAMA_URL, json=payload)
        response.raise_for_status()
        
        data = response.json()
        return data.get("embedding", [])
        
    except Exception as e:
        print(f"Embedding error: {str(e)}")
        return []

def embed_pdf_to_vectors(file_path: str) -> tuple[list[str], list[list[float]]]:
    """PDF를 문단별로 로드하고 각 문단을 임베딩"""
    paragraphs = load_pdf_by_paragraph(file_path)
    
    vectors = []
    for paragraph in paragraphs:
        vector = embed_text(paragraph)
        if vector:
            vectors.append(vector)
        else:
            # 임베딩 실패 시 빈 벡터나 처리 방법
            print(f"Failed to embed paragraph: {paragraph[:50]}...")
    
    # 성공한 것들만 반환
    valid_paragraphs = paragraphs[:len(vectors)]
    
    return valid_paragraphs, vectors