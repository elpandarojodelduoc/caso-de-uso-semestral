package com.example.bapptest.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.bapptest.data.local.AppDatabase
import com.example.bapptest.data.local.RackConComponentes
import com.example.bapptest.data.repository.BappRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class RacksViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio = BappRepository(AppDatabase.obtener(application))
    private var trabajoObservacion: Job? = null

    var racks by mutableStateOf<List<RackConComponentes>>(emptyList())
        private set

    fun observar(correoUsuario: String) {
        trabajoObservacion?.cancel()
        trabajoObservacion = viewModelScope.launch {
            repositorio.observarRacks(correoUsuario).collect { racks = it }
        }
    }

    fun eliminar(rack: RackConComponentes) {
        viewModelScope.launch {
            repositorio.eliminarRack(rack.rack.id)
            withContext(Dispatchers.IO) { File(rack.rack.rutaImagen).delete() }
        }
    }
}
