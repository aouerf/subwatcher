package io.github.aouerfelli.subwatcher.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.graphics.drawable.toBitmap
import coil.Coil
import coil.api.get
import java.io.ByteArrayOutputStream

// TODO: De-inlined because of https://github.com/cashapp/sqldelight/issues/1285
class ImageBlob(val value: ByteArray) {

    override fun equals(other: Any?): Boolean {
        return (this === other) || (other is ImageBlob &&  value.contentEquals(other.value))
    }

    override fun hashCode() = value.contentHashCode()
}

suspend fun Uri.toImageBlob(): ImageBlob {
    val drawable = Coil.get(this)
    val bitmap = drawable.toBitmap()
    val bitmapByteArray = ByteArrayOutputStream(bitmap.byteCount).use { outputStream ->
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        outputStream.toByteArray()
    }
    return ImageBlob(bitmapByteArray)
}

fun ImageBlob.toBitmap(): Bitmap {
    return BitmapFactory.decodeByteArray(value, 0, value.size)
}
