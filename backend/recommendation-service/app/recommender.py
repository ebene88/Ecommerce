import requests
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity

from app.config import PRODUCT_SERVICE_URL


class ContentBasedRecommender:
    def __init__(self):
        self.products = []
        self.product_ids = []
        self.tfidf_matrix = None
        self.vectorizer = TfidfVectorizer(stop_words="english")

    def load_products(self):
        response = requests.get(PRODUCT_SERVICE_URL)
        response.raise_for_status()

        data = response.json()["data"]["content"]

        self.products = data
        self.product_ids = [p["id"] for p in data]

        corpus = [f'{p["name"]} {p["description"]} {p["categoryId"]}' for p in data]

        self.tfidf_matrix = self.vectorizer.fit_transform(corpus)

    def recommend(self, product_id: str, top_n: int = 5):
        if product_id not in self.product_ids:
            return []

        idx = self.product_ids.index(product_id)
        similarity_scores = cosine_similarity(
            self.tfidf_matrix[idx], self.tfidf_matrix
        ).flatten()

        similar_indices = similarity_scores.argsort()[::-1][1 : top_n + 1]

        return [self.products[i] for i in similar_indices]
