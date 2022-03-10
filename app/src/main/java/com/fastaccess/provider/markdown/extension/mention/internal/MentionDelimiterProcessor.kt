package com.fastaccess.provider.markdown.extension.mention.internal

import org.commonmark.parser.delimiter.DelimiterProcessor
import org.commonmark.parser.delimiter.DelimiterRun
import com.fastaccess.provider.markdown.extension.mention.Mention
import org.commonmark.node.Node
import org.commonmark.node.Text

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

    override fun getDelimiterUse(opener: DelimiterRun, closer: DelimiterRun): Int {
        return if (opener.length() >= 1 && closer.length() >= 1) {
            1
        } else {
            0
        }
    }

    override fun process(opener: Text, closer: Text, delimiterCount: Int) {
        val mention: Node = Mention()
        var tmp = opener.next
        while (tmp != null && tmp !== closer) {
            val next = tmp.next
            mention.appendChild(tmp)
            tmp = next
        }
        opener.insertAfter(mention)
    }
}