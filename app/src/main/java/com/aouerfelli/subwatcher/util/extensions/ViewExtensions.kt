package com.aouerfelli.subwatcher.util.extensions

import android.net.Uri
import android.widget.ImageView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import coil.ImageLoader
import coil.api.load
import coil.request.LoadRequestBuilder
import coil.request.RequestDisposable
import com.aouerfelli.subwatcher.R

inline fun ImageView.load(
  uri: Uri?,
  imageLoader: ImageLoader,
  builder: LoadRequestBuilder.() -> Unit = { }
): RequestDisposable {
  return imageLoader.load(context, uri) {
    target(this@load)
    builder()
  }
}

// TODO: Custom view
fun SwipeRefreshLayout.setThemeColorScheme() {
  val foregroundColor = context.getThemeColor(R.attr.colorPrimary)
  val backgroundColor = context.getThemeColor(R.attr.colorBackgroundFloating)
  setColorSchemeColors(foregroundColor)
  setProgressBackgroundColorSchemeColor(backgroundColor)
}

enum class Direction(val flag: Int) {
  LEFT(ItemTouchHelper.LEFT),
  RIGHT(ItemTouchHelper.RIGHT),
  START(ItemTouchHelper.START),
  END(ItemTouchHelper.END),
  UP(ItemTouchHelper.UP),
  DOWN(ItemTouchHelper.DOWN)
}

inline fun RecyclerView.onSwipe(
  vararg directions: Direction = arrayOf(Direction.START, Direction.END),
  crossinline action: (RecyclerView.ViewHolder, Direction) -> Unit
) {
  val swipeDirFlags = directions.fold(0) { acc, direction -> acc or direction.flag }

  ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, swipeDirFlags) {
    override fun onMove(
      recyclerView: RecyclerView,
      viewHolder: RecyclerView.ViewHolder,
      target: RecyclerView.ViewHolder
    ): Boolean {
      return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
      val swipedDirection = Direction.values().single { it.flag == direction }
      action(viewHolder, swipedDirection)
    }
  }).attachToRecyclerView(this)
}
