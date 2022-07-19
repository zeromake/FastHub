package com.prettifier.pretty.helper

/**
 * Created by Kosh on 25 Dec 2016, 9:12 PM
 */
object PrettifyHelper {
    private fun getHtmlContent(css: String, text: String, wrap: Boolean, isDark: Boolean): String {
        return """${HtmlHelper.HTML_HEADER}
${HtmlHelper.HEAD_HEADER}
    <link rel="stylesheet" href="./styles/$css">
    ${if (!wrap) HtmlHelper.MEAT_VIEWPORT_PRETTIFY else ""}
    $LINE_NO_CSS
    ${if (wrap) WRAPPED_STYLE else ""}
    <script src="./js/prettify.js"></script>
    <script src="./js/prettify_line_number.js"></script>
${HtmlHelper.HEAD_BOTTOM}
<body${if (isDark && textTooLarge(text)) " style=\"color:white;\"" else ""}>
<pre><code>$text</code></pre>
${if (textTooLarge(text)) "" else "<script>\nhljs.initHighlightingOnLoad();\nhljs.initLineNumbersOnLoad();\n</script>"}

<script src="./js/scrollto.js"></script>
${HtmlHelper.BODY_BOTTOM}
${HtmlHelper.HTML_BOTTOM}"""
    }

    private const val WRAPPED_STYLE = "<style>\n " +
            "td.hljs-ln-code {\n" +
            "    word-wrap: break-word !important;\n" +
            "    word-break: break-all  !important;\n" +
            "    white-space: pre-wrap  !important;\n" +
            "}" +
            "img {\n" +
            "    max-width: 100% !important;\n" +
            "}\n" +
            "ol {\n" +
            "    margin-left: 0 !important;\n" +
            "    padding-left: 6px !important;\n" +
            "}\n" +
            "ol li {\n" +
            "    margin-left: 0  !important;\n" +
            "    padding-left: 0  !important;\n" +
            "    text-indent: -12px !important;\n" +
            "}" +
            "</style>"
    private const val LINE_NO_CSS = "<style>\n " +
            "td.hljs-ln-numbers {\n" +
            "    -webkit-touch-callout: none;\n" +
            "    -webkit-user-select: none;\n" +
            "    -khtml-user-select: none;\n" +
            "    -moz-user-select: none;\n" +
            "    -ms-user-select: none;\n" +
            "    user-select: none;\n" +
            "    text-align: center;\n" +
            "    color: #ccc;\n" +
            "    border-right: 1px solid #CCC;\n" +
            "    vertical-align: top;\n" +
            "    padding-right: 3px !important;\n" +
            "}\n" +
            "\n" +
            ".hljs-ln-line {\n" +
            "    margin-left: 6px !important;\n" +
            "}\n" +
            "</style>"

    fun generateContent(source: String, theme: String): String {
        return getHtmlContent(theme, getFormattedSource(source), wrap = false, isDark = false)
    }

    fun generateContent(source: String, isDark: Boolean, wrap: Boolean): String {
        return getHtmlContent(getStyle(isDark), getFormattedSource(source), wrap, isDark)
    }

    private fun getFormattedSource(source: String): String {
        return source.replace("<".toRegex(), "&lt;")
            .replace(">".toRegex(), "&gt;")
    }

    private fun getStyle(isDark: Boolean): String {
        return CodeThemesHelper.getTheme(isDark)
    }

    private fun textTooLarge(text: String): Boolean {
        return text.length > 304800 //>roughly 300kb ? disable highlighting to avoid crash.
    }
}