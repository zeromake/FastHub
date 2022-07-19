package com.fastaccess.ui.base

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.view.ContextThemeWrapper
import com.evernote.android.state.StateSaver
import com.fastaccess.R
import com.fastaccess.helper.AnimHelper.dismissDialog
import com.fastaccess.helper.AnimHelper.revealDialog
import com.fastaccess.helper.AppHelper
import com.fastaccess.helper.PrefGetter.isAppAnimationDisabled
import com.fastaccess.ui.base.mvp.BaseMvp.FAView
import com.fastaccess.ui.base.mvp.presenter.BasePresenter
import com.fastaccess.ui.widgets.dialog.ProgressDialogFragment
import net.grandcentrix.thirtyinch.TiDialogFragment

/**
 * Created by Kosh on 22 Feb 2017, 7:28 PM
 */
abstract class BaseDialogFragment<V : FAView, P : BasePresenter<V>> : TiDialogFragment<P, V>(),
    FAView {
    protected var callback: FAView? = null

    @JvmField
    protected var suppressAnimation = false

    @LayoutRes
    protected abstract fun fragmentLayout(): Int
    protected abstract fun onFragmentCreated(view: View, savedInstanceState: Bundle?)
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FAView) {
            callback = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        callback = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        StateSaver.saveInstanceState(this, outState)
        presenter!!.onSaveInstanceState(outState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(
            STYLE_NO_TITLE, if (AppHelper.isNightMode(
                    resources
                )
            ) R.style.DialogThemeDark else R.style.DialogThemeLight
        )
        if (savedInstanceState != null && !savedInstanceState.isEmpty) {
            StateSaver.restoreInstanceState(this, savedInstanceState)
            presenter!!.onRestoreInstanceState(savedInstanceState)
        }
        presenter!!.isEnterprise = isEnterprise
    }

    override fun dismiss() {
        if (suppressAnimation) {
            super.dismiss()
            return
        }
        if (isAppAnimationDisabled) {
            super.dismiss()
        } else {
            dismissDialog(this, resources.getInteger(android.R.integer.config_shortAnimTime),
                object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        super@BaseDialogFragment.dismiss()
                    }
                })
        }
    }

    @SuppressLint("RestrictedApi")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (fragmentLayout() != 0) {
            val contextThemeWrapper: Context = ContextThemeWrapper(
                context, requireContext().theme
            )
            val themeAwareInflater = inflater.cloneInContext(contextThemeWrapper)
            return themeAwareInflater.inflate(fragmentLayout(), container, false)
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        if (!isAppAnimationDisabled && this !is ProgressDialogFragment && !suppressAnimation) {
            val longAnimTime = resources.getInteger(android.R.integer.config_longAnimTime)
            dialog.setOnShowListener {
                revealDialog(
                    dialog,
                    longAnimTime
                )
            }
        }
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onFragmentCreated(view, savedInstanceState)
    }

    override fun showProgress(@StringRes resId: Int) {
        callback!!.showProgress(resId)
    }

    override fun showBlockingProgress(resId: Int) {
        callback!!.showBlockingProgress(resId)
    }

    override fun hideProgress() {
        callback!!.hideProgress()
    }

    override fun showMessage(@StringRes titleRes: Int, @StringRes msgRes: Int) {
        callback!!.showMessage(titleRes, msgRes)
    }

    override fun showMessage(titleRes: String, msgRes: String) {
        callback!!.showMessage(titleRes, msgRes)
    }

    override fun showErrorMessage(msgRes: String) {
        callback!!.showErrorMessage(msgRes)
    }

    override val isLoggedIn: Boolean
        get() = callback!!.isLoggedIn

    override fun onMessageDialogActionClicked(isOk: Boolean, bundle: Bundle?) {}
    override fun onRequireLogin() {
        callback!!.onRequireLogin()
    }

    override fun onLogoutPressed() {
        callback!!.onLogoutPressed()
    }

    override fun onThemeChanged() {
        callback!!.onThemeChanged()
    }

    override fun onOpenSettings() {
        callback!!.onOpenSettings()
    }

    override fun onScrollTop(index: Int) {}
    override fun onDialogDismissed() {}
    override val isEnterprise: Boolean
        get() = callback != null && callback!!.isEnterprise

    override fun onOpenUrlInBrowser() {
        callback!!.onOpenUrlInBrowser()
    }
}