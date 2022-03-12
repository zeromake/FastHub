package com.fastaccess.ui.widgets.recyclerview.layout_manager

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.recyclerview.widget.RecyclerView.Recycler
import androidx.recyclerview.widget.RecyclerView
import java.lang.Exception

/**
 * Created by Kosh on 17 May 2016, 10:02 PM
 */
class StaggeredManager : StaggeredGridLayoutManager {
    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
    }

    constructor(spanCount: Int, orientation: Int) : super(spanCount, orientation) {}

    override fun onLayoutChildren(recycler: Recycler, state: RecyclerView.State) {
        try {
            super.onLayoutChildren(recycler, state)
        } catch (ignored: Exception) {
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
        } catch (ignored: Exception) {
        }
    }
}