import fitz  

def load_pdf(filepath: str) -> str:
    text = ""
    with fitz.open(filepath) as doc:
        for page in doc:
            text += page.get_text()
    return text.strip()

def load_pdf_by_paragraph(filepath: str) -> list[str]:
    full_text = load_pdf(filepath)
    # 연속 줄바꿈을 기준으로 분리하고, 너무 짧은 문단은 제외
    import re
    paragraphs = re.split(r'\n\s*\n', full_text) # 연속된 빈 줄 기준 분리
    return [p.strip() for p in paragraphs if p.strip()] 