package com.fastaccess.provider.markdown

import android.os.Build
import android.text.Html
import android.view.ViewTreeObserver
import android.webkit.MimeTypeMap
import android.widget.EditText
import android.widget.TextView
import com.fastaccess.helper.InputHelper.isEmpty
import com.fastaccess.helper.InputHelper.toString
import com.fastaccess.helper.Logger.e
import com.fastaccess.provider.markdown.extension.emoji.EmojiExtension
import com.fastaccess.provider.markdown.extension.mention.MentionExtension
import com.fastaccess.provider.timeline.HtmlHelper.htmlIntoTextView
import org.commonmark.ext.autolink.AutolinkExtension
import org.commonmark.ext.front.matter.YamlFrontMatterExtension
import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension
import org.commonmark.ext.gfm.tables.TablesExtension
import org.commonmark.ext.ins.InsExtension
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import java.util.*

/**
 * Created by Kosh on 24 Nov 2016, 7:43 PM
 */
object MarkDownProvider {
    private val IMAGE_EXTENSIONS = arrayOf(".png", ".jpg", ".jpeg", ".gif", ".svg")
    private val MARKDOWN_EXTENSIONS = arrayOf(
        ".md", ".mkdn", ".mdwn", ".mdown", ".markdown", ".mkd", ".mkdown", ".ron", ".rst", "adoc"
    )
    private val ARCHIVE_EXTENSIONS = arrayOf(
        ".zip",
        ".7z",
        ".rar",
        ".tar.gz",
        ".tgz",
        ".tar.Z",
        ".tar.bz2",
        ".tbz2",
        ".tar.lzma",
        ".tlz",
        ".apk",
        ".jar",
        ".dmg",
        ".pdf",
        ".ico",
        ".docx",
        ".doc",
        ".xlsx",
        ".hwp",
        ".pptx",
        ".show",
        ".mp3",
        ".ogg",
        ".ipynb"
    )

    @JvmStatic
    fun setMdText(textView: TextView, markdown: String?) {
        if (!isEmpty(markdown)) {
            val width = textView.measuredWidth
            if (width > 0) {
                render(textView, markdown, width)
            } else {
                textView.viewTreeObserver.addOnPreDrawListener(object :
                    ViewTreeObserver.OnPreDrawListener {
                    override fun onPreDraw(): Boolean {
                        textView.viewTreeObserver.removeOnPreDrawListener(this)
                        render(textView, markdown, textView.measuredWidth)
                        return true
                    }
                })
            }
        }
    }

    fun setMdText(textView: TextView, markdown: String?, width: Int) {
        if (!isEmpty(markdown)) {
            render(textView, markdown, width)
        }
    }

    internal fun render(textView: TextView, markdown: String?, width: Int) {
        val extensions = listOf(
            StrikethroughExtension.create(),
            AutolinkExtension.create(),
            TablesExtension.create(),
            InsExtension.create(),
            EmojiExtension.create(),
            MentionExtension.create(),
            YamlFrontMatterExtension.create()
        )
        val parser = Parser.builder()
            .extensions(extensions)
            .build()
        try {
            val node = parser.parse(markdown)
            val rendered = HtmlRenderer
                .builder()
                .extensions(extensions)
                .build()
                .render(node)
            htmlIntoTextView(
                textView,
                rendered,
                width - (textView.paddingStart + textView.paddingEnd)
            )
        } catch (ignored: Exception) {
            htmlIntoTextView(
                textView,
                markdown!!,
                width - (textView.paddingStart + textView.paddingEnd)
            )
        }
    }

    @JvmStatic
    fun stripMdText(textView: TextView, markdown: String?) {
        if (!isEmpty(markdown)) {
            val parser = Parser.builder().build()
            val node = parser.parse(markdown)
            textView.text = stripHtml(HtmlRenderer.builder().build().render(node))
        }
    }

    @JvmStatic
    fun stripMdText(markdown: String?): String {
        if (!isEmpty(markdown)) {
            val parser = Parser.builder().build()
            val node = parser.parse(markdown)
            return stripHtml(HtmlRenderer.builder().build().render(node))
        }
        return ""
    }

    @JvmStatic
    private fun stripHtml(html: String?): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY).toString()
        } else {
            Html.fromHtml(html).toString()
        }
    }

    fun addList(editText: EditText, list: String) {
        val tag = "$list "
        val source = editText.text.toString()
        var selectionStart = editText.selectionStart
        val selectionEnd = editText.selectionEnd
        var substring = source.substring(0, selectionStart)
        val line = substring.lastIndexOf(10.toChar())
        selectionStart = if (line != -1) {
            line + 1
        } else {
            0
        }
        substring = source.substring(selectionStart, selectionEnd)
        val split = substring.split("\n").toTypedArray()
        val stringBuffer = StringBuilder()
        if (split.isNotEmpty()) for (s in split) {
            if (s.isEmpty() && stringBuffer.isNotEmpty()) {
                stringBuffer.append("\n")
                continue
            }
            if (!s.trim { it <= ' ' }.startsWith(tag)) {
                if (stringBuffer.isNotEmpty()) stringBuffer.append("\n")
                stringBuffer.append(tag).append(s)
            } else {
                if (stringBuffer.isNotEmpty()) stringBuffer.append("\n")
                stringBuffer.append(s)
            }
        }
        if (stringBuffer.isEmpty()) {
            stringBuffer.append(tag)
        }
        editText.text.replace(selectionStart, selectionEnd, stringBuffer.toString())
        editText.setSelection(stringBuffer.length + selectionStart)
    }

    fun addHeader(editText: EditText, level: Int) {
        val source = editText.text.toString()
        val selectionStart = editText.selectionStart
        val selectionEnd = editText.selectionEnd
        val result = StringBuilder()
        val substring = source.substring(selectionStart, selectionEnd)
        if (!hasNewLine(source, selectionStart)) result.append("\n")
        (0 until level).forEach { _ -> result.append("#") }
        result.append(" ").append(substring)
        editText.text.replace(selectionStart, selectionEnd, result.toString())
        editText.setSelection(selectionStart + result.length)
    }

    fun addItalic(editText: EditText) {
        val source = editText.text.toString()
        val selectionStart = editText.selectionStart
        val selectionEnd = editText.selectionEnd
        val substring = source.substring(selectionStart, selectionEnd)
        val result = "_" + substring + "_ "
        editText.text.replace(selectionStart, selectionEnd, result)
        editText.setSelection(result.length + selectionStart - 2)
    }

    fun addBold(editText: EditText) {
        val source = editText.text.toString()
        val selectionStart = editText.selectionStart
        val selectionEnd = editText.selectionEnd
        val substring = source.substring(selectionStart, selectionEnd)
        val result = "**$substring** "
        editText.text.replace(selectionStart, selectionEnd, result)
        editText.setSelection(result.length + selectionStart - 3)
    }

    fun addCode(editText: EditText) {
        try {
            val source = editText.text.toString()
            val selectionStart = editText.selectionStart
            val selectionEnd = editText.selectionEnd
            val substring = source.substring(selectionStart, selectionEnd)
            val result: String = if (hasNewLine(
                    source,
                    selectionStart
                )
            ) "```\n$substring\n```\n" else "\n```\n$substring\n```\n"
            editText.text.replace(selectionStart, selectionEnd, result)
            editText.setSelection(result.length + selectionStart - 5)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun addInlinleCode(editText: EditText) {
        val source = editText.text.toString()
        val selectionStart = editText.selectionStart
        val selectionEnd = editText.selectionEnd
        val substring = source.substring(selectionStart, selectionEnd)
        val result = "`$substring` "
        editText.text.replace(selectionStart, selectionEnd, result)
        editText.setSelection(result.length + selectionStart - 2)
    }

    fun addStrikeThrough(editText: EditText) {
        val source = editText.text.toString()
        val selectionStart = editText.selectionStart
        val selectionEnd = editText.selectionEnd
        val substring = source.substring(selectionStart, selectionEnd)
        val result = "~~$substring~~ "
        editText.text.replace(selectionStart, selectionEnd, result)
        editText.setSelection(result.length + selectionStart - 3)
    }

    fun addQuote(editText: EditText) {
        val source = editText.text.toString()
        val selectionStart = editText.selectionStart
        val selectionEnd = editText.selectionEnd
        val substring = source.substring(selectionStart, selectionEnd)
        val result: String = if (hasNewLine(source, selectionStart)) {
            "> $substring"
        } else {
            "\n> $substring"
        }
        editText.text.replace(selectionStart, selectionEnd, result)
        editText.setSelection(result.length + selectionStart)
    }

    fun addDivider(editText: EditText) {
        val source = editText.text.toString()
        val selectionStart = editText.selectionStart
        val result: String = if (hasNewLine(source, selectionStart)) {
            "-------\n"
        } else {
            "\n-------\n"
        }
        editText.text.replace(selectionStart, selectionStart, result)
        editText.setSelection(result.length + selectionStart)
    }

    fun addPhoto(editText: EditText, title: String, link: String) {
        val result = "![" + toString(title) + "](" + toString(link) + ")"
        insertAtCursor(editText, result)
    }

    fun addLink(editText: EditText, title: String, link: String) {
        val result = "[" + toString(title) + "](" + toString(link) + ")"
        insertAtCursor(editText, result)
    }

    private fun hasNewLine(source: String, selectionStart: Int): Boolean {
        if (source.isEmpty()) {
            return true
        }
        val selectSource = source.substring(
            0,
            selectionStart
        )
        if (selectSource.isEmpty()) {
            return true
        }
        return selectSource.last().code == 10
    }

    @JvmStatic
    fun isImage(name: String?): Boolean {
        if (name.isNullOrEmpty()) return false
        val text = name.lowercase(Locale.getDefault())
        for (value in IMAGE_EXTENSIONS) {
            val extension = MimeTypeMap.getFileExtensionFromUrl(text)
            if (extension != null && value.replace(
                    ".",
                    ""
                ) == extension || text.endsWith(value)
            ) return true
        }
        return false
    }

    @JvmStatic
    fun isMarkdown(name: String?): Boolean {
        if (name.isNullOrEmpty()) return false
        val text = name.lowercase(Locale.getDefault())
        for (value in MARKDOWN_EXTENSIONS) {
            val extension = MimeTypeMap.getFileExtensionFromUrl(text)
            if (extension != null && value.replace(".", "") == extension ||
                text.equals("README", ignoreCase = true) || text.endsWith(value)
            ) return true
        }
        return false
    }

    @JvmStatic
    fun isArchive(name: String?): Boolean {
        if (name.isNullOrEmpty()) return false
        val text = name.lowercase(Locale.getDefault())
        for (value in ARCHIVE_EXTENSIONS) {
            val extension = MimeTypeMap.getFileExtensionFromUrl(text)
            if (extension != null && value.replace(
                    ".",
                    ""
                ) == extension || text.endsWith(value)
            ) return true
        }
        return false
    }

    fun insertAtCursor(editText: EditText, text: String) {
        val oriContent = editText.text.toString()
        val start = editText.selectionStart
        val end = editText.selectionEnd
        e(start, end)
        if (start >= 0 && end > 0 && start != end) {
            editText.text = editText.text.replace(start, end, text)
        } else {
            val index = if (editText.selectionStart >= 0) editText.selectionStart else 0
            e(start, end, index)
            val builder = StringBuilder(oriContent)
            builder.insert(index, text)
            editText.setText(builder.toString())
            editText.setSelection(index + text.length)
        }
    }
}