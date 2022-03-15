package com.fastaccess.provider.timeline.handler

import net.nightwhistler.htmlspanner.TagNodeHandler
import androidx.annotation.ColorInt
import org.htmlcleaner.TagNode
import android.text.SpannableStringBuilder
import com.zzhoujay.markdown.style.MarkDownQuoteSpan

/**
 * Created by Kosh on 23 Apr 2017, 11:30 AM
 */
class QuoteHandler(@field:ColorInt private val color: Int) : TagNodeHandler() {
    override fun handleTagNode(
        node: TagNode,
        builder: SpannableStringBuilder,
        start: Int,
        end: Int
    ) {
        try {
            builder.setSpan(MarkDownQuoteSpan(color), start + 1, builder.length, 33)
        } catch (e: IndexOutOfBoundsException) {
            builder.setSpan(MarkDownQuoteSpan(color), start, builder.length, 33)
        }
    }
}
