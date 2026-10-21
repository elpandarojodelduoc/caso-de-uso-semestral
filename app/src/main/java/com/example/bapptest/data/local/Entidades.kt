package com.example.bapptest.data.local

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "usuarios", indices = [Index(value = ["correo"], unique = true)])
data class UsuarioEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val correo: String,
    val claveHash: String
)

@Entity(tableName = "racks")
data class RackEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val idRack: String,
    val fecha: String,
    val rol: String,
    val rutaImagen: String,
    val correoUsuario: String
)

@Entity(
    tableName = "componentes",
    foreignKeys = [
        ForeignKey(
            entity = RackEntity::class,
            parentColumns = ["id"],
            childColumns = ["rackId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("rackId")]
)
data class ComponenteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val rackId: Long,
    val nombre: String,
    val categoria: String,
    val descripcion: String,
    val validado: Boolean
)

data class RackConComponentes(
    @Embedded val rack: RackEntity,
    @Relation(parentColumn = "id", entityColumn = "rackId")
    val componentes: List<ComponenteEntity>
)
