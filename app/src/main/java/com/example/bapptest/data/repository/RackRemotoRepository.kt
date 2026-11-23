package com.example.bapptest.data.repository

import com.example.bapptest.data.remote.RacksApi
import com.example.bapptest.data.remote.RetrofitInstance
import com.example.bapptest.model.RackRemoto
import com.example.bapptest.model.RackRemotoSolicitud

class RackRemotoRepository(private val api: RacksApi = RetrofitInstance.racksApi) {

    suspend fun obtenerRacks(): List<RackRemoto> = api.obtenerRacks()

    suspend fun obtenerRack(id: Int): RackRemoto = api.obtenerRack(id)

    suspend fun crearRack(rack: RackRemotoSolicitud): RackRemoto = api.crearRack(rack)

    suspend fun actualizarRack(id: Int, rack: RackRemotoSolicitud): RackRemoto = api.actualizarRack(id, rack)

    suspend fun eliminarRack(id: Int) = api.eliminarRack(id)
}
