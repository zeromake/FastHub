package com.fastaccess.provider.timeline.handler

import android.text.SpannableStringBuilder
import android.text.style.RelativeSizeSpan
import android.text.style.SuperscriptSpan
import net.nightwhistler.htmlspanner.TagNodeHandler
import org.htmlcleaner.TagNode

class SuperScriptHandler : TagNodeHandler() {
    override fun handleTagNode(
        node: TagNode,
        builder: SpannableStringBuilder,
        start: Int,
        end: Int
    ) {
        builder.setSpan(SuperscriptSpan(), start, end, 33)
        builder.setSpan(RelativeSizeSpan(0.8f), start, end, 33)
    }
}