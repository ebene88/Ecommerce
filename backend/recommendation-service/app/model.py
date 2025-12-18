from pydantic import BaseModel


class Product(BaseModel):
    id: str
    name: str
    description: str
    categoryName: str
    sellerName: str
    imageUrls: list[str]
