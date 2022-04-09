package com.fastaccess.ui.modules.repos.code.contributors

import android.os.Bundle
import android.view.View
import com.fastaccess.data.entity.User
import com.fastaccess.helper.BundleConstant
import com.fastaccess.helper.InputHelper.isEmpty
import com.fastaccess.provider.rest.RestProvider.getRepoService
import com.fastaccess.ui.base.mvp.presenter.BasePresenter

/**
 * Created by Kosh on 03 Dec 2016, 3:48 PM
 */
class RepoContributorsPresenter : BasePresenter<RepoContributorsMvp.View>(),
    RepoContributorsMvp.Presenter {
    override val users = ArrayList<User>()
    override var currentPage = 0
    override var previousTotal = 0
    private var lastPage = Int.MAX_VALUE

    @JvmField
    @com.evernote.android.state.State
    var repoId: String? = null

    @JvmField
    @com.evernote.android.state.State
    var login: String? = null
    override fun onCallApi(page: Int, parameter: String?): Boolean {
        if (page == 1) {
            lastPage = Int.MAX_VALUE
            sendToView { view -> view.loadMore.reset() }
        }
        if (page > lastPage || lastPage == 0) {
            sendToView { it.hideProgress() }
            return false
        }
        makeRestCall(getRepoService(isEnterprise).getContributors(
            login!!, repoId!!, page
        )
        ) { response ->
            if (response != null) {
                lastPage = response.last
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
        if (!isEmpty(login) && !isEmpty(repoId)) {
            onCallApi(1, null)
        }
    }

    override fun onError(throwable: Throwable) {
        onWorkOffline()
        super.onError(throwable)
    }

    override fun onWorkOffline() {
        sendToView { it.hideProgress() }
    }

    override fun onItemClick(position: Int, v: View?, item: User) {}
    override fun onItemLongClick(position: Int, v: View?, item: User) {}
}