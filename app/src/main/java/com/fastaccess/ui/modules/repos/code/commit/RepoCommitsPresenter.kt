package com.fastaccess.ui.modules.repos.code.commit

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.fastaccess.data.dao.model.Commit
import com.fastaccess.helper.BundleConstant
import com.fastaccess.helper.InputHelper.isEmpty
import com.fastaccess.helper.RxHelper.getObservable
import com.fastaccess.helper.RxHelper.safeObservable
import com.fastaccess.provider.rest.RestProvider.getRepoService
import com.fastaccess.ui.base.mvp.presenter.BasePresenter
import com.fastaccess.ui.modules.repos.code.commit.details.CommitPagerActivity.Companion.createIntentForOffline

/**
 * Created by Kosh on 03 Dec 2016, 3:48 PM
 */
class RepoCommitsPresenter : BasePresenter<RepoCommitsMvp.View>(),
    RepoCommitsMvp.Presenter {
    override val commits = ArrayList<Commit>()

    @JvmField
    @com.evernote.android.state.State
    var login: String? = null

    @JvmField
    @com.evernote.android.state.State
    var repoId: String? = null

    @com.evernote.android.state.State
    override var defaultBranch: String? = null

    @JvmField
    @com.evernote.android.state.State
    var path: String? = null
    override var currentPage = 0
    override var previousTotal = 0
    private var lastPage = Int.MAX_VALUE
    override fun onError(throwable: Throwable) {
        onWorkOffline()
        super.onError(throwable)
    }

    override fun onCallApi(page: Int, parameter: String?): Boolean {
        if (page == 1) {
            lastPage = Int.MAX_VALUE
            sendToView { view -> view.loadMore.reset() }
        }
        if (page > lastPage || lastPage == 0) {
            sendToView { it.hideProgress() }
            return false
        }
        if (repoId == null || login == null) return false
        val observable = if (isEmpty(path)) getRepoService(isEnterprise).getCommits(
            login!!, repoId!!, defaultBranch!!, page
        ) else getRepoService(isEnterprise).getCommits(
            login!!, repoId!!, defaultBranch!!, path!!, page
        )
        makeRestCall(observable) { response ->
            if (response?.items != null) {
                lastPage = response.last
                if (currentPage == 1) {
                    manageDisposable(Commit.save(response.items!!, repoId!!, login!!))
                }
            }
            sendToView { view ->
                view.onNotifyAdapter(
                    response?.items, page
                )
            }
        }
        return true
    }

    override fun onFragmentCreated(bundle: Bundle) {
        repoId = bundle.getString(BundleConstant.ID)
        login = bundle.getString(BundleConstant.EXTRA)
        defaultBranch = bundle.getString(BundleConstant.EXTRA_TWO)
        path = bundle.getString(BundleConstant.EXTRA_THREE)
        if (!isEmpty(defaultBranch)) {
            getCommitCount(defaultBranch!!)
        }
        if (!isEmpty(login) && !isEmpty(repoId)) {
            onCallApi(1, null)
        }
    }

    override fun onWorkOffline() {
        if (commits.isEmpty()) {
            manageDisposable(
                getObservable(
                    Commit.getCommits(
                        repoId!!, login!!
                    ).toObservable()
                )
                    .subscribe { models ->
                        sendToView { view ->
                            view.onNotifyAdapter(
                                models,
                                1
                            )
                        }
                    })
        } else {
            sendToView { it.hideProgress() }
        }
    }

    override fun onBranchChanged(branch: String) {
        if (!TextUtils.equals(branch, defaultBranch)) {
            defaultBranch = branch
            onCallApi(1, null)
            getCommitCount(branch)
        }
    }

    override fun onItemClick(position: Int, v: View?, item: Commit) {
        createIntentForOffline(v!!.context, item)
    }

    override fun onItemLongClick(position: Int, v: View?, item: Commit) {}
    private fun getCommitCount(branch: String) {
        manageDisposable(
            safeObservable(
                getObservable(
                    getRepoService(isEnterprise)
                        .getCommitCounts(login!!, repoId!!, branch)
                )
            )
                .subscribe({ response ->
                    if (response != null) {
                        sendToView { view ->
                            view.onShowCommitCount(
                                response.last.toLong()
                            )
                        }
                    }
                }) { obj: Throwable -> obj.printStackTrace() })
    }
}