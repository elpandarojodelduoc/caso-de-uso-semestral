package com.example.bapptest.model

data class RackRemoto(
    val id: Int,
    val numero: Int,
    val modelo: String,
    val encargado: String,
    val capacidad: Int
)

data class RackRemotoSolicitud(
    val numero: Int,
    val modelo: String,
    val encargado: String,
    val capacidad: Int
)
