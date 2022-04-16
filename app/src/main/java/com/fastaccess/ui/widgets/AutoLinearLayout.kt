package com.fastaccess.ui.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.GravityCompat
import com.fastaccess.R

class AutoLinearLayout : FrameLayout {
    private var mOrientation = 0
    private var mGravity = Gravity.TOP or GravityCompat.START
    private val mListPositions = ArrayList<ViewPosition>()

    constructor(context: Context) : super(context) {
        init(context, null, 0, 0)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs, 0, 0)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context, attrs, defStyleAttr, 0)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(context, attrs, defStyleAttr, defStyleRes)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (mOrientation == VERTICAL) {
            measureVertical(widthMeasureSpec, heightMeasureSpec)
        } else {
            measureHorizontal(widthMeasureSpec, heightMeasureSpec)
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        mListPositions.clear()
        if (mOrientation == VERTICAL) layoutVertical(
            left,
            top,
            right,
            bottom
        ) else layoutHorizontal(left, top, right, bottom)
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        val a = context.obtainStyledAttributes(
            attrs,
            R.styleable.AutoLinearLayout,
            defStyleAttr,
            defStyleRes
        )
        try {
            mOrientation = a.getInt(R.styleable.AutoLinearLayout_auto_orientation, HORIZONTAL)
            val gravity = a.getInt(R.styleable.AutoLinearLayout_auto_gravity, -1)
            if (gravity >= 0) {
                setGravity(gravity)
            }
        } finally {
            a.recycle()
        }
    }

    private fun measureHorizontal(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var wSize = MeasureSpec.getSize(widthMeasureSpec) - (paddingLeft + paddingRight)

        //Scrollview case
        if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.UNSPECIFIED) wSize = Int.MAX_VALUE
        val count = childCount
        var rowWidth = 0
        var totalHeight = 0
        var rowMaxHeight = 0
        var childWidth: Int
        var childHeight: Int
        var maxRowWidth = paddingLeft + paddingRight
        for (i in 0 until count) {
            val child = getChildAt(i)
            if (child.visibility != GONE) {
                measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0)
                val lp = child.layoutParams as LayoutParams
                childWidth = child.measuredWidth + lp.leftMargin + lp.rightMargin
                childHeight = child.measuredHeight + lp.topMargin + lp.bottomMargin
                //keep max height value stored
                rowMaxHeight = rowMaxHeight.coerceAtLeast(childHeight)

                //exceed max width start new row and update total height
                if (childWidth + rowWidth > wSize) {
                    totalHeight += rowMaxHeight
                    maxRowWidth = maxRowWidth.coerceAtLeast(rowWidth)
                    rowWidth = childWidth
                    rowMaxHeight = childHeight
                } else {
                    rowWidth += childWidth
                }
            }
        }
        //plus last child height and width
        if (rowWidth != 0) {
            maxRowWidth = maxRowWidth.coerceAtLeast(rowWidth)
            totalHeight += rowMaxHeight
        }

        //set width to max value
        if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.UNSPECIFIED) wSize =
            maxRowWidth + (paddingLeft + paddingRight)
        setMeasuredDimension(
            resolveSize(wSize, widthMeasureSpec),
            resolveSize(totalHeight + paddingTop + paddingBottom, heightMeasureSpec)
        )
    }

    private fun measureVertical(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var hSize = MeasureSpec.getSize(heightMeasureSpec) - (paddingTop + paddingBottom)
        val count = childCount
        var columnHeight = 0
        var totalWidth = 0
        var maxColumnHeight = 0
        var columnMaxWidth = 0
        var childWidth: Int
        var childHeight: Int

        //Scrollview case
        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.UNSPECIFIED) hSize = Int.MAX_VALUE
        for (i in 0 until count) {
            val child = getChildAt(i)
            if (child.visibility != GONE) {
                measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0)
                val lp = child.layoutParams as LayoutParams
                childWidth = child.measuredWidth + lp.leftMargin + lp.rightMargin
                childHeight = child.measuredHeight + lp.topMargin + lp.bottomMargin
                //keep max width value stored
                columnMaxWidth = columnMaxWidth.coerceAtLeast(childWidth)

                //exceed max height start new column and update total width
                if (childHeight + columnHeight > hSize) {
                    totalWidth += columnMaxWidth
                    maxColumnHeight = maxColumnHeight.coerceAtLeast(columnHeight)
                    columnHeight = childHeight
                    columnMaxWidth = childWidth
                } else {
                    columnHeight += childHeight
                }
            }
        }
        //plus last child width
        if (columnHeight != 0) {
            maxColumnHeight = maxColumnHeight.coerceAtLeast(columnHeight)
            totalWidth += columnMaxWidth
        }

        //set height to max value
        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.UNSPECIFIED) hSize =
            maxColumnHeight + (paddingTop + paddingBottom)
        setMeasuredDimension(
            resolveSize(
                totalWidth + paddingRight + paddingLeft,
                widthMeasureSpec
            ), resolveSize(hSize, heightMeasureSpec)
        )
    }

    /**
     * Arranges the children in columns. Takes care about child margin, padding, gravity and
     * child layout gravity.
     *
     * @param left
     * parent left
     * @param top
     * parent top
     * @param right
     * parent right
     * @param bottom
     * parent bottom
     */
    private fun layoutVertical(left: Int, top: Int, right: Int, bottom: Int) {
        val count = childCount
        if (count == 0) return
        val width = right - paddingLeft - left - paddingRight
        val height = bottom - paddingTop - top - paddingBottom
        var childTop = paddingTop
        var childLeft = paddingLeft
        var totalHorizontal = paddingLeft + paddingRight
        var totalVertical = 0
        var column = 0
        var maxChildWidth = 0
        for (i in 0 until count) {
            val child = getChildAt(i)
            if (child != null && child.visibility != GONE) {
                //if child is not updated yet call measure
                if (child.measuredHeight == 0 || child.measuredWidth == 0) child.measure(
                    MeasureSpec.makeMeasureSpec(width, MeasureSpec.AT_MOST),
                    MeasureSpec.makeMeasureSpec(height, MeasureSpec.AT_MOST)
                )
                val lp = child.layoutParams as LayoutParams
                val childWidth = child.measuredWidth
                val childHeight = child.measuredHeight
                //if there is not enough space jump to another column
                if (childTop + childHeight + lp.topMargin + lp.bottomMargin > height + paddingTop) {
                    //before change column update positions if the gravity is present
                    updateChildPositionVertical(height, totalVertical, column, maxChildWidth)
                    childTop = paddingTop
                    childLeft += maxChildWidth
                    maxChildWidth = 0
                    column++
                    totalVertical = 0
                }
                childTop += lp.topMargin
                mListPositions.add(ViewPosition(childLeft, childTop, column))
                //check max child width
                val currentWidth = childWidth + lp.leftMargin + lp.rightMargin
                if (maxChildWidth < currentWidth) maxChildWidth = currentWidth
                //get ready for next child
                childTop += childHeight + lp.bottomMargin
                totalVertical += childHeight + lp.topMargin + lp.bottomMargin
            }
        }

        //update positions for last column
        updateChildPositionVertical(height, totalVertical, column, maxChildWidth)
        totalHorizontal += childLeft + maxChildWidth
        //final update for horizontal gravities and layout views
        updateChildPositionHorizontal(width, totalHorizontal, column, 0)
        //mListPositions.clear();
    }

    /**
     * Arranges the children in rows. Takes care about child margin, padding, gravity and
     * child layout gravity. Analog to vertical.
     *
     * @param left
     * parent left
     * @param top
     * parent top
     * @param right
     * parent right
     * @param bottom
     * parent bottom
     */
    private fun layoutHorizontal(left: Int, top: Int, right: Int, bottom: Int) {
        val count = childCount
        if (count == 0) return
        val width = right - paddingLeft - left - paddingRight
        val height = bottom - paddingTop - top - paddingBottom
        var childTop = paddingTop
        var childLeft = paddingLeft
        var totalHorizontal = 0
        var totalVertical = paddingTop + paddingBottom
        var row = 0
        var maxChildHeight = 0
        for (i in 0 until count) {
            val child = getChildAt(i)
            if (child != null && child.visibility != GONE) {
                if (child.measuredHeight == 0 || child.measuredWidth == 0) child.measure(
                    MeasureSpec.makeMeasureSpec(width, MeasureSpec.AT_MOST),
                    MeasureSpec.makeMeasureSpec(height, MeasureSpec.AT_MOST)
                )
                val lp = child.layoutParams as LayoutParams
                val childWidth = child.measuredWidth
                val childHeight = child.measuredHeight
                if (childLeft + childWidth + lp.leftMargin + lp.rightMargin > width + paddingLeft) {
                    updateChildPositionHorizontal(width, totalHorizontal, row, maxChildHeight)
                    childLeft = paddingLeft
                    childTop += maxChildHeight
                    maxChildHeight = 0
                    row++
                    totalHorizontal = 0
                }
                childLeft += lp.leftMargin
                mListPositions.add(ViewPosition(childLeft, childTop, row))
                val currentHeight = childHeight + lp.topMargin + lp.bottomMargin
                if (maxChildHeight < currentHeight) maxChildHeight = currentHeight
                childLeft += childWidth + lp.rightMargin
                totalHorizontal += childWidth + lp.rightMargin + lp.leftMargin
            }
        }
        updateChildPositionHorizontal(width, totalHorizontal, row, maxChildHeight)
        totalVertical += childTop + maxChildHeight
        updateChildPositionVertical(height, totalVertical, row, 0)
        //mListPositions.clear();
    }

    /**
     * Updates children positions. Takes cares about gravity and layout gravity.
     * Finally layout children to parent if needed.
     *
     * @param parentHeight
     * parent parentHeight
     * @param totalSize
     * total vertical size used by children in a column
     * @param column
     * column number
     * @param maxChildWidth
     * the biggest child width
     */
    private fun updateChildPositionVertical(
        parentHeight: Int,
        totalSize: Int,
        column: Int,
        maxChildWidth: Int
    ) {
        for (i in mListPositions.indices) {
            val pos = mListPositions[i]
            val child = getChildAt(i)
            //(android:gravity)
            //update children position inside parent layout
            if (mOrientation == HORIZONTAL || pos.position == column) {
                updateTopPositionByGravity(pos, parentHeight - totalSize, mGravity)
            }
            //(android:layout_gravity)
            //update children position inside their space
            if (mOrientation == VERTICAL && pos.position == column) {
                val lp = child.layoutParams as LayoutParams
                val size = maxChildWidth - child.measuredWidth - lp.leftMargin - lp.rightMargin
                updateLeftPositionByGravity(pos, size, lp.gravity)
            }
            //update children into layout parent
            if (mOrientation == HORIZONTAL) layout(child, pos)
        }
    }

    /**
     * Updates children positions. Takes cares about gravity and layout gravity.
     * Finally layout children to parent if needed. Analog to vertical.
     *
     * @param parentWidth
     * parent parentWidth
     * @param totalSize
     * total horizontal size used by children in a row
     * @param row
     * row number
     * @param maxChildHeight
     * the biggest child height
     */
    private fun updateChildPositionHorizontal(
        parentWidth: Int,
        totalSize: Int,
        row: Int,
        maxChildHeight: Int
    ) {
        for (i in mListPositions.indices) {
            val pos = mListPositions[i]
            val child = getChildAt(i)
            if (mOrientation == VERTICAL || pos.position == row) {
                updateLeftPositionByGravity(pos, parentWidth - totalSize, mGravity)
            }
            if (mOrientation == HORIZONTAL && pos.position == row) {
                val lp = child.layoutParams as LayoutParams
                val size = maxChildHeight - child.measuredHeight - lp.topMargin - lp.bottomMargin
                updateTopPositionByGravity(pos, size, lp.gravity)
            }
            if (mOrientation == VERTICAL) layout(child, pos)
        }
    }

    private fun updateLeftPositionByGravity(pos: ViewPosition, size: Int, gravity: Int) {
        when (gravity and Gravity.HORIZONTAL_GRAVITY_MASK) {
            GravityCompat.END -> pos.left += if (size > 0) size else 0
            Gravity.CENTER_HORIZONTAL -> pos.left += (if (size > 0) size else 0) / 2
        }
    }

    private fun updateTopPositionByGravity(pos: ViewPosition, size: Int, gravity: Int) {
        when (gravity and Gravity.VERTICAL_GRAVITY_MASK) {
            Gravity.BOTTOM -> pos.top += if (size > 0) size else 0
            Gravity.CENTER_VERTICAL -> pos.top += (if (size > 0) size else 0) / 2
        }
    }

    private fun layout(child: View, pos: ViewPosition) {
        val lp = child.layoutParams as LayoutParams
        if (mOrientation == HORIZONTAL) child.layout(
            pos.left, pos.top + lp.topMargin, pos.left + child.measuredWidth, pos.top +
                    child.measuredHeight + lp.topMargin
        ) else child.layout(
            pos.left + lp.leftMargin, pos.top, pos.left + child.measuredWidth +
                    lp.leftMargin, pos.top + child.measuredHeight
        )
    }

    /**
     * Describes how the child views are positioned. Defaults to GRAVITY_TOP. If
     * this layout has a VERTICAL orientation, this controls where all the child
     * views are placed if there is extra vertical space. If this layout has a
     * HORIZONTAL orientation, this controls the alignment of the children.
     *
     * @param gravity
     * See [Gravity]
     */
    private fun setGravity(gravity: Int) {
        var gravity1 = gravity
        if (mGravity != gravity1) {
            if (gravity1 and Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK == 0) {
                gravity1 = gravity1 or GravityCompat.START
            }
            if (gravity1 and Gravity.VERTICAL_GRAVITY_MASK == 0) {
                gravity1 = gravity1 or Gravity.TOP
            }
            mGravity = gravity1
            requestLayout()
        }
    }

    fun setHorizontalGravity(horizontalGravity: Int) {
        val gravity = horizontalGravity and GravityCompat.RELATIVE_HORIZONTAL_GRAVITY_MASK
        if (mGravity and Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK != gravity) {
            mGravity = mGravity and GravityCompat.RELATIVE_HORIZONTAL_GRAVITY_MASK.inv() or gravity
            requestLayout()
        }
    }

    fun setVerticalGravity(verticalGravity: Int) {
        val gravity = verticalGravity and Gravity.VERTICAL_GRAVITY_MASK
        if (mGravity and Gravity.VERTICAL_GRAVITY_MASK != gravity) {
            mGravity = mGravity and Gravity.VERTICAL_GRAVITY_MASK.inv() or gravity
            requestLayout()
        }
    }

    var orientation: Int
        /**
         * Returns the current orientation.
         *
         * @return either [.HORIZONTAL] or [.VERTICAL]
         */
        get() = mOrientation
        /**
         * Should the layout be a column or a row.
         *
         * @param orientation
         * Pass HORIZONTAL or VERTICAL. Default value is HORIZONTAL.
         */
        set(orientation) {
            if (mOrientation != orientation) {
                mOrientation = orientation
                requestLayout()
            }
        }

    /**
     * Helper inner class that stores child position
     */
    private class ViewPosition(
        var left: Int, var top: Int, //row or column
        var position: Int
    ) {
        override fun toString(): String {
            return "left-$left top$top pos$position"
        }
    }

    companion object {
        private const val HORIZONTAL = 0
        private const val VERTICAL = 1
    }
}