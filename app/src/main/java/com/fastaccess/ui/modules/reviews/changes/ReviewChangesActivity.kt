package com.fastaccess.ui.modules.reviews.changes

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Spinner
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.evernote.android.state.State
import com.fastaccess.R
import com.fastaccess.data.dao.ReviewRequestModel
import com.fastaccess.databinding.AddReviewDialogLayoutBinding
import com.fastaccess.helper.BundleConstant
import com.fastaccess.helper.Bundler
import com.fastaccess.helper.InputHelper
import com.fastaccess.ui.base.BaseDialogFragment
import com.fastaccess.ui.delegate.viewBinding
import com.fastaccess.ui.modules.editor.comment.CommentEditorFragment

/**
 * Created by Kosh on 25 Jun 2017, 1:25 AM
 */
class ReviewChangesActivity : BaseDialogFragment<ReviewChangesMvp.View, ReviewChangesPresenter>(),
    ReviewChangesMvp.View {
    private val binding: AddReviewDialogLayoutBinding by viewBinding()

    val toolbar: Toolbar by lazy { this.binding.root.findViewById(R.id.toolbar) }
    val spinner: Spinner by lazy { this.binding.reviewMethod }

    @State
    var reviewRequest: ReviewRequestModel? = null
    @State
    var repoId: String? = null
    @State
    var owner: String? = null
    @State
    var number: Long? = null
    @State
    var isClosed: Boolean = false
    @State
    var isAuthor: Boolean = false

    private var submissionCallback: ReviewChangesMvp.ReviewSubmissionCallback? = null

    private val commentEditorFragment: CommentEditorFragment? by lazy {
        childFragmentManager.findFragmentByTag("commentContainer") as CommentEditorFragment?
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (parentFragment is ReviewChangesMvp.ReviewSubmissionCallback) {
            submissionCallback = parentFragment as ReviewChangesMvp.ReviewSubmissionCallback
        } else if (context is ReviewChangesMvp.ReviewSubmissionCallback) {
            submissionCallback = context
        }
    }

    override fun onDetach() {
        submissionCallback = null
        super.onDetach()
    }

    override fun providePresenter(): ReviewChangesPresenter = ReviewChangesPresenter()

    override fun fragmentLayout(): Int = R.layout.add_review_dialog_layout

    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            val fragment = CommentEditorFragment()
            fragment.arguments = Bundler.start().put(BundleConstant.YES_NO_EXTRA, true).end()
            childFragmentManager.beginTransaction()
                .replace(R.id.commentContainer, fragment, "commentContainer")
                .commit()
            val bundle = requireArguments()
            reviewRequest = bundle.getParcelable(BundleConstant.EXTRA)
            repoId = bundle.getString(BundleConstant.EXTRA_TWO)
            owner = bundle.getString(BundleConstant.EXTRA_THREE)
            number = bundle.getLong(BundleConstant.ID)
            isClosed = bundle.getBoolean(BundleConstant.EXTRA_FIVE)
            isAuthor = bundle.getBoolean(BundleConstant.EXTRA_FOUR)
        }
        toolbar.navigationIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_clear)
        toolbar.inflateMenu(R.menu.done_menu)
        toolbar.setNavigationOnClickListener { dismiss() }
        toolbar.setOnMenuItemClickListener {
            if (it.itemId == R.id.submit) {
                if (spinner.selectedItemPosition != 0 && commentEditorFragment?.getEditText()?.text.isNullOrEmpty()) {
                    commentEditorFragment?.getEditText()?.error = getString(R.string.required_field)
                } else {
                    commentEditorFragment?.getEditText()?.error = null
                    presenter.onSubmit(
                        reviewRequest!!,
                        repoId!!,
                        owner!!,
                        number!!,
                        InputHelper.toString(commentEditorFragment?.getEditText()?.text),
                        spinner.selectedItem as String
                    )
                }
            }
            return@setOnMenuItemClickListener true
        }

        if (isAuthor || isClosed) {
            spinner.setSelection(2, true)
            spinner.isEnabled = false
        }
    }

    override fun onSuccessfullySubmitted() {
        hideProgress()
        submissionCallback?.onSuccessfullyReviewed()
        dismiss()
    }

    override fun onErrorSubmitting() {
        showErrorMessage(getString(R.string.network_error))
    }

    override fun showMessage(titleRes: Int, msgRes: Int) {
        hideProgress()
        super.showMessage(titleRes, msgRes)
    }

    override fun showMessage(titleRes: String, msgRes: String) {
        hideProgress()
        super.showMessage(titleRes, msgRes)
    }

    override fun showErrorMessage(msgRes: String) {
        hideProgress()
        super.showErrorMessage(msgRes)
    }

    override fun onSendActionClicked(text: String, bundle: Bundle?) {}

    override fun onTagUser(username: String) {}

    override fun onClearEditText() {
        commentEditorFragment?.commentText?.setText("")
    }

    override fun getNamesToTag(): ArrayList<String> {
        return arrayListOf()
    }

    companion object {
        fun startForResult(
            reviewChanges: ReviewRequestModel, repoId: String, owner: String, number: Long,
            isAuthor: Boolean, isEnterprise: Boolean, isClosed: Boolean
        ): ReviewChangesActivity {
            val fragment = ReviewChangesActivity()
            val bundle = Bundler.start()
                .put(BundleConstant.EXTRA, reviewChanges)
                .put(BundleConstant.EXTRA_TWO, repoId)
                .put(BundleConstant.EXTRA_THREE, owner)
                .put(BundleConstant.EXTRA_FOUR, isAuthor)
                .put(BundleConstant.ID, number)
                .put(BundleConstant.IS_ENTERPRISE, isEnterprise)
                .put(BundleConstant.EXTRA_FIVE, isClosed)
                .end()
            fragment.arguments = bundle
            return fragment
        }
    }


}