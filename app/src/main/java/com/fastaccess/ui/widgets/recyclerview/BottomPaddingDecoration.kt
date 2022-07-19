package com.fastaccess.ui.widgets.recyclerview

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.fastaccess.R
import com.fastaccess.helper.ViewHelper

internal class BottomPaddingDecoration private constructor(private val bottomPadding: Int) :
    ItemDecoration() {
    private constructor(context: Context) : this(
        ViewHelper.toPx(
            context,
            context.resources.getDimensionPixelSize(R.dimen.fab_spacing)
        )
    )

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val dataSize = state.itemCount
        val position = parent.getChildAdapterPosition(view)
        if (parent.layoutManager is GridLayoutManager) {
            val grid = parent.layoutManager as GridLayoutManager?
            if (dataSize - position <= grid!!.spanCount) {
                outRect[0, 0, 0] = bottomPadding
            } else {
                outRect[0, 0, 0] = 0
            }
        } else if (parent.layoutManager is LinearLayoutManager) {
            if (dataSize > 0 && position == dataSize - 1) {
                outRect[0, 0, 0] = bottomPadding
            } else {
                outRect[0, 0, 0] = 0
            }
        } else if (parent.layoutManager is StaggeredGridLayoutManager) {
            val grid = parent.layoutManager as StaggeredGridLayoutManager?
            if (dataSize - position <= grid!!.spanCount) {
                outRect[0, 0, 0] = bottomPadding
            } else {
                outRect[0, 0, 0] = 0
            }
        }
    }

    companion object {
        @JvmStatic
        fun with(bottomPadding: Int): BottomPaddingDecoration {
            return BottomPaddingDecoration(bottomPadding)
        }

        @JvmStatic
        fun with(context: Context): BottomPaddingDecoration {
            return BottomPaddingDecoration(context)
        }
    }
}