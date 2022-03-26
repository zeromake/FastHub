package com.fastaccess.ui.modules.repos.code.contributors.graph

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.withClip
import com.fastaccess.R
import kotlin.math.abs
import kotlin.math.round

class GraphView(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    View(context, attrs, defStyleAttr) {
    private val TAG = "GraphView"
    var graphData: List<GraphStatModel.ContributionStats.Week> = emptyList()
        set(value) {
            if (field != value) {
                field = value
                requestLayout()
            }
        }
    private val maxLines: Int = 5
    private var widthSpacing: Float = resources.getDimension(R.dimen.grid_spacing)
    private var commits: Int = 0
    private var textDistance: Float = 0f
    private val textBounds = Rect()
    private val path = Path()
    private val textPadding = resources.getDimension(R.dimen.spacing_micro)
    private val lineDistance = resources.getDimension(R.dimen.spacing_large)
    private val linePaint = Paint().apply {
        style = Paint.Style.FILL
        color = if (isInEditMode) Color.BLACK else Color.LTGRAY
        textSize = resources.getDimension(R.dimen.graph_line_size)
    }
    private val graphPaint = Paint().apply {
        style = Paint.Style.STROKE
        color = ContextCompat.getColor(context, R.color.material_green_900)
        isAntiAlias = true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // TODO: Find better way to achieve below using floor or ceil functions.
        if (graphData.isNotEmpty()) {
            val maxCommit = graphData.maxOf { it.commits } // eg: 21
            var roundedCommit = (round(maxCommit.toDouble()/5.0) * 5).toInt() // eg: 25
            if (roundedCommit > maxCommit)
                roundedCommit -= 5 // eg: 20
            commits = roundedCommit / (maxLines - 1) // Since sequence is in AP.
            if (commits % 5 != 0) {
                commits = getNearestRoundTo5(commits.toDouble())
            }
            if (commits == 0) commits = 5
            val heightSpec = MeasureSpec.makeMeasureSpec(lineDistance.toInt() * maxLines, MeasureSpec.EXACTLY)
            linePaint.getTextBounds(maxCommit.toString(), 0, maxCommit.toString().length, textBounds)
            super.onMeasure(widthMeasureSpec, heightSpec)
        } else
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        var startX = textPadding * 3 + textBounds.width()
        val minHeight = height.toFloat() - textBounds.height() / 4
        widthSpacing = (width - startX) / graphData.size
        path.reset()
        path.moveTo(startX, minHeight)
        graphData.forEachIndexed { _, gm ->
            val maxY = abs(minHeight - (lineDistance * gm.commits / maxLines))
            path.lineTo(startX, maxY)
            startX += widthSpacing
        }
        path.lineTo(width.toFloat(), minHeight)
        path.lineTo(startX, minHeight)
    }

    override fun onDraw(canvas: Canvas?) {
        if (canvas == null) return
        super.onDraw(canvas)
        var decHeight = 0f
        for(i in 0..maxLines) {
            canvas.drawText("${i * commits}", textPadding, height.toFloat() - decHeight, linePaint)
            canvas.drawLine(textDistance, height.toFloat() - decHeight - textBounds.height() / 4, width.toFloat(), height.toFloat() - decHeight - textBounds.height() / 4, linePaint)
            decHeight += lineDistance
        }
        canvas.withClip(path) {
            canvas.drawColor(graphPaint.color)
        }
    }


    private fun getNearestRoundTo5(n: Double): Int {
        return (round(n/5.0) * 5).toInt()
    }
}