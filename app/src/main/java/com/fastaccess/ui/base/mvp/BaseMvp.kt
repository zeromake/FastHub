package com.fastaccess.ui.base.mvp

import android.os.Bundle
import androidx.annotation.StringRes
import com.fastaccess.ui.widgets.dialog.MessageDialogView.MessageDialogViewActionCallback
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import net.grandcentrix.thirtyinch.TiView
import net.grandcentrix.thirtyinch.callonmainthread.CallOnMainThread

/**
 * Created by Kosh on 25 May 2016, 9:09 PM
 */
interface BaseMvp {
    interface FAView : TiView, MessageDialogViewActionCallback, OnScrollTopListener {
        @CallOnMainThread
        fun showProgress(@StringRes resId: Int)

        @CallOnMainThread
        fun showBlockingProgress(@StringRes resId: Int)

        @CallOnMainThread
        fun hideProgress()

        @CallOnMainThread
        fun showMessage(@StringRes titleRes: Int, @StringRes msgRes: Int)

        @CallOnMainThread
        fun showMessage(titleRes: String, msgRes: String)

        @CallOnMainThread
        fun showErrorMessage(msgRes: String)
        val isLoggedIn: Boolean
        fun onRequireLogin()
        fun onLogoutPressed()
        fun onThemeChanged()
        fun onOpenSettings()
        val isEnterprise: Boolean
        fun onOpenUrlInBrowser()
    }

    interface FAPresenter {
        fun onSaveInstanceState(outState: Bundle?)
        fun onRestoreInstanceState(outState: Bundle?)
        fun manageDisposable(vararg disposables: Disposable?)
        fun <T> manageObservable(observable: Observable<T>?)
        fun manageViewDisposable(vararg disposables: Disposable?)
        val isApiCalled: Boolean
        fun onSubscribed(cancelable: Boolean)
        fun onError(throwable: Throwable)
        fun <T> makeRestCall(observable: Observable<T>, onNext: Consumer<T>)
        fun <T> makeRestCall(observable: Observable<T>, onNext: Consumer<T>, cancelable: Boolean)
        fun onCheckGitHubStatus()
    }

    interface PaginationListener<P> {
        var currentPage: Int
        var previousTotal: Int
        fun onCallApi(page: Int, parameter: P?): Boolean
    }

    interface OnScrollTopListener {
        fun onScrollTop(index: Int)
    }
}