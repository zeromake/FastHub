package com.fastaccess.provider.markdown.extension.emoji.internal

import com.fastaccess.provider.markdown.extension.emoji.Emoji
import org.commonmark.node.Node
import org.commonmark.node.Nodes
import org.commonmark.node.Text
import org.commonmark.parser.delimiter.DelimiterProcessor
import org.commonmark.parser.delimiter.DelimiterRun

class EmojiDelimiterProcessor : DelimiterProcessor {
    override fun getOpeningCharacter(): Char {
        return ':'
    }

    override fun getClosingCharacter(): Char {
        return ':'
    }

    override fun getMinLength(): Int {
        return 1
    }

    override fun process(openerRun: DelimiterRun, closerRun: DelimiterRun): Int {
        return if (openerRun.length() >= 1 && closerRun.length() >= 1) {
            val emoji: Node = Emoji()
            val opener: Text = openerRun.opener
            for (node in Nodes.between(opener, closerRun.closer)) {
                emoji.appendChild(node)
            }
            opener.insertAfter(emoji)
            1
        } else {
            0
        }
    }
}