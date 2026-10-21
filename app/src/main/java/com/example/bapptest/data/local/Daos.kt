package com.example.bapptest.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertar(usuario: UsuarioEntity): Long

    @Query("SELECT * FROM usuarios WHERE correo = :correo LIMIT 1")
    suspend fun buscarPorCorreo(correo: String): UsuarioEntity?
}

@Dao
abstract class RackDao {

    @Insert
    abstract suspend fun insertarRack(rack: RackEntity): Long

    @Update
    abstract suspend fun actualizarRack(rack: RackEntity)

    @Insert
    abstract suspend fun insertarComponentes(componentes: List<ComponenteEntity>)

    @Query("DELETE FROM componentes WHERE rackId = :rackId")
    abstract suspend fun eliminarComponentes(rackId: Long)

    @Query("DELETE FROM racks WHERE id = :rackId")
    abstract suspend fun eliminarRack(rackId: Long)

    @Transaction
    @Query("SELECT * FROM racks WHERE correoUsuario = :correo ORDER BY id DESC")
    abstract fun observarRacks(correo: String): Flow<List<RackConComponentes>>

    @Transaction
    @Query("SELECT * FROM racks WHERE id = :rackId")
    abstract suspend fun obtenerRack(rackId: Long): RackConComponentes?

    @Transaction
    open suspend fun guardarCompleto(
        rackExistenteId: Long?,
        rack: RackEntity,
        componentes: List<ComponenteEntity>
    ) {
        val rackId = if (rackExistenteId == null) {
            insertarRack(rack)
        } else {
            actualizarRack(rack.copy(id = rackExistenteId))
            eliminarComponentes(rackExistenteId)
            rackExistenteId
        }
        insertarComponentes(componentes.map { it.copy(id = 0, rackId = rackId) })
    }
}
