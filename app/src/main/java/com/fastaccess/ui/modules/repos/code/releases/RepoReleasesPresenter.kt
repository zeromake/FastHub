package com.fastaccess.ui.modules.repos.code.releases

import android.os.Bundle
import android.view.View
import com.fastaccess.R
import com.fastaccess.data.dao.Pageable
import com.fastaccess.data.entity.Release
import com.fastaccess.data.entity.dao.ReleaseDao
import com.fastaccess.helper.BundleConstant
import com.fastaccess.helper.InputHelper.isEmpty
import com.fastaccess.helper.RxHelper.getSingle
import com.fastaccess.provider.rest.RestProvider.getRepoService
import com.fastaccess.ui.base.mvp.presenter.BasePresenter

/**
 * Created by Kosh on 03 Dec 2016, 3:48 PM
 */
class RepoReleasesPresenter : BasePresenter<RepoReleasesMvp.View>(),
    RepoReleasesMvp.Presenter {
    override val releases = ArrayList<Release>()

    @JvmField
    @com.evernote.android.state.State
    var login: String? = null

    @JvmField
    @com.evernote.android.state.State
    var repoId: String? = null
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
        makeRestCall(
            getRepoService(isEnterprise).getReleases(
                login!!, repoId!!, page
            )
        ) { response ->
            if (response.items == null || response.items!!.isEmpty()) {
                makeRestCall(
                    getRepoService(isEnterprise).getTagReleases(
                        login!!, repoId!!, page
                    )
                ) { pageable ->
                    onResponse(pageable)
                }
                return@makeRestCall
            }
            onResponse(response)
        }
        return true
    }

    override fun onFragmentCreated(bundle: Bundle) {
        repoId = bundle.getString(BundleConstant.ID)
        login = bundle.getString(BundleConstant.EXTRA)
        val tag = bundle.getString(BundleConstant.EXTRA_THREE)
        val id = bundle.getLong(BundleConstant.EXTRA_TWO, -1)
        if (!isEmpty(tag)) {
            manageObservable(
                getRepoService(isEnterprise).getTagRelease(login!!, repoId!!, tag!!)
                    .doOnNext { release ->
                        if (release != null) {
                            sendToView { view ->
                                view.onShowDetails(
                                    release
                                )
                            }
                        }
                    })
        } else if (id > 0) {
            manageObservable(
                getRepoService(isEnterprise).getRelease(login!!, repoId!!, id)
                    .doOnNext { release: Release? ->
                        if (release != null) {
                            sendToView { view ->
                                view.onShowDetails(
                                    release
                                )
                            }
                        }
                    })
        }
        if (!isEmpty(login) && !isEmpty(repoId)) {
            onCallApi(1, null)
        }
    }

    override fun onWorkOffline() {
        if (releases.isEmpty()) {
            manageDisposable(
                getSingle(
                    ReleaseDao.get(
                        repoId!!, login!!
                    )
                )
                    .subscribe { releasesModels ->
                        sendToView { view ->
                            view.onNotifyAdapter(
                                releasesModels,
                                1
                            )
                        }
                    })
        } else {
            sendToView { it.hideProgress() }
        }
    }

    override fun onItemClick(position: Int, v: View?, item: Release) {
        if (view == null) return
        if (v!!.id == R.id.download) {
            view!!.onDownload(item)
        } else {
            view!!.onShowDetails(item)
        }
    }

    override fun onItemLongClick(position: Int, v: View?, item: Release) {}
    private fun onResponse(response: Pageable<Release>) {
        lastPage = response.last
        if (currentPage == 1) {
            manageObservable(
                ReleaseDao.save(
                    response.items!!,
                    repoId!!,
                    login!!,
                ).toObservable()
            )
        }
        sendToView { view ->
            view.onNotifyAdapter(
                response.items,
                currentPage
            )
        }
    }
}