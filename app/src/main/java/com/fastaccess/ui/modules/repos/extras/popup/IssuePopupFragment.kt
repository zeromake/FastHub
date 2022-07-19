package com.fastaccess.ui.modules.repos.extras.popup

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentManager
import com.fastaccess.R
import com.fastaccess.data.dao.LabelListModel
import com.fastaccess.data.dao.LabelModel
import com.fastaccess.data.dao.MilestoneModel
import com.fastaccess.data.dao.PullsIssuesParser.Companion.getForIssue
import com.fastaccess.data.dao.PullsIssuesParser.Companion.getForPullRequest
import com.fastaccess.data.entity.Issue
import com.fastaccess.data.entity.PullRequest
import com.fastaccess.data.entity.User
import com.fastaccess.helper.AnimHelper.mimicFabVisibility
import com.fastaccess.helper.BundleConstant
import com.fastaccess.helper.Bundler.Companion.start
import com.fastaccess.helper.InputHelper.isEmpty
import com.fastaccess.helper.InputHelper.toString
import com.fastaccess.provider.markdown.MarkDownProvider.setMdText
import com.fastaccess.provider.scheme.LinkParserHelper.isEnterprise
import com.fastaccess.ui.base.BaseMvpBottomSheetDialogFragment
import com.fastaccess.ui.delegate.viewFind
import com.fastaccess.ui.widgets.AvatarLayout
import com.fastaccess.ui.widgets.FontEditText
import com.fastaccess.ui.widgets.FontTextView
import com.fastaccess.ui.widgets.LabelSpan
import com.fastaccess.ui.widgets.SpannableBuilder.Companion.builder
import com.fastaccess.utils.setOnThrottleClickListener
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * Created by Kosh on 27 May 2017, 12:54 PM
 */
class IssuePopupFragment :
    BaseMvpBottomSheetDialogFragment<IssuePopupMvp.View, IssuePopupPresenter>(),
    IssuePopupMvp.View {
    val toolbar: Toolbar? by viewFind(R.id.toolbar)
    val appbar: AppBarLayout? by viewFind(R.id.appbar)
    val avatarLayout: AvatarLayout? by viewFind(R.id.avatarLayout)
    val name: FontTextView? by viewFind(R.id.name)
    val body: FontTextView? by viewFind(R.id.body)
    val assignee: FontTextView? by viewFind(R.id.assignee)
    private val assigneeLayout: LinearLayout? by viewFind(R.id.assigneeLayout)
    val title: FontTextView? by viewFind(R.id.title)
    val labels: FontTextView? by viewFind(R.id.labels)
    private val labelsLayout: LinearLayout? by viewFind(R.id.labelsLayout)
    val milestoneTitle: FontTextView? by viewFind(R.id.milestoneTitle)
    val milestoneDescription: FontTextView? by viewFind(R.id.milestoneDescription)
    private val milestoneLayout: LinearLayout? by viewFind(R.id.milestoneLayout)
    val comment: FontEditText? by viewFind(R.id.comment)
    val submit: FloatingActionButton? by viewFind(R.id.submit)
    val commentSection: LinearLayout? by viewFind(R.id.commentSection)
    val progressBar: ProgressBar? by viewFind(R.id.progressBar)

    fun onSubmit() {
        val isEmpty = isEmpty(comment)
        if (!isEmpty) {
            presenter!!.onSubmit(
                requireArguments().getString(BundleConstant.EXTRA_SEVEN)!!,
                requireArguments().getString(BundleConstant.EXTRA_EIGHT)!!,
                requireArguments().getInt(BundleConstant.ID),
                toString(comment)
            )
        } else {
            showMessage(R.string.error, R.string.required_field)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        submit!!.setOnThrottleClickListener {
            onSubmit()
        }

        toolbar!!.setNavigationIcon(R.drawable.ic_clear)
        toolbar!!.setNavigationOnClickListener { dismiss() }
        val bundle = arguments
        val titleString = bundle!!.getString(BundleConstant.EXTRA)
        val bodyString = bundle.getString(BundleConstant.EXTRA_TWO)
        val user: User? = bundle.getParcelable(BundleConstant.EXTRA_THREE)
        val assigneeModel: User? = bundle.getParcelable(BundleConstant.EXTRA_FOUR)
        val labelsList: ArrayList<LabelModel>? =
            bundle.getParcelableArrayList(BundleConstant.EXTRA_FIVE)
        val milestoneModel: MilestoneModel? = bundle.getParcelable(BundleConstant.EXTRA_SIX)
        val canComment = bundle.getBoolean(BundleConstant.YES_NO_EXTRA)
        commentSection!!.visibility = if (canComment) View.VISIBLE else View.GONE
        toolbar!!.title = String.format("#%s", bundle.getInt(BundleConstant.ID))
        title!!.text = titleString
        setMdText(body!!, bodyString)
        if (user != null) {
            name!!.text = user.login
            avatarLayout!!.setUrl(user.avatarUrl, user.login, false, isEnterprise(user.url))
        }
        if (assigneeModel == null) {
            assigneeLayout!!.visibility = View.GONE
        } else {
            assignee!!.text = assigneeModel.login
        }
        if (labelsList == null || labelsList.isEmpty()) {
            labelsLayout!!.visibility = View.GONE
        } else {
            val builder = builder()
            for (label in labelsList) {
                val color = Color.parseColor("#" + label.color)
                builder.append(" ").append(" " + label.name + " ", LabelSpan(color))
            }
            labels!!.text = builder
        }
        if (milestoneModel == null) {
            milestoneLayout!!.visibility = View.GONE
        } else {
            milestoneTitle!!.text = milestoneModel.title
            milestoneDescription!!.text = milestoneModel.description
            if (milestoneModel.description == null) {
                milestoneDescription!!.visibility = View.GONE
            }
        }
    }

    override fun fragmentLayout(): Int {
        return R.layout.issue_popup_layout
    }

    override fun providePresenter(): IssuePopupPresenter {
        return IssuePopupPresenter()
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

    override fun showProgress(resId: Int) {
        submit!!.hide()
        mimicFabVisibility(true, progressBar!!, null)
    }

    override fun hideProgress() {
        mimicFabVisibility(false, progressBar!!, null)
        submit!!.show()
    }

    override fun onSuccessfullySubmitted() {
        showMessage(R.string.success, R.string.successfully_submitted)
        hideProgress()
        comment!!.setText("")
    }

    companion object {
        fun showPopup(manager: FragmentManager, issue: Issue) {
            val fragment = IssuePopupFragment()
            var parser = getForIssue(issue.htmlUrl!!)
            if (parser == null) {
                parser = getForPullRequest(issue.htmlUrl!!)
            }
            if (parser == null) return
            fragment.arguments = getBundle(
                parser.login!!,
                parser.repoId!!,
                issue.number,
                issue.title!!,
                issue.body!!,
                issue.user!!,
                issue.assignee,
                issue.labels,
                issue.milestone,
                !issue.locked
            )
            fragment.show(manager, "")
        }

        fun showPopup(manager: FragmentManager, pullRequest: PullRequest) {
            val fragment = IssuePopupFragment()
            val parser = getForPullRequest(pullRequest.htmlUrl!!) ?: return
            fragment.arguments = getBundle(
                parser.login!!,
                parser.repoId!!,
                pullRequest.number,
                pullRequest.title!!,
                pullRequest.body!!,
                pullRequest.user!!,
                pullRequest.assignee,
                pullRequest.labels,
                pullRequest.milestone,
                !pullRequest.locked
            )
            fragment.show(manager, "")
        }

        private fun getBundle(
            login: String, repoId: String,
            number: Int, title: String, body: String,
            user: User, assignee: User?,
            labels: LabelListModel?, milestone: MilestoneModel?,
            canComment: Boolean
        ): Bundle {
            return start()
                .put(BundleConstant.EXTRA_SEVEN, login)
                .put(BundleConstant.EXTRA_EIGHT, repoId)
                .put(BundleConstant.ID, number)
                .put(BundleConstant.EXTRA, title)
                .put(BundleConstant.EXTRA_TWO, body)
                .put(BundleConstant.EXTRA_THREE, user)
                .put(BundleConstant.EXTRA_FOUR, assignee)
                .putParcelableArrayList(BundleConstant.EXTRA_FIVE, labels)
                .put(BundleConstant.EXTRA_SIX, milestone)
                .put(BundleConstant.YES_NO_EXTRA, canComment)
                .end()
        }
    }
}