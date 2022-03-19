package com.fastaccess.ui.modules.repos.extras.assignees

import com.fastaccess.data.dao.model.User
import com.fastaccess.provider.rest.RestProvider.getRepoService
import com.fastaccess.ui.base.mvp.presenter.BasePresenter

/**
 * Created by Kosh on 05 Mar 2017, 11:52 AM
 */
class AssigneesPresenter : BasePresenter<AssigneesMvp.View>(), AssigneesMvp.Presenter {
    override val list = ArrayList<User>()
    override fun onCallApi(login: String, repo: String, isAssignees: Boolean) {
        makeRestCall(if (isAssignees) getRepoService(isEnterprise).getAssignees(
            login,
            repo
        ) else getRepoService(isEnterprise).getCollaborator(login, repo)
        ) { response ->
            sendToView { view->
                view.onNotifyAdapter(
                    response.items
                )
            }
        }
    }
}