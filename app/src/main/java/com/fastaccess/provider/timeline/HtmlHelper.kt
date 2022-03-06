package com.fastaccess.provider.timeline

import android.annotation.SuppressLint
import android.graphics.Color
import android.net.Uri
import android.view.HapticFeedbackConstants
import android.view.MenuItem
import android.widget.PopupMenu
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.fastaccess.R
import com.fastaccess.helper.AppHelper
import com.fastaccess.helper.PrefGetter
import com.fastaccess.helper.PrefGetter.ThemeType
import com.fastaccess.helper.ViewHelper
import com.fastaccess.provider.scheme.SchemeParser.launchUri
import com.fastaccess.provider.timeline.handler.*
import net.nightwhistler.htmlspanner.HtmlSpanner
import net.nightwhistler.htmlspanner.handlers.BoldHandler

/**
 * Created by Kosh on 21 Apr 2017, 11:24 PM
 */
object HtmlHelper {
    @JvmStatic
    fun htmlIntoTextView(textView: TextView, html: String, width: Int) {
        registerClickEvent(textView)
        textView.text =
            initHtml(textView, width).fromHtml(format(html).toString())
    }

    @SuppressLint("NonConstantResourceId")
    private fun registerClickEvent(textView: TextView) {
        val betterLinkMovementMethod = BetterLinkMovementExtended.linkifyHtml(textView)
        betterLinkMovementMethod.setOnLinkClickListener { view: TextView, url: String? ->
            launchUri(view.context, Uri.parse(url))
            true
        }
        betterLinkMovementMethod.setOnLinkLongClickListener { view: TextView, url: String? ->
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
            val menu = PopupMenu(view.context, view)
            menu.setOnMenuItemClickListener { menuItem: MenuItem ->
                when (menuItem.itemId) {
                    R.id.copy -> {
                        AppHelper.copyToClipboard(view.context, url!!)
                        return@setOnMenuItemClickListener true
                    }
                    R.id.open -> {
                        launchUri(view.context, Uri.parse(url))
                        return@setOnMenuItemClickListener true
                    }
                    R.id.open_new_window -> {
                        launchUri(view.context, Uri.parse(url), false, true)
                        return@setOnMenuItemClickListener true
                    }
                    else -> return@setOnMenuItemClickListener false
                }
            }
            menu.inflate(R.menu.link_popup_menu)
            menu.show()
            true
        }
    }

    private fun initHtml(textView: TextView, width: Int): HtmlSpanner {
        @ThemeType val theme = PrefGetter.getThemeType()
        @ColorInt val windowBackground = getWindowBackground(theme)
        val checked = ContextCompat.getDrawable(textView.context, R.drawable.ic_checkbox_small)
        val unchecked =
            ContextCompat.getDrawable(textView.context, R.drawable.ic_checkbox_empty_small)
        val mySpanner = HtmlSpanner()
        mySpanner.isStripExtraWhiteSpace = true
        mySpanner.registerHandler("pre", PreTagHandler(windowBackground, true, theme))
        mySpanner.registerHandler("code", PreTagHandler(windowBackground, false, theme))
        mySpanner.registerHandler("img", DrawableHandler(textView, width))
        mySpanner.registerHandler("g-emoji", EmojiHandler())
        mySpanner.registerHandler("blockquote", QuoteHandler(windowBackground))
        mySpanner.registerHandler("b", BoldHandler())
        mySpanner.registerHandler("strong", BoldHandler())
        mySpanner.registerHandler("i", ItalicHandler())
        mySpanner.registerHandler("em", ItalicHandler())
        mySpanner.registerHandler("ul", MarginHandler())
        mySpanner.registerHandler("ol", MarginHandler())
        mySpanner.registerHandler("li", ListsHandler(checked, unchecked))
        mySpanner.registerHandler("u", UnderlineHandler())
        mySpanner.registerHandler("strike", StrikethroughHandler())
        mySpanner.registerHandler("ins", UnderlineHandler())
        mySpanner.registerHandler("del", StrikethroughHandler())
        mySpanner.registerHandler("sub", SubScriptHandler())
        mySpanner.registerHandler("sup", SuperScriptHandler())
        mySpanner.registerHandler("a", LinkHandler())
        mySpanner.registerHandler("hr", HrHandler(windowBackground, width, false))
        mySpanner.registerHandler("emoji", EmojiHandler())
        mySpanner.registerHandler("mention", LinkHandler())
        mySpanner.registerHandler("h1", HeaderHandler(1.5f))
        mySpanner.registerHandler("h2", HeaderHandler(1.4f))
        mySpanner.registerHandler("h3", HeaderHandler(1.3f))
        mySpanner.registerHandler("h4", HeaderHandler(1.2f))
        mySpanner.registerHandler("h5", HeaderHandler(1.1f))
        mySpanner.registerHandler("h6", HeaderHandler(1.0f))
        if (width > 0) {
            val tableHandler = TableHandler()
            tableHandler.setTextColor(ViewHelper.generateTextColor(windowBackground))
            tableHandler.setTableWidth(width)
            mySpanner.registerHandler("table", tableHandler)
        }
        return mySpanner
    }

    @JvmStatic
    @ColorInt
    fun getWindowBackground(@ThemeType theme: Int): Int {
        return when (theme) {
            PrefGetter.AMLOD -> {
                Color.parseColor("#0B162A")
            }
            PrefGetter.BLUISH -> {
                Color.parseColor("#111C2C")
            }
            PrefGetter.DARK -> {
                Color.parseColor("#22252A")
            }
            else -> {
                Color.parseColor("#EEEEEE")
            }
        }
    }

    private const val TOGGLE_START = "<span class=\"email-hidden-toggle\">"
    private const val TOGGLE_END = "</span>"
    private const val REPLY_START = "<div class=\"email-quoted-reply\">"
    private const val REPLY_END = "</div>"
    private const val SIGNATURE_START = "<div class=\"email-signature-reply\">"
    private const val SIGNATURE_END = "</div>"
    private const val HIDDEN_REPLY_START =
        "<div class=\"email-hidden-reply\" style=\" display:none\">"
    private const val HIDDEN_REPLY_END = "</div>"
    private const val BREAK = "<br>"
    private const val PARAGRAPH_START = "<p>"
    private const val PARAGRAPH_END = "</p>"

    //https://github.com/k0shk0sh/GitHubSdk/blob/master/library/src/main/java/com/meisolsson/githubsdk/core/HtmlUtils.java
    fun format(html: String?): CharSequence {
        if (html == null || html.isEmpty()) return ""
        val formatted = StringBuilder(html)
        strip(formatted, TOGGLE_START, TOGGLE_END)
        strip(formatted, SIGNATURE_START, SIGNATURE_END)
        strip(formatted, REPLY_START, REPLY_END)
        strip(formatted, HIDDEN_REPLY_START, HIDDEN_REPLY_END)
        if (replace(formatted, PARAGRAPH_START, BREAK)) replace(formatted, PARAGRAPH_END, BREAK)
        trim(formatted)
        return formatted
    }

    private fun strip(input: StringBuilder, prefix: String, suffix: String) {
        var start = input.indexOf(prefix)
        while (start != -1) {
            var end = input.indexOf(suffix, start + prefix.length)
            if (end == -1) end = input.length
            input.delete(start, end + suffix.length)
            start = input.indexOf(prefix, start)
        }
    }

    private fun replace(input: StringBuilder, from: String, to: String): Boolean {
        var start = input.indexOf(from)
        if (start == -1) return false
        val fromLength = from.length
        val toLength = to.length
        while (start != -1) {
            input.replace(start, start + fromLength, to)
            start = input.indexOf(from, start + toLength)
        }
        return true
    }

    private fun trim(input: StringBuilder) {
        var length = input.length
        val breakLength = BREAK.length
        while (length > 0) {
            if (input.indexOf(BREAK) == 0) input.delete(
                0,
                breakLength
            ) else if (length >= breakLength && input.lastIndexOf(
                    BREAK
                ) == length - breakLength
            ) input.delete(length - breakLength, length) else if (Character.isWhitespace(
                    input[0]
                )
            ) input.deleteCharAt(0) else if (Character.isWhitespace(input[length - 1])) input.deleteCharAt(
                length - 1
            ) else break
            length = input.length
        }
    }
}