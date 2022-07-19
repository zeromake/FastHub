package com.fastaccess.provider.timeline.handler

import android.graphics.Color
import android.text.SpannableStringBuilder
import com.zzhoujay.markdown.style.LinkSpan
import net.nightwhistler.htmlspanner.TagNodeHandler
import org.htmlcleaner.TagNode

/**
 * Created by Kosh on 10 May 2017, 8:46 PM
 */
class LinkHandler : TagNodeHandler() {
    override fun handleTagNode(
        node: TagNode,
        spannableStringBuilder: SpannableStringBuilder,
        start: Int,
        end: Int
    ) {
        val href = node.getAttributeByName("href")
        if (href != null) {
            spannableStringBuilder.setSpan(LinkSpan(href, linkColor), start, end, 33)
        } else if (node.text != null) {
            spannableStringBuilder.setSpan(
                LinkSpan(
                    "https://github.com/" + node.text.toString(),
                    linkColor
                ), start, end, 33
            )
        }
    }

    companion object {
        private val linkColor = Color.parseColor("#4078C0")
    }
}