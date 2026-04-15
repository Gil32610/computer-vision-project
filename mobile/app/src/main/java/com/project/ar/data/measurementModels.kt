package com.project.ar.data

import com.google.gson.annotations.SerializedName

// ADD THIS CLASS HERE - This was the missing piece!
data class Point3D(
    val x: Float,
    val y: Float,
    val z: Float
)

data class MeasurementRequest(
    @SerializedName("point_a") val pointA: Point3D,
    @SerializedName("point_b") val pointB: Point3D,
    val label: String = "Manual Measurement"
)

data class MeasurementResponse(
    val label: String,
    @SerializedName("distance_meters") val distanceMeters: Float,
    @SerializedName("distance_centimeters") val distanceCm: Float
)