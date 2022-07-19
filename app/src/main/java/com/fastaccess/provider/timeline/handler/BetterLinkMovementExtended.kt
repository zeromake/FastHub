package com.fastaccess.provider.timeline.handler

import android.content.Context
import android.graphics.RectF
import android.text.Selection
import android.text.Spannable
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.BackgroundColorSpan
import android.text.style.ClickableSpan
import android.text.style.URLSpan
import android.text.util.Linkify
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.TextView

/**
 * Created by Kosh on 23 Apr 2017, 12:09 PM
 *
 *
 * credit to to https://github.com/Saketme/Better-Link-Movement-Method
 */
class BetterLinkMovementExtended private constructor(context: Context) : LinkMovementMethod() {
    private var onLinkClickListener: ((TextView, String) -> Boolean)? = null
    private var onLinkLongClickListener: ((TextView, String) -> Boolean)? = null
    private val touchedLineBounds = RectF()
    private var isUrlHighlighted = false
    private var touchStartedOverLink = false
    private var activeTextViewHashcode = 0
    private val gestureDetector: GestureDetector
    private val clickGestureListener = LinkClickGestureListener()

    private inner class LinkClickGestureListener : SimpleOnGestureListener() {
        var listener: SimpleOnGestureListener? = null
        override fun onDown(e: MotionEvent): Boolean {
            listener?.onDown(e)
            return true
        }

        override fun onSingleTapUp(e: MotionEvent): Boolean {
            return listener?.onSingleTapUp(e)?:false
        }

        override fun onLongPress(e: MotionEvent) {
            listener?.onLongPress(e)
        }
    }

    fun setOnLinkClickListener(onLinkClickListener: (TextView, String) -> Boolean) {
        this.onLinkClickListener = onLinkClickListener
    }

    fun setOnLinkLongClickListener(onLinkLongClickListener: (TextView, String) -> Boolean) {
        this.onLinkLongClickListener = onLinkLongClickListener
    }

    override fun onTouchEvent(view: TextView, text: Spannable, event: MotionEvent): Boolean {
        if (activeTextViewHashcode != view.hashCode()) {
            activeTextViewHashcode = view.hashCode()
            view.autoLinkMask = 0
        }
        val touchedClickableSpan = findClickableSpanUnderTouch(view, text, event)
        if (touchedClickableSpan != null) {
            highlightUrl(view, touchedClickableSpan, text)
        } else {
            removeUrlHighlightColor(view)
        }
        clickGestureListener.listener = object : SimpleOnGestureListener() {
            override fun onDown(e: MotionEvent): Boolean {
                touchStartedOverLink = touchedClickableSpan != null
                return true
            }

            override fun onSingleTapUp(e: MotionEvent): Boolean {
                if (touchedClickableSpan != null && touchStartedOverLink) {
                    dispatchUrlClick(view, touchedClickableSpan)
                    removeUrlHighlightColor(view)
                }
                touchStartedOverLink = false
                return true
            }

            override fun onLongPress(e: MotionEvent) {
                if (touchedClickableSpan != null && touchStartedOverLink) {
                    dispatchUrlLongClick(view, touchedClickableSpan)
                    removeUrlHighlightColor(view)
                }
                touchStartedOverLink = false
            }
        }
        var ret = gestureDetector.onTouchEvent(event)
        if (!ret && event.action == MotionEvent.ACTION_UP) {
            clickGestureListener.listener = null
            removeUrlHighlightColor(view)
            touchStartedOverLink = false
            ret = true
        }
        return ret
    }

    private fun findClickableSpanUnderTouch(
        textView: TextView,
        text: Spannable,
        event: MotionEvent
    ): ClickableSpanWithText? {
        var touchX = event.x.toInt()
        var touchY = event.y.toInt()
        touchX -= textView.totalPaddingLeft
        touchY -= textView.totalPaddingTop
        touchX += textView.scrollX
        touchY += textView.scrollY
        val layout = textView.layout
        val touchedLine = layout.getLineForVertical(touchY)
        val touchOffset = layout.getOffsetForHorizontal(touchedLine, touchX.toFloat())
        touchedLineBounds.left = layout.getLineLeft(touchedLine)
        touchedLineBounds.top = layout.getLineTop(touchedLine).toFloat()
        touchedLineBounds.right = layout.getLineWidth(touchedLine) + touchedLineBounds.left
        touchedLineBounds.bottom = layout.getLineBottom(touchedLine).toFloat()
        return if (touchedLineBounds.contains(touchX.toFloat(), touchY.toFloat())) {
            val spans = text.getSpans(touchOffset, touchOffset, SPAN_CLASS)
            for (span in spans) {
                if (span is ClickableSpan) {
                    return ClickableSpanWithText.ofSpan(textView, span)
                }
            }
            null
        } else {
            null
        }
    }

    private fun highlightUrl(
        textView: TextView,
        spanWithText: ClickableSpanWithText,
        text: Spannable
    ) {
        if (!isUrlHighlighted) {
            isUrlHighlighted = true
            val spanStart = text.getSpanStart(spanWithText.span())
            val spanEnd = text.getSpanEnd(spanWithText.span())
            Selection.removeSelection(text)
            text.setSpan(
                BackgroundColorSpan(textView.highlightColor),
                spanStart,
                spanEnd,
                Spanned.SPAN_INCLUSIVE_INCLUSIVE
            )
            textView.text = text
            Selection.setSelection(text, spanStart, spanEnd)
        }
    }

    private fun removeUrlHighlightColor(textView: TextView) {
        if (isUrlHighlighted) {
            isUrlHighlighted = false
            val text = textView.text as Spannable
            val highlightSpans = text.getSpans(0, text.length, BackgroundColorSpan::class.java)
            for (highlightSpan in highlightSpans) {
                text.removeSpan(highlightSpan)
            }
            try {
                textView.text = text
                Selection.removeSelection(text)
            } catch (ignored: Exception) {
            }
        }
    }

    private fun dispatchUrlClick(textView: TextView, spanWithText: ClickableSpanWithText) {
        val spanUrl = spanWithText.text()
        val handled =
            onLinkClickListener != null && onLinkClickListener!!(textView, spanUrl)
        if (!handled) {
            spanWithText.span().onClick(textView)
        }
    }

    private fun dispatchUrlLongClick(textView: TextView, spanWithText: ClickableSpanWithText) {
        val spanUrl = spanWithText.text()
        if (onLinkLongClickListener != null) onLinkLongClickListener!!(
            textView,
            spanUrl
        )
    }

    internal class ClickableSpanWithText private constructor(
        private val span: ClickableSpan,
        private val text: String
    ) {
        fun span(): ClickableSpan {
            return span
        }

        fun text(): String {
            return text
        }

        companion object {
            fun ofSpan(textView: TextView, span: ClickableSpan): ClickableSpanWithText {
                val s = textView.text as Spanned
                val text: String = if (span is URLSpan) {
                    span.url
                } else {
                    val start = s.getSpanStart(span)
                    val end = s.getSpanEnd(span)
                    s.subSequence(start, end).toString()
                }
                return ClickableSpanWithText(span, text)
            }
        }
    }

    companion object {
        private val SPAN_CLASS: Class<ClickableSpan> = ClickableSpan::class.java
        private const val LINKIFY_NONE = -2
        private fun linkify(linkifyMask: Int, textView: TextView): BetterLinkMovementExtended {
            val movementMethod = BetterLinkMovementExtended(textView.context)
            addLinks(linkifyMask, movementMethod, textView)
            return movementMethod
        }

        fun linkifyHtml(textView: TextView): BetterLinkMovementExtended {
            return linkify(LINKIFY_NONE, textView)
        }

        private fun linkify(linkifyMask: Int, viewGroup: ViewGroup): BetterLinkMovementExtended {
            val movementMethod = BetterLinkMovementExtended(viewGroup.context)
            rAddLinks(linkifyMask, viewGroup, movementMethod)
            return movementMethod
        }

        fun linkifyHtml(viewGroup: ViewGroup): BetterLinkMovementExtended {
            return linkify(LINKIFY_NONE, viewGroup)
        }

        private fun rAddLinks(
            linkifyMask: Int,
            viewGroup: ViewGroup,
            movementMethod: BetterLinkMovementExtended
        ) {
            for (i in 0 until viewGroup.childCount) {
                val child = viewGroup.getChildAt(i)
                if (child is ViewGroup) {
                    rAddLinks(linkifyMask, child, movementMethod)
                } else if (child is TextView) {
                    addLinks(linkifyMask, movementMethod, child)
                }
            }
        }

        private fun addLinks(
            linkifyMask: Int,
            movementMethod: BetterLinkMovementExtended,
            textView: TextView
        ) {
            textView.movementMethod = movementMethod
            if (linkifyMask != LINKIFY_NONE) {
                Linkify.addLinks(textView, linkifyMask)
            }
        }
    }

    init {
        gestureDetector = GestureDetector(context, clickGestureListener)
    }
}