# from fastapi import FastAPI
# from app.api import router as api_router

# app = FastAPI()

# app.include_router(api_router, prefix="/api/gpt")

from fastapi import FastAPI, Request
from fastapi.exceptions import RequestValidationError
from fastapi.responses import JSONResponse
from fastapi.encoders import jsonable_encoder
import logging

from app.api import router as api_router

# 로깅 설정 (필요하면 로그 파일로도 저장 가능)
logging.basicConfig(level=logging.INFO, format="%(asctime)s - %(levelname)s - %(message)s")

app = FastAPI()

# 422 Validation Error 핸들러 등록
@app.exception_handler(RequestValidationError)
async def validation_exception_handler(request: Request, exc: RequestValidationError):
    logging.error("❌ [422 Validation Error]")
    logging.error(f"요청 경로: {request.url.path}")
    logging.error(f"요청 본문: {await request.body()}")
    logging.error(f"오류 내용: {exc.errors()}")

    return JSONResponse(
        status_code=422,
        content=jsonable_encoder({"detail": exc.errors()}),
    )

# 라우터 등록
app.include_router(api_router, prefix="/api/gpt")
