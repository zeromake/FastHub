package com.fastaccess.provider.timeline.handler

import android.text.SpannableStringBuilder
import com.fastaccess.helper.Logger.e
import com.fastaccess.provider.emoji.EmojiManager.getForAlias
import net.nightwhistler.htmlspanner.TagNodeHandler
import org.htmlcleaner.TagNode

/**
 * Created by Kosh on 27 May 2017, 4:54 PM
 */
class EmojiHandler : TagNodeHandler() {
    override fun handleTagNode(
        node: TagNode,
        builder: SpannableStringBuilder,
        start: Int,
        end: Int
    ) {
        val emoji = node.getAttributeByName("alias")
        if (emoji != null) {
            val unicode = getForAlias(emoji)
            if (unicode != null) {
                builder.replace(start, end, " " + unicode.unicode + " ")
            }
        } else if (node.text != null) {
            e(node.text)
            val unicode = getForAlias(node.text.toString())
            if (unicode != null) {
                builder.replace(start, end, " " + unicode.unicode + " ")
            }
        }
    }

    override fun beforeChildren(node: TagNode, builder: SpannableStringBuilder) {
        super.beforeChildren(node, builder)
    }
}