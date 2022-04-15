package com.fastaccess.ui.modules.repos.pull_requests.pull_request

import android.os.Bundle
import android.view.View
import com.fastaccess.data.dao.PullsIssuesParser.Companion.getForPullRequest
import com.fastaccess.data.dao.model.PullRequest
import com.fastaccess.data.dao.types.IssueState
import com.fastaccess.helper.BundleConstant
import com.fastaccess.helper.InputHelper.isEmpty
import com.fastaccess.helper.RxHelper.getObservable
import com.fastaccess.helper.RxHelper.getSingle
import com.fastaccess.provider.rest.RepoQueryProvider.getIssuesPullRequestQuery
import com.fastaccess.provider.rest.RestProvider.getPullRequestService
import com.fastaccess.ui.base.mvp.presenter.BasePresenter

/**
 * Created by Kosh on 03 Dec 2016, 3:48 PM
 */
class RepoPullRequestPresenter : BasePresenter<RepoPullRequestMvp.View>(),
    RepoPullRequestMvp.Presenter {
    @JvmField
    @com.evernote.android.state.State
    var login: String? = null

    @JvmField
    @com.evernote.android.state.State
    var repoId: String? = null

    @com.evernote.android.state.State
    override var issueState: IssueState = IssueState.open
    override val pullRequests = ArrayList<PullRequest>()
    override var currentPage = 0
    override var previousTotal = 0
    private var lastPage = Int.MAX_VALUE
    override fun onError(throwable: Throwable) {
        onWorkOffline()
        super.onError(throwable)
    }

    override fun onCallApi(page: Int, parameter: IssueState?): Boolean {
        if (parameter == null) {
            sendToView { it.hideProgress() }
            return false
        }
        issueState = parameter
        if (page == 1) {
            onCallCountApi(issueState)
            lastPage = Int.MAX_VALUE
            sendToView { view -> view.loadMore.reset() }
        }
        if (page > lastPage || lastPage == 0) {
            sendToView { it.hideProgress() }
            return false
        }
        if (repoId == null || login == null) return false
        currentPage = page
        makeRestCall(
            getPullRequestService(isEnterprise).getPullRequests(
                login!!, repoId!!, parameter.name, page
            )
        ) { response ->
            lastPage = response.last
            if (currentPage == 1) {
                manageDisposable(PullRequest.save(response.items!!, login!!, repoId!!))
            }
            sendToView { view ->
                view.onNotifyAdapter(
                    response.items,
                    page
                )
            }
        }
        return true
    }

    override fun onFragmentCreated(bundle: Bundle) {
        repoId = bundle.getString(BundleConstant.ID)
        login = bundle.getString(BundleConstant.EXTRA)
        issueState =
            (bundle.getSerializable(BundleConstant.EXTRA_TWO) as IssueState?) ?: IssueState.open
        if (!isEmpty(login) && !isEmpty(repoId)) {
            onCallApi(1, issueState)
        }
    }

    private fun onCallCountApi(issueState: IssueState) {
        manageDisposable(
            getObservable(
                getPullRequestService(isEnterprise)
                    .getPullsWithCount(
                        getIssuesPullRequestQuery(
                            login!!,
                            repoId!!,
                            issueState,
                            true
                        ), 0
                    )
            )
                .subscribe({ pullRequestPageable ->
                    sendToView { view ->
                        view.onUpdateCount(
                            pullRequestPageable.totalCount
                        )
                    }
                }) { obj: Throwable -> obj.printStackTrace() })
    }

    override fun onWorkOffline() {
        if (pullRequests.isEmpty()) {
            manageDisposable(
                getSingle(
                    PullRequest.getPullRequests(
                        repoId!!, login!!, issueState
                    )
                )
                    .subscribe { pulls ->
                        sendToView { view ->
                            view.onNotifyAdapter(pulls, 1)
                            view.onUpdateCount(pulls.size)
                        }
                    })
        } else {
            sendToView { it.hideProgress() }
        }
    }

    override fun onItemClick(position: Int, v: View?, item: PullRequest) {
        val parser = getForPullRequest(item.htmlUrl)
        if (parser != null && view != null) {
            view!!.onOpenPullRequest(parser)
        }
    }

    override fun onItemLongClick(position: Int, v: View?, item: PullRequest) {
        if (view != null) view!!.onShowPullRequestPopup(item)
    }
}