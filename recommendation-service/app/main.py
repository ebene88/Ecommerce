from app.recommender import ContentBasedRecommender
from fastapi import FastAPI

app = FastAPI(title="Recommendation Service")

recommender = ContentBasedRecommender()


@app.on_event("startup")
def startup():
    recommender.load_products()


@app.get("/api/recommendations/product/{product_id}")
def get_recommendations(product_id: str, limit: int = 8):
    return {
        "productId": product_id,
        "recommendations": recommender.recommend(product_id, limit),
    }
