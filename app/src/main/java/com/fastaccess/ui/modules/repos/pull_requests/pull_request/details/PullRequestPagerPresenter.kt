package com.fastaccess.ui.modules.repos.pull_requests.pull_request.details

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import com.fastaccess.R
import com.fastaccess.data.dao.*
import com.fastaccess.data.dao.IssueRequestModel.Companion.clone
import com.fastaccess.data.dao.PullsIssuesParser.Companion.getForIssue
import com.fastaccess.data.dao.types.IssueState
import com.fastaccess.data.entity.PullRequest
import com.fastaccess.data.entity.User
import com.fastaccess.data.entity.dao.LoginDao
import com.fastaccess.data.entity.dao.PinnedPullRequestsDao
import com.fastaccess.data.entity.dao.PullRequestDao
import com.fastaccess.helper.BundleConstant
import com.fastaccess.helper.InputHelper.isEmpty
import com.fastaccess.helper.RxHelper.getObservable
import com.fastaccess.provider.rest.RestProvider.getErrorCode
import com.fastaccess.provider.rest.RestProvider.getIssueService
import com.fastaccess.provider.rest.RestProvider.getNotificationService
import com.fastaccess.provider.rest.RestProvider.getPullRequestService
import com.fastaccess.provider.rest.RestProvider.getRepoService
import com.fastaccess.ui.base.mvp.presenter.BasePresenter
import com.fastaccess.ui.widgets.SpannableBuilder
import io.reactivex.Observable

/**
 * Created by Kosh on 10 Dec 2016, 9:23 AM
 */
class PullRequestPagerPresenter : BasePresenter<PullRequestPagerMvp.View>(),
    PullRequestPagerMvp.Presenter {
    @com.evernote.android.state.State
    override var pullRequest: PullRequest? = null

    @com.evernote.android.state.State
    var issueNumber = 0

    @com.evernote.android.state.State
    override var login: String? = null

    @com.evernote.android.state.State
    override var repoId: String? = null

    @com.evernote.android.state.State
    override var isCollaborator = false

    @com.evernote.android.state.State
    var showToRepoBtn = false

    @com.evernote.android.state.State
    override var commitComment = ArrayList<CommentRequestModel>()
    override fun onError(throwable: Throwable) {
        if (getErrorCode(throwable) == 404) {
            sendToView { it.onOpenUrlInBrowser() }
        } else {
            onWorkOffline()
        }
        super.onError(throwable)
    }

    override fun onActivityCreated(intent: Intent?) {
        if (intent != null && intent.extras != null) {
            issueNumber = intent.extras!!.getInt(BundleConstant.ID)
            login = intent.extras!!.getString(BundleConstant.EXTRA)
            repoId = intent.extras!!.getString(BundleConstant.EXTRA_TWO)
            showToRepoBtn = intent.extras!!.getBoolean(BundleConstant.EXTRA_THREE)
            if (pullRequest != null) {
                sendToView { view -> view.onSetupIssue(false) }
                return
            } else if (issueNumber > 0 && !isEmpty(login) && !isEmpty(repoId)) {
                callApi()
                return
            }
        }
        sendToView { view -> view.onSetupIssue(false) }
    }

    override fun onWorkOffline() {
        if (pullRequest == null) {
            manageObservable(
                PullRequestDao.getPullRequestByNumber(issueNumber, repoId!!, login!!).toObservable()
            ) { pullRequestModelOpt ->
                val pullRequestModel = pullRequestModelOpt.get()
                if (pullRequestModel != null) {
                    pullRequest = pullRequestModel
                    sendToView { view ->
                        view.onSetupIssue(
                            false
                        )
                    }
                }
            }
        }
    }

    override val isOwner: Boolean
        get() {
            if (pullRequest == null) return false
            val userModel = if (pullRequest != null) pullRequest!!.user else null
            val me = LoginDao.getUser().blockingGet().or()
            val parser = getForIssue(pullRequest!!.htmlUrl!!)
            return (userModel != null && userModel.login.equals(me.login, ignoreCase = true)
                    || parser != null && parser.login.equals(me.login, ignoreCase = true))
        }
    override val isRepoOwner: Boolean
        get() {
            if (pullRequest == null) return false
            val me = LoginDao.getUser().blockingGet().or()
            return TextUtils.equals(login, me.login)
        }
    override val isLocked: Boolean
        get() = pullRequest != null && pullRequest!!.locked
    override val isMergeable: Boolean
        get() = pullRequest != null && pullRequest!!.mergeable && !pullRequest!!.merged

    override fun showToRepoBtn(): Boolean {
        return showToRepoBtn
    }

    override fun onHandleConfirmDialog(bundle: Bundle?) {
        if (bundle != null) {
            val proceedCloseIssue = bundle.getBoolean(BundleConstant.EXTRA)
            val proceedLockUnlock = bundle.getBoolean(BundleConstant.EXTRA_TWO)
            if (proceedCloseIssue) {
                onOpenCloseIssue()
            } else if (proceedLockUnlock) {
                onLockUnlockConversations(null)
            }
        }
    }

    override fun onLockUnlockConversations(reason: String?) {
        pullRequest ?: return
        val service = getIssueService(isEnterprise)
        var model: LockIssuePrModel? = null
        if (!isLocked && !isEmpty(reason)) {
            model = LockIssuePrModel(true, reason)
        }
        val observable = getObservable(
            if (model == null) service.unlockIssue(
                login!!, repoId!!, issueNumber
            ) else service.lockIssue(model, login!!, repoId!!, issueNumber)
        )
        makeRestCall(
            observable
        ) { booleanResponse ->
            val code = booleanResponse.code()
            if (code == 204) {
                pullRequest!!.locked = !isLocked
                sendToView { view ->
                    view.onSetupIssue(
                        false
                    )
                }
            }
        }
    }

    override fun onOpenCloseIssue() {
        if (pullRequest != null) {
            val requestModel = clone(pullRequest!!, true)
            manageDisposable(
                getObservable(
                    getPullRequestService(isEnterprise).editPullRequest(
                        login!!, repoId!!,
                        issueNumber, requestModel
                    )
                )
                    .doOnSubscribe {
                        sendToView { view -> view.showProgress(0) }
                    }
                    .subscribe({ issue ->
                        if (issue != null) {
                            sendToView { view ->
                                view.showSuccessIssueActionMsg(
                                    pullRequest!!.state === IssueState.open
                                )
                            }
                            issue.repoId = pullRequest!!.repoId
                            issue.login = pullRequest!!.login
                            pullRequest = issue
                            sendToView { view ->
                                view.onSetupIssue(
                                    false
                                )
                            }
                        }
                    }) {
                        sendToView { view ->
                            view.showErrorIssueActionMsg(
                                pullRequest!!.state === IssueState.open
                            )
                        }
                    })
        }
    }

    override fun getMergeBy(pullRequest: PullRequest, context: Context): SpannableBuilder {
        return PullRequestDao.getMergeBy(context, pullRequest, false)
    }

    override fun onPutLabels(labels: java.util.ArrayList<LabelModel>) {
        makeRestCall(getIssueService(isEnterprise).putLabels(
            login!!, repoId!!, issueNumber,
            labels.filter { value -> value.name != null }
                .map { it.name!! }
        )
        ) {
            sendToView { view: PullRequestPagerMvp.View ->
                updateTimeline(
                    view,
                    R.string.labels_added_successfully
                )
            }
            val listModel = LabelListModel()
            listModel.addAll(labels)
            pullRequest!!.labels = listModel
            manageObservable(PullRequestDao.save(pullRequest!!).toObservable())
        }
    }

    override fun onPutMilestones(milestone: MilestoneModel) {
        pullRequest!!.milestone = milestone
        val issueRequestModel = clone(pullRequest!!, false)
        makeRestCall(
            getPullRequestService(isEnterprise).editIssue(
                login!!, repoId!!, issueNumber, issueRequestModel
            )
        ) { pr: PullRequest ->
            pullRequest!!.milestone = pr.milestone
            manageObservable(PullRequestDao.save(pullRequest!!).toObservable())
            sendToView { view ->
                updateTimeline(
                    view,
                    R.string.labels_added_successfully
                )
            }
        }
    }

    override fun onPutAssignees(users: java.util.ArrayList<User>, isAssignee: Boolean) {
        val assigneesRequestModel = AssigneesRequestModel()
        val assignees = users
            .map { obj: User -> obj.login!! }
        if (isAssignee) {
            assigneesRequestModel.assignees =
                assignees.ifEmpty {
                    pullRequest!!.assignees!!.map { obj -> obj.login!! }
                }
            makeRestCall(
                if (assignees.isNotEmpty()) getIssueService(isEnterprise).putAssignees(
                    login!!, repoId!!, issueNumber, assigneesRequestModel
                ) else getIssueService(isEnterprise).deleteAssignees(
                    login!!, repoId!!, issueNumber, assigneesRequestModel
                )
            ) {
                val usersListModel = UsersListModel()
                usersListModel.addAll(users)
                pullRequest!!.assignees = usersListModel
                manageObservable(PullRequestDao.save(pullRequest!!).toObservable())
                sendToView { view ->
                    updateTimeline(
                        view,
                        R.string.assignee_added
                    )
                }
            }
        } else {
            assigneesRequestModel.reviewers = assignees
            makeRestCall(
                getPullRequestService(isEnterprise).putReviewers(
                    login!!, repoId!!, issueNumber, assigneesRequestModel
                )
            ) {
                sendToView { view ->
                    updateTimeline(
                        view,
                        R.string.reviewer_added
                    )
                }
            }
        }
    }

    override fun onMerge(s: String?, msg: String?) {
        if (isMergeable && (isCollaborator || isRepoOwner)) { //double the checking
            if (pullRequest == null || pullRequest!!.head == null || pullRequest!!.head!!.sha == null) return
            val mergeRequestModel = MergeRequestModel()
            mergeRequestModel.sha = pullRequest!!.head!!.sha
            mergeRequestModel.commitMessage = s
            mergeRequestModel.mergeMethod = msg?.lowercase() ?: ""
            manageDisposable(
                getObservable(
                    getPullRequestService(isEnterprise)
                        .mergePullRequest(
                            login!!,
                            repoId!!,
                            issueNumber.toLong(),
                            mergeRequestModel
                        )
                )
                    .doOnSubscribe {
                        sendToView { view -> view.showProgress(0) }
                    }
                    .subscribe({ mergeResponseModel: MergeResponseModel ->
                        if (mergeResponseModel.isMerged) {
                            pullRequest!!.merged = true
                            sendToView { view ->
                                updateTimeline(
                                    view,
                                    R.string.success_merge
                                )
                            }
                        } else {
                            sendToView { view ->
                                view.showErrorMessage(
                                    mergeResponseModel.message!!
                                )
                            }
                        }
                    }) { throwable: Throwable ->
                        sendToView { view ->
                            view.showErrorMessage(
                                throwable.message!!
                            )
                        }
                    }
            )
        }
    }

    override fun onUpdatePullRequest(pullRequestModel: PullRequest) {
        pullRequest!!.title = pullRequestModel.title
        pullRequest!!.body = pullRequestModel.body
        pullRequest!!.bodyHtml = pullRequestModel.bodyHtml
        pullRequest!!.login = login
        pullRequest!!.repoId = repoId
        manageObservable(PullRequestDao.save(pullRequest!!).toObservable())
        sendToView { view -> view.onSetupIssue(true) }
    }

    override fun onRefresh() {
        callApi()
    }

    override fun onPinUnpinPullRequest() {
        if (pullRequest == null) return
        manageObservable(PinnedPullRequestsDao.pinUpin(pullRequest!!).toObservable()) {
            sendToView { it.onUpdateMenu() }
        }
    }

    override fun onAddComment(comment: CommentRequestModel) {
        val index = commitComment.indexOf(comment)
        if (index != -1) {
            commitComment[index] = comment
        } else {
            commitComment.add(comment)
        }
    }

    override fun hasReviewComments(): Boolean {
        return commitComment.size > 0
    }

    override fun onSubscribeOrMute(mute: Boolean) {
        if (pullRequest == null) return
        makeRestCall(
            if (mute) getNotificationService(isEnterprise).subscribe(
                pullRequest!!.id,
                NotificationSubscriptionBodyModel(subscribed = false, ignored = true)
            ) else getNotificationService(isEnterprise).subscribe(
                pullRequest!!.id,
                NotificationSubscriptionBodyModel(subscribed = true, ignored = false)
            )
        ) { booleanResponse ->
            if (booleanResponse.code() == 204 || booleanResponse.code() == 200) {
                sendToView { view ->
                    view.showMessage(
                        R.string.success,
                        R.string.successfully_submitted
                    )
                }
            } else {
                sendToView { view: PullRequestPagerMvp.View ->
                    view.showMessage(
                        R.string.error,
                        R.string.network_error
                    )
                }
            }
        }
    }

    private fun callApi() {
        val loggedInUser = LoginDao.getUser().blockingGet().get() ?: return
        makeRestCall(
            getObservable(
                Observable.zip(
                    getPullRequestService(isEnterprise)
                        .getPullRequest(login!!, repoId!!, issueNumber.toLong()),
                    getRepoService(isEnterprise).isCollaborator(
                        login!!,
                        repoId!!,
                        loggedInUser.login!!
                    ),
                    getIssueService(isEnterprise).getIssue(login!!, repoId!!, issueNumber)
                ) { pullRequestModel, booleanResponse, issue ->
                    pullRequest = pullRequestModel
                    pullRequest!!.reactions = issue.reactions
                    pullRequest!!.title = issue.title
                    pullRequest!!.body = issue.body
                    pullRequest!!.bodyHtml = issue.bodyHtml
                    pullRequest!!.login = login
                    pullRequest!!.repoId = repoId
                    isCollaborator = booleanResponse.code() == 204
                    pullRequest!!
                }
            )
        ) { pullRequest: PullRequest ->
            sendToView { view -> view.onSetupIssue(false) }
            manageObservable(PinnedPullRequestsDao.updateEntry(pullRequest.id).toObservable())
            manageObservable(PullRequestDao.save(pullRequest).toObservable())
        }
    }

    private fun updateTimeline(view: PullRequestPagerMvp.View, assignee_added: Int) {
        view.showMessage(R.string.success, assignee_added)
        view.onUpdateTimeline()
    }
}