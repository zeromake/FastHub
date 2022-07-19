package com.fastaccess.provider.markdown.extension.mention.internal

import com.fastaccess.provider.markdown.extension.mention.Mention
import org.commonmark.node.Node
import org.commonmark.node.Nodes
import org.commonmark.parser.delimiter.DelimiterProcessor
import org.commonmark.parser.delimiter.DelimiterRun

class MentionDelimiterProcessor : DelimiterProcessor {
    override fun getOpeningCharacter(): Char {
        return '@'
    }

    override fun getClosingCharacter(): Char {
        return ' '
    }

    override fun getMinLength(): Int {
        return 1
    }

    override fun process(openingRun: DelimiterRun, closingRun: DelimiterRun): Int {
        return if (openingRun.length() >= 1 && closingRun.length() >= 1) {
            val mention: Node = Mention()
            val opener = openingRun.opener
            for (node in Nodes.between(opener, closingRun.closer)) {
                mention.appendChild(node)
            }
            opener.insertAfter(mention)
            1
        } else {
            0
        }
    }
}