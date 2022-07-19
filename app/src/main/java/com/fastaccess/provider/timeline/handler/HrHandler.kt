package com.fastaccess.provider.timeline.handler

import android.text.SpannableStringBuilder
import android.text.Spanned
import net.nightwhistler.htmlspanner.TagNodeHandler
import net.nightwhistler.htmlspanner.spans.CenterSpan
import org.htmlcleaner.TagNode

/**
 * Created by kosh on 30/07/2017.
 */
class HrHandler(private val color: Int, private val width: Int, private val isHeader: Boolean) :
    TagNodeHandler() {
    override fun handleTagNode(
        tagNode: TagNode,
        spannableStringBuilder: SpannableStringBuilder,
        i: Int,
        i1: Int
    ) {
        spannableStringBuilder.append("\n")
        val builder = SpannableStringBuilder("$")
        val hrSpan = HrSpan(color, width)
        builder.setSpan(hrSpan, 0, builder.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        builder.setSpan(CenterSpan(), 0, builder.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        builder.append("\n")
        spannableStringBuilder.append(builder)
    }
}