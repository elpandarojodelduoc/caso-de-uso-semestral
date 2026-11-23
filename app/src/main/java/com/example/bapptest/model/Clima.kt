package com.example.bapptest.model

import com.google.gson.annotations.SerializedName

data class RespuestaClima(
    @SerializedName("current") val actual: ClimaActual
)

data class ClimaActual(
    @SerializedName("temperature_2m") val temperatura: Double,
    @SerializedName("relative_humidity_2m") val humedad: Double,
    @SerializedName("wind_speed_10m") val vientoKmH: Double
)

data class RespuestaGeocodificacion(
    @SerializedName("results") val resultados: List<CiudadEncontrada>?
)

data class CiudadEncontrada(
    @SerializedName("name") val nombre: String,
    @SerializedName("latitude") val latitud: Double,
    @SerializedName("longitude") val longitud: Double,
    @SerializedName("country") val pais: String?
)

data class ClimaDelSitio(
    val lugar: String,
    val temperatura: Double,
    val humedad: Double,
    val vientoKmH: Double
)
