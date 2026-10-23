package com.example.bapptest.model

import kotlin.math.abs

data class ComponenteCatalogo(
    val nombre: String,
    val categoria: String,
    val descripcion: String
)

object CatalogoComponentes {

    val componentes = listOf(
        ComponenteCatalogo("Switch de red", "Red", "Conmutación de red"),
        ComponenteCatalogo("Panel de control", "Control", "Panel de operación"),
        ComponenteCatalogo("Servidor rack", "Cómputo", "Servidor montado en rack"),
        ComponenteCatalogo("Router", "Red", "Enrutamiento de red"),
        ComponenteCatalogo("Panel Patch", "Cableado", "Patch panel de conexión")
    )

    fun detectar(semilla: String): List<ComponenteCatalogo> {
        val base = abs(semilla.hashCode())
        val cantidad = 2 + base % 3
        val inicio = base % componentes.size
        return List(cantidad) { posicion -> componentes[(inicio + posicion) % componentes.size] }
    }
}
