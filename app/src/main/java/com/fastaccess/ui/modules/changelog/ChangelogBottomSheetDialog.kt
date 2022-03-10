package com.fastaccess.ui.modules.changelog

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import butterknife.BindView
import butterknife.OnClick
import com.fastaccess.R
import com.fastaccess.helper.PrefGetter.setWhatsNewVersion
import com.fastaccess.ui.base.BaseMvpBottomSheetDialogFragment
import com.fastaccess.ui.widgets.FontButton
import com.fastaccess.ui.widgets.FontTextView
import com.prettifier.pretty.PrettifyWebView

/**
 * Created by Kosh on 26 Mar 2017, 10:15 PM
 */
class ChangelogBottomSheetDialog :
    BaseMvpBottomSheetDialogFragment<ChangelogMvp.View, ChangelogPresenter>(), ChangelogMvp.View {
    @JvmField
    @BindView(R.id.title)
    var title: FontTextView? = null

    @JvmField
    @BindView(R.id.message)
    var message: FontTextView? = null

    @JvmField
    @BindView(R.id.cancel)
    var cancel: FontButton? = null

    @JvmField
    @BindView(R.id.messageLayout)
    var messageLayout: View? = null

    @JvmField
    @BindView(R.id.prettifyWebView)
    var prettifyWebView: PrettifyWebView? = null

    @JvmField
    @BindView(R.id.webProgress)
    var webProgress: ProgressBar? = null
    @OnClick(R.id.ok)
    fun onOk() {
        dismiss()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState == null) {
            setWhatsNewVersion()
        }
        webProgress!!.visibility = View.VISIBLE
        cancel!!.visibility = View.GONE
        title!!.setText(R.string.changelog)
        if (presenter!!.html == null) {
            presenter!!.onLoadChangelog()
        } else {
            showChangelog(presenter!!.html)
        }
    }

    override fun fragmentLayout(): Int {
        return R.layout.message_dialog
    }

    override fun onChangelogLoaded(html: String?) {
        showChangelog(html)
    }

    override fun providePresenter(): ChangelogPresenter {
        return ChangelogPresenter()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        return dialog
    }

    private fun showChangelog(html: String?) {
        if (prettifyWebView == null) return
        webProgress!!.visibility = View.GONE
        if (html != null) {
            message!!.visibility = View.GONE
            prettifyWebView!!.visibility = View.VISIBLE
            prettifyWebView!!.setGithubContent(html, null,
                toggleNestScrolling = false,
                enableBridge = false,
                branch = ""
            )
            prettifyWebView!!.isNestedScrollingEnabled = false
        }
    }
}