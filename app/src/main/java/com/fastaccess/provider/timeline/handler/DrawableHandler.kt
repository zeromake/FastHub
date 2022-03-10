package com.fastaccess.provider.timeline.handler

import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ImageSpan
import android.view.View
import android.widget.TextView
import com.fastaccess.helper.InputHelper.isEmpty
import com.fastaccess.helper.PrefGetter.isAutoImageDisabled
import com.fastaccess.provider.scheme.SchemeParser.launchUri
import com.fastaccess.provider.timeline.handler.drawable.DrawableGetter
import com.fastaccess.ui.widgets.SpannableBuilder.Companion.builder
import net.nightwhistler.htmlspanner.TagNodeHandler
import net.nightwhistler.htmlspanner.spans.CenterSpan
import org.htmlcleaner.TagNode

/**
 * Created by Kosh on 22 Apr 2017, 1:09 PM
 */
class DrawableHandler(private val textView: TextView?, private val width: Int) : TagNodeHandler() {
    private val isNull: Boolean
        get() = textView == null

    override fun handleTagNode(
        node: TagNode,
        builder: SpannableStringBuilder,
        start: Int,
        end: Int
    ) {
        val src = node.getAttributeByName("src")
        if (!isEmpty(src)) {
            if (!isAutoImageDisabled) {
                builder.append("￼")
                if (isNull) return
                builder.append("\n")
                val imageGetter = DrawableGetter(textView!!, width)
                builder.setSpan(
                    ImageSpan(imageGetter.getDrawable(src)),
                    start,
                    builder.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                builder.setSpan(
                    CenterSpan(),
                    start,
                    builder.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            } else {
                builder.append(builder().clickable("Image") { v: View ->
                    launchUri(
                        v.context,
                        src
                    )
                })
            }
            builder.append("\n")
        }
    }
}