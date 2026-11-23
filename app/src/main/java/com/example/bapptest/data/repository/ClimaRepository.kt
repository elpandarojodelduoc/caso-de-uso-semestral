package com.example.bapptest.data.repository

import com.example.bapptest.data.remote.ClimaApi
import com.example.bapptest.data.remote.GeocodificacionApi
import com.example.bapptest.data.remote.RetrofitInstance
import com.example.bapptest.model.CiudadEncontrada
import com.example.bapptest.model.ClimaDelSitio

class ClimaRepository(
    private val climaApi: ClimaApi = RetrofitInstance.climaApi,
    private val geocodificacionApi: GeocodificacionApi = RetrofitInstance.geocodificacionApi
) {

    suspend fun buscarCiudad(nombre: String): CiudadEncontrada? =
        geocodificacionApi.buscarCiudad(nombre).resultados?.firstOrNull()

    suspend fun obtenerClima(lugar: String, latitud: Double, longitud: Double): ClimaDelSitio {
        val actual = climaApi.obtenerClimaActual(latitud, longitud).actual
        return ClimaDelSitio(
            lugar = lugar,
            temperatura = actual.temperatura,
            humedad = actual.humedad,
            vientoKmH = actual.vientoKmH
        )
    }
}
