package com.fastaccess.provider.timeline.handler

import android.graphics.Color
import net.nightwhistler.htmlspanner.handlers.PreHandler
import androidx.annotation.ColorInt
import com.fastaccess.helper.PrefGetter.ThemeType
import org.htmlcleaner.ContentNode
import org.htmlcleaner.TagNode
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.BackgroundColorSpan
import com.fastaccess.helper.PrefGetter
import android.text.style.ForegroundColorSpan
import android.text.style.TypefaceSpan

/**
 * Created by Kosh on 22 Apr 2017, 1:07 PM
 */
class PreTagHandler(
    @field:ColorInt private val color: Int,
    private val isPre: Boolean,
    @field:ThemeType private val theme: Int
) : PreHandler() {
    private fun getPlainText(buffer: StringBuffer, node: Any?) {
        if (node is ContentNode) {
            val text = node.content.toString()
            buffer.append(text)
        } else if (node is TagNode) {
            for (child in node.children) {
                getPlainText(buffer, child)
            }
        }
    }

    private fun replace(text: String): String {
        return text.replace("&nbsp;".toRegex(), "\u00A0")
            .replace("&amp;".toRegex(), "&")
            .replace("&quot;".toRegex(), "\"")
            .replace("&cent;".toRegex(), "¢")
            .replace("&lt;".toRegex(), "<")
            .replace("&gt;".toRegex(), ">")
            .replace("&sect;".toRegex(), "§")
            .replace("&ldquo;".toRegex(), "“")
            .replace("&rdquo;".toRegex(), "”")
            .replace("&lsquo;".toRegex(), "‘")
            .replace("&rsquo;".toRegex(), "’")
            .replace("&ndash;".toRegex(), "\u2013")
            .replace("&mdash;".toRegex(), "\u2014")
            .replace("&horbar;".toRegex(), "\u2015")
    }

    override fun handleTagNode(
        node: TagNode,
        builder: SpannableStringBuilder,
        start: Int,
        end: Int
    ) {
        if (isPre) {
            val buffer = StringBuffer()
            buffer.append("\n") //fake padding top + make sure, pre is always by itself
            getPlainText(buffer, node)
            buffer.append("\n") //fake padding bottom + make sure, pre is always by itself
            builder.append(replace(buffer.toString()))
            builder.append("\n")
            builder.setSpan(
                CodeBackgroundRoundedSpan(color),
                start,
                builder.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            builder.append("\n")
            appendNewLine(builder)
            appendNewLine(builder)
        } else {
            builder.append(" ")
            builder.append(replace(node.text.toString()))
            builder.append(" ")
            val stringStart = start + 1
            val stringEnd = builder.length - 1
            builder.setSpan(
                BackgroundColorSpan(color),
                stringStart,
                stringEnd,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            if (theme == PrefGetter.LIGHT) {
                builder.setSpan(
                    ForegroundColorSpan(Color.RED),
                    stringStart,
                    stringEnd,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            builder.setSpan(
                TypefaceSpan("monospace"),
                stringStart,
                stringEnd,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }
}