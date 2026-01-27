from typing import List

from pydantic import BaseModel


class Product(BaseModel):
    id: str
    name: str
    description: str
    price: float
    categoryId: int
    sellerName: str
    imageUrls: list[str]


class RecommendationData(BaseModel):
    productId: str
    items: List[Product]


class ApiResponse(BaseModel):
    success: bool
    message: str
    data: RecommendationData
