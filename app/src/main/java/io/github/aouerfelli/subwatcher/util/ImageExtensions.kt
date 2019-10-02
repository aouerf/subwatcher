package io.github.aouerfelli.subwatcher.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.core.graphics.drawable.toBitmap
import coil.Coil
import coil.api.get
import java.io.ByteArrayOutputStream

inline class EncodedImage(private val value: String) {
    
    companion object {
        fun encode(imageByteArray: ByteArray): EncodedImage {
            return EncodedImage(Base64.encodeToString(imageByteArray, Base64.DEFAULT))
        }
    }
    
    fun decode(): ByteArray = Base64.decode(value, Base64.DEFAULT)
}

suspend fun String.toEncodedImage(): EncodedImage {
    val drawable = Coil.get(this)
    val bitmap = drawable.toBitmap()
    val bitmapByteArray = ByteArrayOutputStream(bitmap.byteCount).use { outputStream ->
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        outputStream.toByteArray()
    }
//    bitmap.recycle()
    return EncodedImage.encode(bitmapByteArray)
}

fun EncodedImage.toBitmap(): Bitmap {
    val decodedImage = decode()
    return BitmapFactory.decodeByteArray(decodedImage, 0, decodedImage.size)
}
