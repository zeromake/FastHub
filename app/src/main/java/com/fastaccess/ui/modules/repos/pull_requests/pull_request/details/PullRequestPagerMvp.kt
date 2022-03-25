package com.fastaccess.ui.modules.repos.pull_requests.pull_request.details

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.fastaccess.data.dao.LabelModel
import com.fastaccess.data.dao.MilestoneModel
import com.fastaccess.data.dao.model.PullRequest
import com.fastaccess.data.dao.model.User
import com.fastaccess.ui.base.mvp.BaseMvp.FAPresenter
import com.fastaccess.ui.base.mvp.BaseMvp.FAView
import com.fastaccess.ui.modules.editor.comment.CommentEditorFragment.CommentListener
import com.fastaccess.ui.modules.repos.extras.assignees.AssigneesMvp.SelectedAssigneesListener
import com.fastaccess.ui.modules.repos.extras.labels.LabelsMvp.SelectedLabelsListener
import com.fastaccess.ui.modules.repos.extras.locking.LockIssuePrCallback
import com.fastaccess.ui.modules.repos.issues.issue.details.IssuePagerMvp.IssuePrCallback
import com.fastaccess.ui.modules.repos.pull_requests.pull_request.details.files.PullRequestFilesMvp.CommitCommentCallback
import com.fastaccess.ui.modules.repos.pull_requests.pull_request.details.files.PullRequestFilesMvp.PatchCallback
import com.fastaccess.ui.modules.repos.pull_requests.pull_request.merge.MergePullRequestMvp.MergeCallback
import com.fastaccess.ui.modules.reviews.changes.ReviewChangesMvp.ReviewSubmissionCallback
import com.fastaccess.ui.widgets.SpannableBuilder

/**
 * Created by Kosh on 10 Dec 2016, 9:21 AM
 */
interface PullRequestPagerMvp {
    interface View : FAView, SelectedLabelsListener, SelectedAssigneesListener, MergeCallback,
        IssuePrCallback<PullRequest>, PatchCallback, CommentListener, ReviewSubmissionCallback,
        LockIssuePrCallback {
        fun onSetupIssue(update: Boolean)
        fun showSuccessIssueActionMsg(isClose: Boolean)
        fun showErrorIssueActionMsg(isClose: Boolean)
        fun onUpdateTimeline()
        fun onMileStoneSelected(milestoneModel: MilestoneModel)
        fun onFinishActivity()
        fun onUpdateMenu()
    }

    interface Presenter : FAPresenter, CommitCommentCallback {
        val pullRequest: PullRequest?
        fun onActivityCreated(intent: Intent?)
        fun onWorkOffline()
        val isOwner: Boolean
        val isRepoOwner: Boolean
        val isLocked: Boolean
        val isMergeable: Boolean
        fun showToRepoBtn(): Boolean
        fun onHandleConfirmDialog(bundle: Bundle?)
        fun onOpenCloseIssue()
        fun onLockUnlockConversations(reason: String?)
        fun getMergeBy(pullRequest: PullRequest, context: Context): SpannableBuilder
        fun onMerge(s: String?, msg: String?)
        fun onPutLabels(labels: ArrayList<LabelModel>)
        fun onPutMilestones(milestone: MilestoneModel)
        fun onPutAssignees(users: ArrayList<User>, isAssignee: Boolean)
        val login: String?
        val repoId: String?
        val isCollaborator: Boolean
        fun onUpdatePullRequest(pullRequestModel: PullRequest)
        fun onRefresh()
        fun onPinUnpinPullRequest()
        fun onSubscribeOrMute(mute: Boolean)
    }
}