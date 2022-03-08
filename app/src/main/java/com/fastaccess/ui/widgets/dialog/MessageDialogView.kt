package com.fastaccess.ui.widgets.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fastaccess.R
import com.fastaccess.helper.BundleConstant
import com.fastaccess.helper.Bundler
import com.fastaccess.helper.InputHelper
import com.fastaccess.ui.base.BaseBottomSheetDialog
import com.fastaccess.ui.widgets.FontButton
import com.fastaccess.ui.widgets.FontTextView
import com.fastaccess.utils.setOnThrottleClickListener
import com.prettifier.pretty.PrettifyWebView

/**
 * Created by Kosh on 16 Sep 2016, 2:15 PM
 */
class MessageDialogView : BaseBottomSheetDialog() {
    interface MessageDialogViewActionCallback {
        fun onMessageDialogActionClicked(isOk: Boolean, bundle: Bundle?)
        fun onDialogDismissed()
    }

    lateinit var prettifyWebView: PrettifyWebView
    lateinit var title: FontTextView
    lateinit var message: FontTextView
    lateinit var cancel: FontButton
    lateinit var ok: FontButton
    private var callback: MessageDialogViewActionCallback? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = super.onCreateView(inflater, container, savedInstanceState)!!
        this.prettifyWebView = root.findViewById(R.id.prettifyWebView)
        this.title = root.findViewById(R.id.title)
        this.message = root.findViewById(R.id.message)
        this.cancel = root.findViewById(R.id.cancel)
        this.ok = root.findViewById(R.id.ok)
        listOf<View>(this.ok, this.cancel).setOnThrottleClickListener {
            this.onClick(it)
        }
        val bundle = arguments
        title.text = bundle!!.getString("bundleTitle")
        val msg = bundle.getString("bundleMsg")
        if (bundle.getBoolean("isMarkDown")) {
            if (msg != null) {
                message.visibility = View.GONE
                prettifyWebView.visibility = View.VISIBLE
                prettifyWebView.setGithubContent(msg, null,
                    toggleNestScrolling = false,
                    enableBridge = false,
                    "",
                )
                prettifyWebView.isNestedScrollingEnabled = false
            }
        } else {
            message.text = msg
        }
        val hideCancel = bundle.getBoolean("hideCancel")
        if (hideCancel) cancel.visibility = View.GONE
        initButton(bundle)
        return root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (parentFragment != null && parentFragment is MessageDialogViewActionCallback) {
            callback = parentFragment as MessageDialogViewActionCallback?
        } else if (context is MessageDialogViewActionCallback) {
            callback = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        callback = null
    }

    fun onClick(view: View) {
        if (callback != null) {
            isAlreadyHidden = true
            callback!!.onMessageDialogActionClicked(
                view.id == R.id.ok,
                requireArguments().getBundle("bundle")
            )
        }
        dismiss()
    }

    override fun layoutRes(): Int {
        return R.layout.message_dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun initButton(bundle: Bundle) {
        val extra = bundle.getBundle("bundle")
        if (extra != null) {
            val yesNo = extra.getBoolean(BundleConstant.YES_NO_EXTRA)
            if (yesNo) {
                ok.setText(R.string.yes)
                cancel.setText(R.string.no)
            } else {
                val hideButtons = extra.getBoolean("hide_buttons")
                val primaryExtra = extra.getString("primary_extra")
                val secondaryExtra = extra.getString("secondary_extra")
                if (hideButtons) {
                    ok.visibility = View.GONE
                    cancel.visibility = View.GONE
                } else if (!InputHelper.isEmpty(primaryExtra)) {
                    ok.text = primaryExtra
                    if (!InputHelper.isEmpty(secondaryExtra)) cancel.text = secondaryExtra
                    ok.visibility = View.VISIBLE
                    cancel.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onDismissedByScrolling() {
        super.onDismissedByScrolling()
        if (callback != null) callback!!.onDialogDismissed()
    }

    override fun onHidden() {
        if (callback != null) callback!!.onDialogDismissed()
        super.onHidden()
    }

    companion object {
        @JvmField
        val TAG: String = MessageDialogView::class.java.simpleName

        @JvmStatic
        @JvmOverloads
        fun newInstance(
            bundleTitle: String, bundleMsg: String, isMarkDown: Boolean,
            bundle: Bundle? = null
        ): MessageDialogView {
            val messageDialogView = MessageDialogView()
            messageDialogView.arguments =
                getBundle(bundleTitle, bundleMsg, isMarkDown, bundle, false)
            return messageDialogView
        }

        @JvmStatic
        @JvmOverloads
        fun newInstance(
            bundleTitle: String, bundleMsg: String, isMarkDown: Boolean,
            hideCancel: Boolean, bundle: Bundle? = null
        ): MessageDialogView {
            val messageDialogView = MessageDialogView()
            messageDialogView.arguments = getBundle(
                bundleTitle,
                bundleMsg,
                isMarkDown,
                bundle,
                hideCancel
            )
            return messageDialogView
        }

        @JvmStatic
        @JvmOverloads
        fun newInstance(
            bundleTitle: String,
            bundleMsg: String,
            bundle: Bundle? = null
        ): MessageDialogView {
            return newInstance(bundleTitle, bundleMsg, false, bundle)
        }

        private fun getBundle(
            bundleTitle: String,
            bundleMsg: String,
            isMarkDown: Boolean,
            bundle: Bundle?,
            hideCancel: Boolean
        ): Bundle {
            return Bundler.start()
                .put("bundleTitle", bundleTitle)
                .put("bundleMsg", bundleMsg)
                .put("bundle", bundle)
                .put("isMarkDown", isMarkDown)
                .put("hideCancel", hideCancel)
                .end()
        }

        fun getYesNoBundle(context: Context): Bundle {
            return Bundler.start()
                .put("primary_extra", context.getString(R.string.yes))
                .put("secondary_extra", context.getString(R.string.no))
                .end()
        }


    }
}