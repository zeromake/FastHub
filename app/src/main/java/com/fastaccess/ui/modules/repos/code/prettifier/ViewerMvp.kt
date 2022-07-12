package com.fastaccess.ui.modules.repos.code.prettifier

import android.os.Bundle
import androidx.annotation.StringRes
import com.fastaccess.ui.base.mvp.BaseMvp.FAPresenter
import com.fastaccess.ui.base.mvp.BaseMvp.FAView
import com.prettifier.pretty.PrettifyWebView.OnContentChangedListener

/**
 * Created by Kosh on 27 Nov 2016, 3:41 PM
 */
interface ViewerMvp {
    interface View : FAView, OnContentChangedListener {
        fun onSetImageUrl(url: String, isSvg: Boolean)
        fun onSetMdText(text: String, baseUrl: String?, replace: Boolean, branch: String?)
        fun onSetCode(text: String)
        fun onShowError(msg: String)
        fun onShowError(@StringRes msg: Int)
        fun onShowMdProgress()
        fun openUrl(url: String)
        fun onViewAsCode()
    }

    interface Presenter : FAPresenter {
        fun onHandleIntent(intent: Bundle?)
        fun onLoadContentAsStream()
        fun downloadedStream(): String?
        val isMarkDown: Boolean
        fun onWorkOffline()
        fun onWorkOnline()
        val isRepo: Boolean
        val isImage: Boolean
        fun url(): String
    }
}