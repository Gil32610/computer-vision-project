package com.project.ar.network

import com.project.ar.data.MeasurementRequest
import com.project.ar.data.MeasurementResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface ArApiService {
    @POST("calculate-distance")
    suspend fun getDistance(@Body request: MeasurementRequest): MeasurementResponse
}