package com.fastaccess.ui.widgets.dialog

import android.app.Dialog
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import com.fastaccess.R
import com.fastaccess.helper.Bundler
import com.fastaccess.ui.base.BaseDialogFragment
import com.fastaccess.ui.base.mvp.BaseMvp
import com.fastaccess.ui.base.mvp.presenter.BasePresenter

/**
 * Created by Kosh on 09 Dec 2016, 5:18 PM
 */
class ProgressDialogFragment : BaseDialogFragment<BaseMvp.FAView, BasePresenter<BaseMvp.FAView>>() {
    override fun fragmentLayout(): Int {
        return R.layout.progress_dialog_layout
    }

    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {}
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setCancelable(false)
        isCancelable = false
        val window = dialog.window
        if (window != null) {
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window.setDimAmount(0f)
        }
        return dialog
    }

    override fun providePresenter(): BasePresenter<BaseMvp.FAView> {
        return BasePresenter()
    }

    companion object {
        val TAG: String = ProgressDialogFragment::class.java.simpleName
        fun newInstance(
            resources: Resources,
            @StringRes msgId: Int,
            isCancelable: Boolean
        ): ProgressDialogFragment {
            return newInstance(resources.getString(msgId), isCancelable)
        }

        fun newInstance(msg: String, isCancelable: Boolean): ProgressDialogFragment {
            val fragment = ProgressDialogFragment()
            fragment.arguments = Bundler.start()
                .put("msg", msg)
                .put("isCancelable", isCancelable)
                .end()
            return fragment
        }
    }

    init {
        suppressAnimation = true
    }
}