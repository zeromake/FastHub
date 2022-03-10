package com.fastaccess.provider.markdown.extension.mention

import com.fastaccess.provider.markdown.extension.mention.internal.MentionDelimiterProcessor
import com.fastaccess.provider.markdown.extension.mention.internal.MentionNodeRenderer
import org.commonmark.Extension
import org.commonmark.parser.Parser
import org.commonmark.parser.Parser.ParserExtension
import org.commonmark.renderer.html.HtmlRenderer
import org.commonmark.renderer.html.HtmlRenderer.HtmlRendererExtension

/**
 * Created by kosh on 20/08/2017.
 */
class MentionExtension private constructor() : ParserExtension, HtmlRendererExtension {
    override fun extend(parserBuilder: Parser.Builder) {
        parserBuilder.customDelimiterProcessor(MentionDelimiterProcessor())
    }

    override fun extend(rendererBuilder: HtmlRenderer.Builder) {
        rendererBuilder.nodeRendererFactory { context ->
            MentionNodeRenderer(
                context
            )
        }
    }

    companion object {
        fun create(): Extension {
            return MentionExtension()
        }
    }
}