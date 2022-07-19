package com.fastaccess.provider.timeline.handler

import android.graphics.Typeface
import android.text.SpannableStringBuilder
import com.zzhoujay.markdown.style.FontSpan
import net.nightwhistler.htmlspanner.TagNodeHandler
import org.htmlcleaner.TagNode

/**
 * Created by Kosh on 06 May 2017, 11:02 AM
 */
class ItalicHandler : TagNodeHandler() {
    override fun handleTagNode(
        node: TagNode,
        builder: SpannableStringBuilder,
        start: Int,
        end: Int
    ) {
        builder.setSpan(FontSpan(1F, Typeface.ITALIC), start, builder.length, 33)
    }
}