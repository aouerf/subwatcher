package io.github.aouerfelli.subwatcher.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.ImageView
import androidx.core.graphics.drawable.toBitmap
import coil.ImageLoader
import coil.api.get
import coil.api.load
import coil.request.LoadRequestBuilder
import coil.request.RequestDisposable
import java.io.ByteArrayOutputStream

// TODO: De-inlined because of https://github.com/cashapp/sqldelight/issues/1203#issuecomment-487438538
class ImageBlob(val value: ByteArray) {

  override fun equals(other: Any?): Boolean {
    return (this === other) || (other is ImageBlob && value.contentEquals(other.value))
  }

  override fun hashCode() = value.contentHashCode()
}

suspend fun Uri.toImageBlob(imageLoader: ImageLoader): ImageBlob {
  // TODO: Use OkHttp to fetch image directly
  val drawable = imageLoader.get(this)
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

inline fun ImageView.load(
  bitmap: Bitmap?,
  imageLoader: ImageLoader,
  builder: LoadRequestBuilder.() -> Unit = {}
): RequestDisposable {
  return imageLoader.load(context, bitmap) {
    target(this@load)
    builder()
  }
}
