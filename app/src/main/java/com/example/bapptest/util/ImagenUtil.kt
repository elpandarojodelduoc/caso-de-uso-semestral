package com.example.bapptest.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import kotlin.math.max

object ImagenUtil {

    private const val LADO_MAXIMO_VISTA_PREVIA = 1024

    fun crearUriFotoTemporal(contexto: Context): Uri {
        val directorio = File(contexto.cacheDir, "fotos").apply { mkdirs() }
        val archivo = File.createTempFile("rack_", ".jpg", directorio)
        return FileProvider.getUriForFile(contexto, "${contexto.packageName}.fileprovider", archivo)
    }

    fun copiarAAlmacenamientoInterno(contexto: Context, origen: Uri): String? {
        return try {
            val directorio = File(contexto.filesDir, "racks").apply { mkdirs() }
            val destino = File(directorio, "rack_${System.currentTimeMillis()}.jpg")
            val entrada = contexto.contentResolver.openInputStream(origen) ?: return null
            entrada.use { flujoEntrada ->
                FileOutputStream(destino).use { flujoSalida -> flujoEntrada.copyTo(flujoSalida) }
            }
            destino.absolutePath
        } catch (e: Exception) {
            null
        }
    }

    fun cargarImagen(contexto: Context, uri: Uri): ImageBitmap? {
        return try {
            val limites = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            contexto.contentResolver.openInputStream(uri)?.use {
                BitmapFactory.decodeStream(it, null, limites)
            }
            var factorReduccion = 1
            while (max(limites.outWidth, limites.outHeight) / factorReduccion > LADO_MAXIMO_VISTA_PREVIA) {
                factorReduccion *= 2
            }
            val opciones = BitmapFactory.Options().apply { inSampleSize = factorReduccion }
            val mapaBits = contexto.contentResolver.openInputStream(uri)?.use {
                BitmapFactory.decodeStream(it, null, opciones)
            } ?: return null
            corregirRotacion(contexto, uri, mapaBits).asImageBitmap()
        } catch (e: Exception) {
            null
        }
    }

    private fun corregirRotacion(contexto: Context, uri: Uri, mapaBits: Bitmap): Bitmap {
        val orientacion = try {
            contexto.contentResolver.openInputStream(uri)?.use {
                ExifInterface(it).getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )
            } ?: ExifInterface.ORIENTATION_NORMAL
        } catch (e: Exception) {
            ExifInterface.ORIENTATION_NORMAL
        }
        val grados = when (orientacion) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90f
            ExifInterface.ORIENTATION_ROTATE_180 -> 180f
            ExifInterface.ORIENTATION_ROTATE_270 -> 270f
            else -> return mapaBits
        }
        val matriz = Matrix().apply { postRotate(grados) }
        return Bitmap.createBitmap(mapaBits, 0, 0, mapaBits.width, mapaBits.height, matriz, true)
    }
}
