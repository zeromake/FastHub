package com.fastaccess.ui.modules.repos.code.contributors.graph.viewcomponent

import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import androidx.core.graphics.withClip
import com.fastaccess.R
import com.fastaccess.ui.modules.repos.code.contributors.graph.model.GraphStatModel
import java.util.*
import kotlin.math.abs
import kotlin.math.round

class GraphView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val TAG = "GraphView"
    var graphData: List<GraphStatModel.ContributionStats.Week> = emptyList()
        set(value) {
            field = value
            requestLayout()
        }
    private val maxLines: Int = 5
    private var widthSpacing: Float = 0f
    private var commits: Int = 0
    private var textDistance: Float = 0f
    private var maxHeight: Float = 0f
    private val textBounds = Rect()
    private val path = Path()
    private val calenderPath = Path()

    private val linePaint = Paint().apply {
        style = Paint.Style.STROKE
    }
    private val commitTextPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
    }
    private val timelineTextPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
    }
    private val graphPaint = Paint().apply {
        style = Paint.Style.STROKE
        isAntiAlias = true
    }

    init {
        context.withStyledAttributes(attrs, R.styleable.GraphView, defStyleAttr) {
            commitTextPaint.color = getColor(R.styleable.GraphView_graphTextColor, if (isInEditMode) Color.BLACK else Color.LTGRAY)
            timelineTextPaint.color = getColor(R.styleable.GraphView_graphTextColor, if (isInEditMode) Color.BLACK else Color.LTGRAY)
            linePaint.color = getColor(R.styleable.GraphView_graphLineColor, if (isInEditMode) Color.BLACK else Color.LTGRAY)
            graphPaint.color = getColor(R.styleable.GraphView_graphColor, 0xC8517558.toInt())

            linePaint.strokeWidth = getDimension(R.styleable.GraphView_graphLineThickness, 1f)
            commitTextPaint.textSize = getDimension(R.styleable.GraphView_graphCommitTextSize, 13.sp())
            timelineTextPaint.textSize = getDimension(R.styleable.GraphView_graphTimelineTextSize, 11.sp())
        }
    }

    private val bottomTextYearMaxSize = timelineTextPaint.measureText("2000")
    private val bottomTextMonthMaxSize = timelineTextPaint.measureText("Oct")
    private val lineHeight = 8.dp()
    private val bottomOffset = 25.dp()
    private val textPadding = 5.dp()
    private val lineDistance = 40.dp()
    private val lineLocationsX = FloatArray(7)
    private val lineDatesUnix = arrayListOf<Date>()

    private val calendar = Calendar.getInstance()

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

            linePaint.getTextBounds(maxCommit.toString(), 0, maxCommit.toString().length, textBounds)
        }
        val heightSpec = MeasureSpec.makeMeasureSpec(lineDistance.toInt() * maxLines + bottomOffset.toInt(), MeasureSpec.EXACTLY)
        super.onMeasure(widthMeasureSpec, heightSpec)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        maxHeight = height.toFloat() - (if (isInEditMode) textBounds.height() / 2 else textBounds.height()) - bottomOffset // hack for display view
        textDistance = 10.dp() + textPadding * 2 + textBounds.width()
        widthSpacing = (width - textDistance) / graphData.size
        val lineDist = (width - textDistance) / 7

        // Graph & calendar path calculation
        path.reset()
        calenderPath.reset()
        lineDatesUnix.clear()

        val exceptionalCase = graphData.size < 7
        if (exceptionalCase) {
            textDistance += 20.dp()
        }
        calenderPath.moveTo(textDistance, maxHeight)
        path.moveTo(textDistance, maxHeight)
        var startX = textDistance
        var c = 0
        graphData.forEachIndexed { _, gm ->
            val maxY = abs(maxHeight - (lineDistance * gm.commits / commits))
            path.lineTo(startX, maxY)
            if (exceptionalCase || startX >= (c + 1) * lineDist.toInt()) {
                calenderPath.moveTo(startX, maxHeight)
                calenderPath.lineTo(startX, maxHeight + lineHeight)
                lineLocationsX[c] = startX
                lineDatesUnix.add(Date(gm.starting_week * 1000))
                c++
            }
            startX += widthSpacing
        }
        path.lineTo(width.toFloat(), maxHeight)
        path.lineTo(startX, maxHeight)
    }

    override fun onDraw(canvas: Canvas?) {
        if (canvas == null) return
        super.onDraw(canvas)
        var decHeight = 0f
        for(i in 0 until maxLines) {
            canvas.drawText("${i * commits}", textPadding, height.toFloat() - bottomOffset - decHeight, commitTextPaint)
            canvas.drawLine(textDistance, maxHeight - decHeight, width.toFloat(), maxHeight - decHeight, linePaint)
            decHeight += lineDistance
        }
        canvas.drawPath(calenderPath, linePaint)

        canvas.withClip(path) {
            canvas.drawColor(graphPaint.color)
        }

        when {
            graphData.size <= 7 -> { // Very young project
                for(i in lineDatesUnix.indices) {
                    calendar.time = lineDatesUnix[i]
                    val month = calendar.getMonth()
                    val date = calendar.get(Calendar.DAY_OF_MONTH)
                    val x = lineLocationsX[i]
                    drawYear(canvas, x, "$month, $date")
                }
            }
            graphData.size <= 53 -> { // 52 + 1 weeks in a year
                for(i in lineDatesUnix.indices) {
                    calendar.time = lineDatesUnix[i]
                    val month = calendar.getMonth()
                    val x = lineLocationsX[i]
                    drawMonth(canvas, x, month)
                }
            }
            graphData.size > (52 * 7)  -> { // Can draw for 7+ yrs
                for(i in lineDatesUnix.indices) {
                    calendar.time = lineDatesUnix[i]
                    val year = calendar.get(Calendar.YEAR).toString()
                    val x = lineLocationsX[i]
                    drawYear(canvas, x, year)
                }
            }
            else -> { // Any other pattern, year & month will be shown
                var previousYear = ""
                for(i in lineDatesUnix.indices) {
                    calendar.time = lineDatesUnix[i]
                    val year = calendar.get(Calendar.YEAR).toString()
                    val month = calendar.getMonth()
                    val x = lineLocationsX[i]
                    if (i == 0) {
                        previousYear = year
                    }
                    if (previousYear != year) {
                        drawYear(canvas, x, year)
                    } else {
                        drawMonth(canvas, x, month)
                    }
                    previousYear = year
                }
            }
        }
    }

    private fun drawYear(canvas: Canvas, x: Float, year: String) {
        canvas.drawText(year,x - (bottomTextYearMaxSize / 2), maxHeight + lineHeight + bottomTextYearMaxSize / 2, timelineTextPaint)
    }

    private fun drawMonth(canvas: Canvas, x: Float, month: String) {
        canvas.drawText(month,x - (bottomTextMonthMaxSize / 2), maxHeight + lineHeight + bottomTextYearMaxSize / 2, timelineTextPaint)
    }

    private fun getNearestRoundTo5(n: Double): Int {
        return (round(n/5.0) * 5).toInt()
    }

    private fun Int.dp() = this * Resources.getSystem().displayMetrics.density
    private fun Int.sp() = this * Resources.getSystem().displayMetrics.scaledDensity
    private fun Calendar.getMonth() = getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.ROOT) ?: ""
}