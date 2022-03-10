package com.fastaccess.provider.timeline.handler

import android.graphics.*
import net.nightwhistler.htmlspanner.TagNodeHandler
import org.htmlcleaner.TagNode
import android.text.style.ImageSpan
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.*
import android.text.style.AlignmentSpan
import java.util.ArrayList

/**
 * Handles simple HTML tables.
 *
 *
 * Since it renders these tables itself, it needs to know things like font size
 * and text colour to use.
 *
 * @author Alex Kuiper
 */
class TableHandler : TagNodeHandler() {
    private var tableWidth = 500
    private val typeFace = Typeface.DEFAULT
    private val textSize = 28f
    private var textColor = Color.BLACK
    override fun rendersContent(): Boolean {
        return true
    }

    override fun handleTagNode(
        node: TagNode,
        builder: SpannableStringBuilder,
        start: Int,
        end: Int
    ) {
        val table = getTable(node)
        for (i in table.rows.indices) {
            val row: List<Spanned> = table.rows[i]
            builder.append("\uFFFC")
            val drawable = TableRowDrawable(row, table.isDrawBorder)
            drawable.setBounds(
                0, 0, drawable.intrinsicWidth,
                drawable.intrinsicHeight
            )
            builder.setSpan(ImageSpan(drawable), start + i, builder.length, 33)
        }
        builder.append("\uFFFC")
        val drawable: Drawable = TableRowDrawable(ArrayList<Spanned>(), table.isDrawBorder)
        drawable.setBounds(0, 0, tableWidth, 1)
        builder.setSpan(
            ImageSpan(drawable), builder.length - 1, builder.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        builder.setSpan(
            AlignmentSpan { Layout.Alignment.ALIGN_CENTER },
            start,
            builder.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        builder.append("\n")
    }

    fun setTableWidth(tableWidth: Int) {
        this.tableWidth = tableWidth
    }

    fun setTextColor(textColor: Int) {
        this.textColor = textColor
    }

    private fun readNode(node: Any, table: Table) {
        if (node is TagNode) {
            if (node.name == "td" || node.name == "th") {
                val result: Spanned = spanner.fromTagNode(node)
                table.addCell(result)
                return
            }
            if (node.name == "tr") {
                table.addRow()
            }
            for (child in node.childTags) {
                readNode(child, table)
            }
        }
    }

    private fun getTable(node: TagNode): Table {
        val border = node.getAttributeByName("border")
        val drawBorder = "0" != border
        val result = Table(drawBorder)
        readNode(node, result)
        return result
    }

    private val textPaint: TextPaint
        get() {
            val textPaint = TextPaint()
            textPaint.color = textColor
            textPaint.linkColor = textColor
            textPaint.isAntiAlias = true
            textPaint.textSize = textSize
            textPaint.typeface = typeFace
            return textPaint
        }

    private fun calculateRowHeight(row: List<Spanned>): Int {
        if (row.isEmpty()) {
            return 0
        }
        val textPaint = textPaint
        val columnWidth = tableWidth / row.size
        var rowHeight = 0
        for (cell in row) {
            val layout = buildStaticLayout(
                cell,
                textPaint,
                columnWidth - 2 * PADDING,
                Layout.Alignment.ALIGN_NORMAL,
                0.5f,
                1.5f,
                true,
            )
            if (layout.height > rowHeight) {
                rowHeight = layout.height
            }
        }
        return rowHeight
    }

    private inner class TableRowDrawable(
        private val tableRow: List<Spanned>,
        private val paintBorder: Boolean
    ) : Drawable() {
        private val rowHeight: Int = calculateRowHeight(tableRow)
        override fun draw(canvas: Canvas) {
            val paint = Paint()
            paint.color = textColor
            paint.style = Paint.Style.STROKE
            val numberOfColumns = tableRow.size
            if (numberOfColumns == 0) {
                return
            }
            val columnWidth = tableWidth / numberOfColumns
            var offset: Int
            for (i in 0 until numberOfColumns) {
                offset = i * columnWidth
                if (paintBorder) {
                    // The rect is open at the bottom, so there's a single line
                    // between rows.
                    canvas.drawRect(
                        offset.toFloat(),
                        0f,
                        (offset + columnWidth).toFloat(),
                        rowHeight.toFloat(),
                        paint
                    )
                }
                val layout = buildStaticLayout(
                    tableRow[i],
                    textPaint,
                    columnWidth - 2 * PADDING,
                    Layout.Alignment.ALIGN_NORMAL,
                    0.5f,
                    1.5f,
                    true,
                )
                canvas.translate((offset + PADDING).toFloat(), 0f)
                layout.draw(canvas)
                canvas.translate((-1 * (offset + PADDING)).toFloat(), 0f)
            }
        }

        override fun getIntrinsicHeight(): Int {
            return rowHeight
        }

        override fun getIntrinsicWidth(): Int {
            return tableWidth
        }

        override fun getOpacity(): Int {
            return PixelFormat.OPAQUE
        }

        override fun setAlpha(alpha: Int) {}
        override fun setColorFilter(cf: ColorFilter?) {}

    }

    private inner class Table constructor(val isDrawBorder: Boolean) {
        private val content: MutableList<MutableList<Spanned>> = ArrayList()
        fun addRow() {
            content.add(ArrayList())
        }

        val bottomRow: MutableList<Spanned>
            get() = content[content.size - 1]
        val rows: List<MutableList<Spanned>>
            get() = content

        fun addCell(text: Spanned) {
            check(content.isNotEmpty()) { "No rows added yet" }
            bottomRow.add(text)
        }
    }

    companion object {
        private const val PADDING = 20

        @JvmStatic
        private fun buildStaticLayout(
            source: CharSequence,
            paint: TextPaint,
            width: Int,
            alignment: Layout.Alignment,
            spacingAdd: Float,
            spacingMult: Float,
            includePad: Boolean,
        ): StaticLayout {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                StaticLayout.Builder.obtain(
                    source,
                    0,
                    source.length,
                    paint,
                    width
                )
                    .setAlignment(alignment)
                    .setLineSpacing(spacingAdd, spacingMult)
                    .setIncludePad(includePad)
                    .build()
            } else {
                StaticLayout(
                    source,
                    paint,
                    width,
                    alignment,
                    spacingMult,
                    spacingAdd,
                    includePad,
                )
            }
        }

    }
}