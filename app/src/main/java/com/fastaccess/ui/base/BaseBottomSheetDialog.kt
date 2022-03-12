package com.fastaccess.ui.base

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.annotation.LayoutRes
import androidx.appcompat.view.ContextThemeWrapper
import butterknife.ButterKnife
import butterknife.Unbinder
import com.evernote.android.state.StateSaver
import com.fastaccess.R
import com.fastaccess.helper.ViewHelper
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * Created by Kosh on 16 Sep 2016, 2:11 PM
 */
abstract class BaseBottomSheetDialog : BottomSheetDialogFragment() {
    protected var bottomSheetBehavior: BottomSheetBehavior<View>? = null
    private val bottomSheetCallback: BottomSheetCallback = object : BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                isAlreadyHidden = true
                onHidden()
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            if (slideOffset == -1.0F) {
                isAlreadyHidden = true
                onDismissedByScrolling()
            }
        }
    }
    protected var isAlreadyHidden = false
    private var unbinder: Unbinder? = null
    @LayoutRes
    protected abstract fun layoutRes(): Int

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        StateSaver.saveInstanceState(this, outState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null && !savedInstanceState.isEmpty) {
            StateSaver.restoreInstanceState(this, savedInstanceState)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val contextThemeWrapper: Context = ContextThemeWrapper(
            context, requireContext().theme
        )
        val themeAwareInflater = inflater.cloneInContext(contextThemeWrapper)
        val view = themeAwareInflater.inflate(layoutRes(), container, false)
        unbinder = ButterKnife.bind(this, view)
        view.viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                view.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val parent = dialog?.findViewById<View>(R.id.design_bottom_sheet)
                if (parent != null) {
                    bottomSheetBehavior = BottomSheetBehavior.from(parent)
                    bottomSheetBehavior!!.addBottomSheetCallback(bottomSheetCallback)
                    bottomSheetBehavior!!.state = BottomSheetBehavior.STATE_EXPANDED
                }
            }
        })
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (unbinder != null) unbinder!!.unbind()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener {
            if (ViewHelper.isTablet(requireActivity())) {
                if (dialog.window != null) {
                    dialog.window!!.setLayout(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            }
            onDialogIsShowing()
        }
        dialog.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                isAlreadyHidden = true
                onDismissedByScrolling()
            }
            false
        }
        return dialog
    }

    override fun onDetach() {
        if (!isAlreadyHidden) {
            onDismissedByScrolling()
        }
        super.onDetach()
    }

    protected open fun onHidden() {
        try {
            dismiss()
        } catch (ignored: IllegalStateException) {
        } //FML FIXME
    }

    protected open fun onDismissedByScrolling() {}
    private fun onDialogIsShowing() {}
}