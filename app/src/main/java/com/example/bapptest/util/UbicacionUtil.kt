package com.example.bapptest.util

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

object UbicacionUtil {

    val permisosUbicacion = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    fun tienePermiso(contexto: Context): Boolean = permisosUbicacion.any { permiso ->
        ContextCompat.checkSelfPermission(contexto, permiso) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    suspend fun obtenerUbicacionActual(contexto: Context): Location? =
        suspendCancellableCoroutine<Location?> { continuacion ->
            val fuenteCancelacion = CancellationTokenSource()
            continuacion.invokeOnCancellation { fuenteCancelacion.cancel() }
            LocationServices.getFusedLocationProviderClient(contexto)
                .getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, fuenteCancelacion.token)
                .addOnSuccessListener { ubicacion -> continuacion.resume(ubicacion) }
                .addOnFailureListener { continuacion.resume(null) }
        }
}
