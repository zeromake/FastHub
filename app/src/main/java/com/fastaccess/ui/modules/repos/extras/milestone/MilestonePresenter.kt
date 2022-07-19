package com.fastaccess.ui.modules.repos.extras.milestone

import android.view.View
import com.fastaccess.R
import com.fastaccess.data.dao.MilestoneModel
import com.fastaccess.provider.rest.RestProvider.getRepoService
import com.fastaccess.ui.base.mvp.presenter.BasePresenter

/**
 * Created by Kosh on 04 Mar 2017, 9:41 PM
 */
class MilestonePresenter : BasePresenter<MilestoneMvp.View>(), MilestoneMvp.Presenter {
    override val milestones = ArrayList<MilestoneModel>()
    override fun onItemClick(position: Int, v: View?, item: MilestoneModel) {
        if (view != null) view!!.onMilestoneSelected(item)
    }

    override fun onItemLongClick(position: Int, v: View?, item: MilestoneModel) {}
    override fun onLoadMilestones(login: String, repo: String) {
        makeRestCall(getRepoService(isEnterprise).getMilestones(login, repo)
        ) { response ->
            if (response?.items == null || response.items!!.isEmpty()) {
                sendToView { view: MilestoneMvp.View ->
                    view.showMessage(
                        R.string.error,
                        R.string.no_milestones
                    )
                }
                return@makeRestCall
            }
            sendToView { view: MilestoneMvp.View -> view.onNotifyAdapter(response.items) }
        }
    }
}