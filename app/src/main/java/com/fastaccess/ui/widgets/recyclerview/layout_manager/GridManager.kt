package com.fastaccess.ui.widgets.recyclerview.layout_manager

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView.Recycler
import androidx.recyclerview.widget.RecyclerView
import com.fastaccess.provider.crash.Report
import java.lang.Exception

/**
 * Created by Kosh on 17 May 2016, 10:02 PM
 */
class GridManager : GridLayoutManager {
    var iconSize = 0
        set(value) {
            field = value
            updateCount()
        }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    constructor(context: Context?, spanCount: Int) : super(context, spanCount)
    constructor(
        context: Context?,
        spanCount: Int,
        orientation: Int,
        reverseLayout: Boolean
    ) : super(context, spanCount, orientation, reverseLayout)

    override fun onLayoutChildren(recycler: Recycler, state: RecyclerView.State) {
        try {
            super.onLayoutChildren(recycler, state)
            updateCount()
        } catch (e: Exception) {
            Report.reportCatchException(e)
        }
    }

    override fun onMeasure(
        recycler: Recycler,
        state: RecyclerView.State,
        widthSpec: Int,
        heightSpec: Int
    ) {
        try {
            super.onMeasure(recycler, state, widthSpec, heightSpec)
        } catch (e: Exception) {
            Report.reportCatchException(e)
        }
    }

    private fun updateCount() {
        if (iconSize > 1) {
            val count = 1.coerceAtLeast(width / iconSize)
            if (count < 1) {
                spanCount = 1
            }
        }
    }

}