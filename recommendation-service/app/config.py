import os

from dotenv import load_dotenv

load_dotenv()  # loads .env into environment variables

PRODUCT_SERVICE_URL = os.getenv("PRODUCT_SERVICE_URL")

if not PRODUCT_SERVICE_URL:
    raise RuntimeError("PRODUCT_SERVICE_URL is not set")
