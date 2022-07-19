package com.prettifier.pretty.callback

import android.webkit.JavascriptInterface
import com.prettifier.pretty.PrettifyWebView

/**
 * Created by Kosh on 13 Dec 2016, 3:01 PM
 */
class MarkDownInterceptorInterface @JvmOverloads constructor(
    private val prettifyWebView: PrettifyWebView?,
    private val toggleNestScrolling: Boolean = false
) {
    @JavascriptInterface
    fun startIntercept() {
        if (prettifyWebView != null) {
            prettifyWebView.setInterceptTouch(true)
            if (toggleNestScrolling) prettifyWebView.setEnableNestedScrolling(false)
        }
    }

    @JavascriptInterface
    fun stopIntercept() {
        if (prettifyWebView != null) {
            prettifyWebView.setInterceptTouch(false)
            if (toggleNestScrolling) prettifyWebView.setEnableNestedScrolling(true)
        }
    }
}