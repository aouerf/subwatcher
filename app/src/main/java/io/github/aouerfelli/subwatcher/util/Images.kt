package io.github.aouerfelli.subwatcher.util

import android.graphics.Bitmap
import android.net.Uri
import android.widget.ImageView
import androidx.core.graphics.drawable.toBitmap
import coil.ImageLoader
import coil.api.get
import coil.api.loadAny
import coil.bitmappool.BitmapPool
import coil.decode.DataSource
import coil.decode.Options
import coil.fetch.FetchResult
import coil.fetch.Fetcher
import coil.fetch.SourceResult
import coil.request.LoadRequestBuilder
import coil.request.RequestDisposable
import coil.size.Size
import java.io.ByteArrayOutputStream
import okio.buffer
import okio.source

// TODO: De-inlined because of https://github.com/cashapp/sqldelight/issues/1203#issuecomment-487438538
class ImageBlob(val image: ByteArray) {

  override fun equals(other: Any?): Boolean {
    return (this === other) || (other is ImageBlob && image.contentEquals(other.image))
  }

  override fun hashCode() = image.contentHashCode()
}

suspend fun Uri.toImageBlob(imageLoader: ImageLoader): ImageBlob {
  // TODO: Use OkHttp to fetch the image blob directly
  val drawable = imageLoader.get(this)
  val bitmap = drawable.toBitmap()
  val bitmapByteArray = ByteArrayOutputStream(bitmap.byteCount).use { outputStream ->
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
    outputStream.toByteArray()
  }
  return ImageBlob(bitmapByteArray)
}

class ByteArrayFetcher : Fetcher<ByteArray> {

  override fun key(data: ByteArray): String? = null

  override suspend fun fetch(
    pool: BitmapPool,
    data: ByteArray,
    size: Size,
    options: Options
  ): FetchResult {
    return SourceResult(
      source = data.inputStream().source().buffer(),
      mimeType = null,
      dataSource = DataSource.MEMORY
    )
  }
}

inline fun ImageView.load(
  imageBlob: ImageBlob?,
  imageLoader: ImageLoader,
  builder: LoadRequestBuilder.() -> Unit = { }
): RequestDisposable {
  return imageLoader.loadAny(context, imageBlob?.image) {
    target(this@load)
    builder()
  }
}
