package com.fastaccess.ui.modules.repos.extras.misc

import android.os.Bundle
import android.view.View
import com.fastaccess.data.dao.Pageable
import com.fastaccess.data.entity.Repo
import com.fastaccess.data.entity.User
import com.fastaccess.helper.BundleConstant
import com.fastaccess.helper.InputHelper.isEmpty
import com.fastaccess.provider.rest.RestProvider.getRepoService
import com.fastaccess.ui.base.mvp.presenter.BasePresenter
import com.fastaccess.ui.modules.repos.extras.misc.RepoMiscMvp.MiscType
import io.reactivex.Observable

/**
 * Created by Kosh on 04 May 2017, 8:33 PM
 */
class RepoMiscPresenter internal constructor(arguments: Bundle?) :
    BasePresenter<RepoMiscMvp.View>(), RepoMiscMvp.Presenter {
    override val list = ArrayList<User>()
    override var currentPage = 0
    override var previousTotal = 0
    private var lastPage = Int.MAX_VALUE

    @JvmField
    @com.evernote.android.state.State
    var owner: String? = null

    @JvmField
    @com.evernote.android.state.State
    var repo: String? = null

    @com.evernote.android.state.State
    @MiscType
    override var type = 0
    override fun onCallApi(page: Int, @MiscType parameter: Int?): Boolean {
        if (page == 1) {
            lastPage = Int.MAX_VALUE
            sendToView { view -> view.loadMore.reset() }
        }
        if (page > lastPage || lastPage == 0) {
            sendToView { it.hideProgress() }
            return false
        }
        when (type) {
            RepoMiscMvp.WATCHERS -> {
                makeRestCall(
                    getRepoService(isEnterprise).getWatchers(
                        owner!!, repo!!, page
                    )
                ) { response: Pageable<User>? -> onResponse(page, response) }
                return true
            }
            RepoMiscMvp.STARS -> {
                makeRestCall(
                    getRepoService(isEnterprise).getStargazers(
                        owner!!, repo!!, page
                    )
                ) { response: Pageable<User>? -> onResponse(page, response) }
                return true
            }
            RepoMiscMvp.FORKS -> {
                makeRestCall(getRepoService(isEnterprise).getForks(
                    owner!!, repo!!, page
                )
                    .flatMap { repoPageable ->
                        lastPage = repoPageable.last
                        Observable.fromIterable(repoPageable.items)
                            .map { obj: Repo -> obj.owner!! }
                            .toList()
                            .toObservable()
                    }) { owners ->
                    sendToView { view ->
                        view.onNotifyAdapter(
                            owners,
                            page
                        )
                    }
                }
                return true
            }
        }
        return false
    }

    private fun onResponse(page: Int, response: Pageable<User>?) {
        if (response != null) {
            lastPage = response.last
            sendToView { view ->
                view.onNotifyAdapter(
                    response.items,
                    page
                )
            }
        } else {
            sendToView { it.hideProgress() }
        }
    }

    override fun onItemClick(position: Int, v: View?, item: User) {}
    override fun onItemLongClick(position: Int, v: View?, item: User) {}

    init {
        if (arguments != null) {
            if (isEmpty(owner) || isEmpty(repo)) {
                owner = arguments.getString(BundleConstant.EXTRA)
                repo = arguments.getString(BundleConstant.ID)
                type = arguments.getInt(BundleConstant.EXTRA_TYPE)
                onCallApi(1, type)
            }
        }
    }
}