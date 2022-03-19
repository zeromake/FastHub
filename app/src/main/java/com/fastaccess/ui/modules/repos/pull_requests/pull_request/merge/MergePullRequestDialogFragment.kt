package com.fastaccess.ui.modules.repos.pull_requests.pull_request.merge

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatSpinner
import butterknife.BindView
import butterknife.OnClick
import butterknife.OnItemSelected
import com.fastaccess.R
import com.fastaccess.helper.BundleConstant
import com.fastaccess.helper.Bundler.Companion.start
import com.fastaccess.helper.InputHelper.isEmpty
import com.fastaccess.helper.InputHelper.toString
import com.fastaccess.helper.PrefGetter.isProEnabled
import com.fastaccess.ui.base.BaseDialogFragment
import com.fastaccess.ui.modules.main.premium.PremiumActivity.Companion.startActivity
import com.fastaccess.ui.modules.repos.pull_requests.pull_request.merge.MergePullRequestMvp.MergeCallback
import com.google.android.material.textfield.TextInputLayout

/**
 * Created by Kosh on 18 Mar 2017, 12:13 PM
 */
class MergePullRequestDialogFragment :
    BaseDialogFragment<MergePullRequestMvp.View, MergePullRequestPresenter>(),
    MergePullRequestMvp.View {
    @JvmField
    @BindView(R.id.title)
    var title: TextInputLayout? = null

    @JvmField
    @BindView(R.id.mergeMethod)
    var mergeMethod: AppCompatSpinner? = null
    private var mergeCallback: MergeCallback? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MergeCallback) {
            mergeCallback = context
        } else if (parentFragment is MergeCallback) {
            mergeCallback = parentFragment as MergeCallback?
        }
    }

    override fun onDetach() {
        mergeCallback = null
        super.onDetach()
    }

    override fun fragmentLayout(): Int {
        return R.layout.merge_dialog_layout
    }

    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            val titleMsg = requireArguments().getString(BundleConstant.EXTRA)
            if (!isEmpty(titleMsg)) {
                if (title!!.editText != null) title!!.editText!!.setText(titleMsg)
            }
        }
    }

    override fun providePresenter(): MergePullRequestPresenter {
        return MergePullRequestPresenter()
    }

    @OnClick(R.id.cancel, R.id.ok)
    fun onClick(view: View) {
        if (view.id == R.id.ok) {
            val isEmpty = isEmpty(title)
            title!!.error = if (isEmpty) getString(R.string.required_field) else null
            if (isEmpty) return
            mergeCallback!!.onMerge(
                toString(title),
                mergeMethod!!.selectedItem.toString().lowercase()
            )
        }
        dismiss()
    }

    @OnItemSelected(R.id.mergeMethod)
    fun onItemSelect(position: Int) {
        if (position > 0) {
            if (!isProEnabled) {
                mergeMethod!!.setSelection(0)
                startActivity(requireContext())
            }
        }
    }

    companion object {
        fun newInstance(title: String?): MergePullRequestDialogFragment {
            val view = MergePullRequestDialogFragment()
            view.arguments = start()
                .put(BundleConstant.EXTRA, title)
                .end()
            return view
        }
    }
}