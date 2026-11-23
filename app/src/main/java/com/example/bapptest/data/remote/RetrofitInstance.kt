package com.example.bapptest.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private const val URL_RACKS = "https://x8ki-letl-twmt.n7.xano.io/api:V7He416y/registros_racks/"
    private const val URL_CLIMA = "https://api.open-meteo.com/"
    private const val URL_GEOCODIFICACION = "https://geocoding-api.open-meteo.com/"

    private fun crearRetrofit(urlBase: String): Retrofit =
        Retrofit.Builder()
            .baseUrl(urlBase)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    val racksApi: RacksApi by lazy { crearRetrofit(URL_RACKS).create(RacksApi::class.java) }

    val climaApi: ClimaApi by lazy { crearRetrofit(URL_CLIMA).create(ClimaApi::class.java) }

    val geocodificacionApi: GeocodificacionApi by lazy {
        crearRetrofit(URL_GEOCODIFICACION).create(GeocodificacionApi::class.java)
    }
}
