package com.example.bapptest.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [UsuarioEntity::class, RackEntity::class, ComponenteEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao

    abstract fun rackDao(): RackDao

    companion object {
        @Volatile
        private var instancia: AppDatabase? = null

        fun obtener(contexto: Context): AppDatabase {
            return instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    contexto.applicationContext,
                    AppDatabase::class.java,
                    "bapp.db"
                ).build().also { instancia = it }
            }
        }
    }
}
