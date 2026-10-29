package com.example.bapptest.data.repository

import com.example.bapptest.data.local.AppDatabase
import com.example.bapptest.data.local.ComponenteEntity
import com.example.bapptest.data.local.RackConComponentes
import com.example.bapptest.data.local.RackEntity
import com.example.bapptest.data.local.UsuarioEntity
import com.example.bapptest.util.Seguridad
import kotlinx.coroutines.flow.Flow

class BappRepository(baseDatos: AppDatabase) {

    private val usuarioDao = baseDatos.usuarioDao()
    private val rackDao = baseDatos.rackDao()

    suspend fun registrarUsuario(correo: String, clave: String): Boolean {
        val usuario = UsuarioEntity(correo = correo, claveHash = Seguridad.hashClave(clave))
        return usuarioDao.insertar(usuario) != -1L
    }

    suspend fun iniciarSesion(correo: String, clave: String): Boolean {
        val usuario = usuarioDao.buscarPorCorreo(correo) ?: return false
        return usuario.claveHash == Seguridad.hashClave(clave)
    }

    fun observarRacks(correo: String): Flow<List<RackConComponentes>> = rackDao.observarRacks(correo)

    suspend fun obtenerRack(rackId: Long): RackConComponentes? = rackDao.obtenerRack(rackId)

    suspend fun guardarRack(
        rackExistenteId: Long?,
        rack: RackEntity,
        componentes: List<ComponenteEntity>
    ) {
        rackDao.guardarCompleto(rackExistenteId, rack, componentes)
    }

    suspend fun eliminarRack(rackId: Long) {
        rackDao.eliminarRack(rackId)
    }
}
