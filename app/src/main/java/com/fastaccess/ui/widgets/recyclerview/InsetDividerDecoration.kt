package com.fastaccess.ui.widgets.recyclerview

import android.graphics.Canvas
import android.graphics.Paint
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

/**
 * A decoration which draws a horizontal divider between [RecyclerView.ViewHolder]s of a given
 * type; with a left inset.
 * this class adopted from Plaid
 */
internal class InsetDividerDecoration<T : RecyclerView.ViewHolder> @JvmOverloads constructor(
    private val height: Int,
    private val inset: Int,
    @ColorInt dividerColor: Int,
    toDivide: Class<T>? = null
) : ItemDecoration() {
    private val paint: Paint = Paint()
    private val toDivide: Class<T>?
    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val childCount = parent.childCount
        if (childCount < 2) return
        val lm = parent.layoutManager
        val lines = FloatArray(childCount * 4)
        var hasDividers = false
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val viewHolder = parent.getChildViewHolder(child)
            if (viewHolder !is ProgressBarViewHolder) {
                val canDivide = toDivide == null || viewHolder.javaClass == toDivide
                if (canDivide) {
                    val position = parent.getChildAdapterPosition(child)
                    if (child.isActivated || i + 1 < childCount && parent.getChildAt(i + 1).isActivated) {
                        continue
                    }
                    if (lm != null && position != state.itemCount - 1) {
                        lines[i * 4] =
                            if (inset == 0) inset.toFloat() else (inset + lm.getDecoratedLeft(child)).toFloat()
                        lines[i * 4 + 2] = lm.getDecoratedRight(child).toFloat()
                        val y = lm.getDecoratedBottom(child) + child.translationY
                            .toInt() - height
                        lines[i * 4 + 1] = y.toFloat()
                        lines[i * 4 + 3] = y.toFloat()
                        hasDividers = true
                    }
                }
            }
        }
        if (hasDividers) {
            canvas.drawLines(lines, paint)
        }
    }

    init {
        paint.color = dividerColor
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = height.toFloat()
        this.toDivide = toDivide
    }
}