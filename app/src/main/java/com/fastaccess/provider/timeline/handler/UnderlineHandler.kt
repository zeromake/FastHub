package com.fastaccess.provider.timeline.handler

import android.text.SpannableStringBuilder
import android.text.style.UnderlineSpan
import net.nightwhistler.htmlspanner.TagNodeHandler
import org.htmlcleaner.TagNode

class UnderlineHandler : TagNodeHandler() {
    override fun handleTagNode(
        tagNode: TagNode,
        spannableStringBuilder: SpannableStringBuilder,
        start: Int,
        end: Int
    ) {
        spannableStringBuilder.setSpan(UnderlineSpan(), start, end, 33)
    }
}