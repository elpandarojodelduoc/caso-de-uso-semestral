package com.example.bapptest.data.remote

import com.example.bapptest.model.RackRemoto
import com.example.bapptest.model.RackRemotoSolicitud
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface RacksApi {

    @GET("racks")
    suspend fun obtenerRacks(): List<RackRemoto>

    @GET("racks/{id}")
    suspend fun obtenerRack(@Path("id") id: Int): RackRemoto

    @POST("racks")
    suspend fun crearRack(@Body rack: RackRemotoSolicitud): RackRemoto

    @PATCH("racks/{id}")
    suspend fun actualizarRack(@Path("id") id: Int, @Body rack: RackRemotoSolicitud): RackRemoto

    @DELETE("racks/{id}")
    suspend fun eliminarRack(@Path("id") id: Int)
}
