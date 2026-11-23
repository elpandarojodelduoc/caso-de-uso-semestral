package com.example.bapptest.data.remote

import com.example.bapptest.model.RespuestaClima
import com.example.bapptest.model.RespuestaGeocodificacion
import retrofit2.http.GET
import retrofit2.http.Query

interface ClimaApi {

    @GET("v1/forecast")
    suspend fun obtenerClimaActual(
        @Query("latitude") latitud: Double,
        @Query("longitude") longitud: Double,
        @Query("current") variables: String = "temperature_2m,relative_humidity_2m,wind_speed_10m"
    ): RespuestaClima
}

interface GeocodificacionApi {

    @GET("v1/search")
    suspend fun buscarCiudad(
        @Query("name") nombre: String,
        @Query("count") cantidad: Int = 1,
        @Query("language") idioma: String = "es"
    ): RespuestaGeocodificacion
}
