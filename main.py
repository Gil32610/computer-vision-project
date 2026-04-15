from argparse import OPTIONAL

from fastapi import FastAPI, HTTPException, Header
from pydantic import BaseModel
from typing import Optional
import math
import logging


logging.basicConfig(level=logging.INFO)

logger = logging.getLogger("AR-Backend")

app = FastAPI(title="AR Measurement Computer Vision Backend")

HISTORY = []


class Point3D(BaseModel):
    x:float
    y:float
    z:float


class Measurement(BaseModel):
    point_a: Point3D
    point_b: Point3D
    label: Optional[str] = "Measurment"

class MeasurementResponse(BaseModel):
    label:Optional[str]
    distance_meters: float
    distance_centimeters: float
    coordinates: dict[str, Point3D]


@app.get("/")
def get_root():
    return {"status": "online", "mode": "CV_AR_Points_Distance_Measurement"}

@app.post("/calculate-distance", response_model=MeasurementResponse)
async def calculate_distance(data: Measurement):
    try:
        p1 = data.point_a
        p2 = data.point_b
        distance = math.sqrt((p2.x - p1.x)**2 + (p2.y - p1.y)**2 + (p2.z - p1.z)**2 )

        logger.info(f"Processed the {data.label} Distance = {distance:.4f}m between points {p1} and {p2}")

        result= {"label": data.label, "distance_meters": round(distance, 4), "distance_centimeters": round(distance * 100, 2), "coordinates": {"point_a":p1, "point_b": p2}}
        print(f"Calculated the distance with provided data ", result["distance_meters"])
        HISTORY.append(result)
        return result 
    
    except Exception as e:
        logger.error(f"Calculation error: {e}")
        raise HTTPException(status_code=400,  detail="Invalid coordinate data")