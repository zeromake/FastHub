package com.fastaccess.provider.markdown.extension.emoji

import com.fastaccess.provider.markdown.extension.emoji.internal.EmojiDelimiterProcessor
import com.fastaccess.provider.markdown.extension.emoji.internal.EmojiNodeRenderer
import org.commonmark.Extension
import org.commonmark.parser.Parser
import org.commonmark.parser.Parser.ParserExtension
import org.commonmark.renderer.html.HtmlRenderer
import org.commonmark.renderer.html.HtmlRenderer.HtmlRendererExtension

/**
 * Created by kosh on 20/08/2017.
 */
class EmojiExtension private constructor() : ParserExtension, HtmlRendererExtension {
    override fun extend(parserBuilder: Parser.Builder) {
        parserBuilder.customDelimiterProcessor(EmojiDelimiterProcessor())
    }

    override fun extend(rendererBuilder: HtmlRenderer.Builder) {
        rendererBuilder.nodeRendererFactory { context ->
            EmojiNodeRenderer(
                context
            )
        }
    }

    companion object {
        fun create(): Extension {
            return EmojiExtension()
        }
    }
}