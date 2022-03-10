package com.fastaccess.provider.markdown.extension.mention

import org.commonmark.node.CustomNode
import org.commonmark.node.Delimited

/**
 * Created by kosh on 20/08/2017.
 */
class Mention : CustomNode(), Delimited {
    override fun getOpeningDelimiter(): String {
        return DELIMITER
    }

    override fun getClosingDelimiter(): String {
        return " "
    }

    companion object {
        private const val DELIMITER = "@"
    }
}