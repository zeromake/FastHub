package com.fastaccess.ui.modules.changelog

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import com.fastaccess.R
import com.fastaccess.helper.PrefGetter.setWhatsNewVersion
import com.fastaccess.ui.base.BaseMvpBottomSheetDialogFragment
import com.fastaccess.ui.delegate.viewFind
import com.fastaccess.ui.widgets.FontButton
import com.fastaccess.ui.widgets.FontTextView
import com.fastaccess.utils.setOnThrottleClickListener
import com.prettifier.pretty.PrettifyWebView

/**
 * Created by Kosh on 26 Mar 2017, 10:15 PM
 */
class ChangelogBottomSheetDialog :
    BaseMvpBottomSheetDialogFragment<ChangelogMvp.View, ChangelogPresenter>(), ChangelogMvp.View {
    val title: FontTextView? by viewFind(R.id.title)
    val message: FontTextView? by viewFind(R.id.message)
    val cancel: FontButton? by viewFind(R.id.cancel)
    val prettifyWebView: PrettifyWebView? by viewFind(R.id.prettifyWebView)
    private val webProgress: ProgressBar? by viewFind(R.id.webProgress)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<View>(R.id.ok).setOnThrottleClickListener {
            dismiss()
        }
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
            prettifyWebView!!.setGithubContent(
                html, null,
                toggleNestScrolling = false,
                enableBridge = false,
                branch = ""
            )
            prettifyWebView!!.isNestedScrollingEnabled = false
        }
    }
}