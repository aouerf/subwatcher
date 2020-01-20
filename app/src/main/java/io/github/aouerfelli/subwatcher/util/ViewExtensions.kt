package io.github.aouerfelli.subwatcher.util

import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import io.github.aouerfelli.subwatcher.R

// TODO: Custom view
fun SwipeRefreshLayout.setThemeColorScheme() {
  val foregroundColor = context.getThemeColor(R.attr.colorSecondary)
  val backgroundColor = context.getThemeColor(R.attr.colorBackgroundFloating)
  setColorSchemeColors(foregroundColor)
  setProgressBackgroundColorSchemeColor(backgroundColor)
}

enum class SnackbarLength(val flag: Int) {
  INDEFINITE(Snackbar.LENGTH_INDEFINITE),
  SHORT(Snackbar.LENGTH_SHORT),
  LONG(Snackbar.LENGTH_LONG)
}

// TODO: Theme snackbar
inline fun View.makeSnackbar(
  text: AndroidString,
  actionText: AndroidString? = null,
  length: SnackbarLength = SnackbarLength.SHORT,
  show: Boolean = false,
  crossinline action: () -> Unit = {}
): Snackbar {
  val textString = text.getString(context)
  val actionTextString = actionText?.getString(context)
  return Snackbar.make(this, textString, length.flag).apply {
    if (actionTextString != null) {
      setAction(actionTextString) { action() }
    }
    if (show) {
      show()
    }
  }
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
    ) = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
      val swipedDirection = Direction.values().single { it.flag == direction }
      action(viewHolder, swipedDirection)
    }
  }).attachToRecyclerView(this)
}
