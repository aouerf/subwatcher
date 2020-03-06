package com.aouerfelli.subwatcher.widget.behavior

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton

class ScrollAwareExtendedFloatingActionButtonBehavior(context: Context, attrs: AttributeSet) :
  CoordinatorLayout.Behavior<ExtendedFloatingActionButton>(context, attrs) {

  override fun onAttachedToLayoutParams(params: CoordinatorLayout.LayoutParams) {
    if (params.dodgeInsetEdges == Gravity.NO_GRAVITY) {
      params.dodgeInsetEdges = Gravity.BOTTOM
    }
  }

  override fun onStartNestedScroll(
    coordinatorLayout: CoordinatorLayout,
    child: ExtendedFloatingActionButton,
    directTargetChild: View,
    target: View,
    axes: Int,
    type: Int
  ): Boolean {
    return axes == View.SCROLL_AXIS_VERTICAL || super.onStartNestedScroll(
      coordinatorLayout,
      child,
      directTargetChild,
      target,
      axes,
      type
    )
  }

  override fun onNestedScroll(
    coordinatorLayout: CoordinatorLayout,
    child: ExtendedFloatingActionButton,
    target: View,
    dxConsumed: Int,
    dyConsumed: Int,
    dxUnconsumed: Int,
    dyUnconsumed: Int,
    type: Int,
    consumed: IntArray
  ) {
    super.onNestedScroll(
      coordinatorLayout,
      child,
      target,
      dxConsumed,
      dyConsumed,
      dxUnconsumed,
      dyUnconsumed,
      type,
      consumed
    )

    if (dyConsumed > 0 && child.isExtended) {
      child.shrink()
    } else if (dyConsumed < 0 && !child.isExtended) {
      // If the FAB has a layout anchor, use that to determine if user has scrolled to the top
      val anchorId = (child.layoutParams as CoordinatorLayout.LayoutParams).anchorId
      val anchorView = if (anchorId != View.NO_ID) {
        coordinatorLayout.findViewById<View>(anchorId)
      } else {
        null
      }
      // The FAB should not extend until the user scrolls back to the top of the page
      val upDirection = -1
      if (anchorView == null || !anchorView.canScrollVertically(upDirection)) {
        child.extend()
      }
    }
  }
}
