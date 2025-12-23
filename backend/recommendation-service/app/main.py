from fastapi import FastAPI

from app.model import ApiResponse
from app.recommender import ContentBasedRecommender

app = FastAPI(title="Recommendation Service")


recommender = ContentBasedRecommender()


@app.on_event("startup")
def startup():
    recommender.load_products()


# @app.get("/api/recommendations/product/{product_id}")
@app.get("/api/recommendations/product/{product_id}", response_model=ApiResponse)
def get_recommendations(product_id: str, limit: int = 8):
    recommendations = recommender.recommend(product_id, limit)

    return {
        "success": True,
        "message": "Recommendations fetched successfully",
        "data": {
            "productId": product_id,
            "items": recommendations,
        },
    }
