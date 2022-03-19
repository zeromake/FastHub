package com.fastaccess.ui.modules.repos.extras.labels

import com.fastaccess.data.dao.LabelModel
import com.fastaccess.provider.rest.RestProvider.getRepoService
import com.fastaccess.ui.base.mvp.presenter.BasePresenter

/**
 * Created by Kosh on 22 Feb 2017, 7:23 PM
 */
class LabelsPresenter(
    @JvmField
    @com.evernote.android.state.State
    var login: String,
    @JvmField
    @com.evernote.android.state.State
    var repoId: String
) :
    BasePresenter<LabelsMvp.View>(), LabelsMvp.Presenter {

    override var currentPage = 0
    override var previousTotal = 0
    private var lastPage = Int.MAX_VALUE
    override val labels = ArrayList<LabelModel>()

    override fun onCallApi(page: Int, parameter: String?): Boolean {
        if (page == 1) {
            lastPage = Int.MAX_VALUE
            sendToView { view -> view.loadMore.reset() }
        }
        if (page > lastPage || lastPage == 0) {
            sendToView { it.hideProgress() }
            return false
        }
        makeRestCall(getRepoService(isEnterprise).getLabels(
            login, repoId, page
        )
        ) { response ->
            lastPage = response.last
            sendToView { view ->
                view.onNotifyAdapter(
                    response.items,
                    page
                )
            }
        }
        return true
    }
}