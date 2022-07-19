package com.fastaccess.ui.modules.repos.extras.locking

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatSpinner
import com.fastaccess.R
import com.fastaccess.ui.base.BaseBottomSheetDialog

/**
 * Created by Kosh on 10.02.18.
 */

class LockIssuePrBottomSheetDialog : BaseBottomSheetDialog() {
    private var lockIssuePrCallback: LockIssuePrCallback? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        lockIssuePrCallback = when {
            parentFragment is LockIssuePrCallback -> parentFragment as LockIssuePrCallback
            context is LockIssuePrCallback -> context
            else -> null
        }
    }

    override fun onDetach() {
        lockIssuePrCallback = null
        super.onDetach()
    }

    override fun layoutRes(): Int = R.layout.lock_issue_pr_dialog

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val cancel = view.findViewById<View>(R.id.cancel)
        val ok = view.findViewById<View>(R.id.ok)
        val lockReason = view.findViewById<AppCompatSpinner>(R.id.lockReason)
        cancel.setOnClickListener { dismiss() }
        ok.setOnClickListener {
            lockIssuePrCallback?.onLock(lockReason.selectedItem as String)
            dismiss()
        }
    }

    companion object {
        fun newInstance() = LockIssuePrBottomSheetDialog()
    }
}