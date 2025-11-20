from sentence_transformers import SentenceTransformer
from file_loader import load_pdf_by_paragraph

model = SentenceTransformer("nomic-ai/nomic-embed-text-v1.5", trust_remote_code=True)

def embed_text(text: str) -> list[float]:
    return model.encode(text).tolist()

def embed_pdf_to_vectors(file_path: str) -> tuple[list[str], list[list[float]]]:
    paragraphs = load_pdf_by_paragraph(file_path)
    vectors = model.encode(paragraphs).tolist()
    return paragraphs, vectors