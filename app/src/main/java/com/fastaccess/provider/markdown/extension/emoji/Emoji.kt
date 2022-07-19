package com.fastaccess.provider.markdown.extension.emoji

import org.commonmark.node.CustomNode
import org.commonmark.node.Delimited

/**
 * Created by kosh on 20/08/2017.
 */
class Emoji : CustomNode(), Delimited {
    override fun getOpeningDelimiter(): String {
        return DELIMITER
    }

    override fun getClosingDelimiter(): String {
        return DELIMITER
    }

    companion object {
        private const val DELIMITER = ":"
    }
}