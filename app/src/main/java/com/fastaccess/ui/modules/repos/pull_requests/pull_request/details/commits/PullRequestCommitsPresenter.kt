package com.fastaccess.ui.modules.repos.pull_requests.pull_request.details.commits

import android.os.Bundle
import android.view.View
import com.fastaccess.data.dao.model.Commit
import com.fastaccess.helper.BundleConstant
import com.fastaccess.helper.InputHelper.isEmpty
import com.fastaccess.helper.RxHelper
import com.fastaccess.provider.rest.RestProvider
import com.fastaccess.ui.base.mvp.presenter.BasePresenter
import com.fastaccess.ui.modules.repos.code.commit.details.CommitPagerActivity

/**
 * Created by Kosh on 03 Dec 2016, 3:48 PM
 */
class PullRequestCommitsPresenter : BasePresenter<PullRequestCommitsMvp.View>(),
    PullRequestCommitsMvp.Presenter {
    @JvmField
    @com.evernote.android.state.State
    var login: String? = null

    @JvmField
    @com.evernote.android.state.State
    var repoId: String? = null

    @JvmField
    @com.evernote.android.state.State
    var number: Long = 0
    override val commits = ArrayList<Commit>()
    override var currentPage = 0
    override var previousTotal = 0
    private var lastPage = Int.MAX_VALUE
    override fun onError(throwable: Throwable) {
        onWorkOffline()
        super.onError(throwable)
    }

    override fun onCallApi(page: Int, parameter: Any?): Boolean {
        if (page == 1) {
            lastPage = Int.MAX_VALUE
            sendToView { view -> view?.loadMore?.reset() }
        }
        if (page > lastPage || lastPage == 0) {
            sendToView { it?.hideProgress() }
            return false
        }
        if (repoId == null || login == null) return false
        makeRestCall(RestProvider.getPullRequestService(isEnterprise)
            .getPullRequestCommits(login!!, repoId!!, number, page)
        ) { response ->
            lastPage = response.last
            if (currentPage == 1) {
                manageDisposable(Commit.save(response.items!!, repoId!!, login!!, number))
            }
            sendToView { view ->
                view?.onNotifyAdapter(
                    response.items ?: listOf(),
                    page
                )
            }
        }
        return true
    }

    override fun onFragmentCreated(bundle: Bundle) {
        repoId = bundle.getString(BundleConstant.ID)
        login = bundle.getString(BundleConstant.EXTRA)
        number = bundle.getLong(BundleConstant.EXTRA_TWO)
        if (!isEmpty(login) && !isEmpty(repoId)) {
            onCallApi(1, null)
        }
    }

    override fun onWorkOffline() {
        if (commits.isEmpty()) {
            manageDisposable(RxHelper.getSingle(
                Commit.getCommits(
                    repoId!!, login!!, number
                )
            )
                .subscribe { models: List<Commit> ->
                    sendToView { view ->
                        view?.onNotifyAdapter(
                            models,
                            1
                        )
                    }
                })
        } else {
            sendToView { it?.hideProgress() }
        }
    }

    override fun onItemClick(position: Int, v: View?, item: Commit) {
        CommitPagerActivity.createIntentForOffline(v!!.context, item)
    }

    override fun onItemLongClick(position: Int, v: View?, item: Commit) {}
}