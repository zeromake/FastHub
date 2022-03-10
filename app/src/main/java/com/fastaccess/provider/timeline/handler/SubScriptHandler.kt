package com.fastaccess.provider.timeline.handler

import net.nightwhistler.htmlspanner.TagNodeHandler
import org.htmlcleaner.TagNode
import android.text.SpannableStringBuilder
import android.text.style.SubscriptSpan
import android.text.style.RelativeSizeSpan

class SubScriptHandler : TagNodeHandler() {
    override fun handleTagNode(
        node: TagNode,
        builder: SpannableStringBuilder,
        start: Int,
        end: Int
    ) {
        builder.setSpan(SubscriptSpan(), start, end, 33)
        builder.setSpan(RelativeSizeSpan(0.8f), start, end, 33)
    }
}