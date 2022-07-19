package com.fastaccess.provider.timeline.handler

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.LeadingMarginSpan
import net.nightwhistler.htmlspanner.TagNodeHandler
import org.htmlcleaner.TagNode

/**
 * Created by Kosh on 29 Apr 2017, 11:59 PM
 */
class MarginHandler : TagNodeHandler() {
    override fun beforeChildren(node: TagNode, builder: SpannableStringBuilder) {
        if (builder.isNotEmpty() && builder[builder.length - 1].code != 10) { //'10 = \n'
            appendNewLine(builder)
        }
    }

    override fun handleTagNode(
        node: TagNode,
        builder: SpannableStringBuilder,
        start: Int,
        end: Int
    ) {
        builder.setSpan(
            LeadingMarginSpan.Standard(30),
            start,
            end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        appendNewLine(builder)
    }
}