package com.fastaccess.ui.modules.repos.extras.milestone.create

import com.fastaccess.R
import com.fastaccess.data.dao.CreateMilestoneModel
import com.fastaccess.helper.InputHelper.isEmpty
import com.fastaccess.helper.ParseDateFormat.Companion.getDateFromString
import com.fastaccess.helper.ParseDateFormat.Companion.toGithubDate
import com.fastaccess.provider.rest.RestProvider.getRepoService
import com.fastaccess.ui.base.mvp.presenter.BasePresenter

/**
 * Created by Kosh on 04 Mar 2017, 10:49 PM
 */
class CreateMilestonePresenter : BasePresenter<CreateMilestoneMvp.View>(),
    CreateMilestoneMvp.Presenter {
    override fun onSubmit(
        title: String?, dueOn: String?, description: String?,
        login: String, repo: String
    ) {
        if (view != null) {
            val isEmptyTitle = isEmpty(title)
            view!!.onShowTitleError(isEmptyTitle)
            if (!isEmptyTitle) {
                val createMilestoneModel = CreateMilestoneModel()
                createMilestoneModel.title = title
                if (!isEmpty(dueOn)) {
                    val date = getDateFromString(dueOn!!)
                    if (date != null) createMilestoneModel.dueOn = toGithubDate(date)
                }
                if (!isEmpty(description)) {
                    createMilestoneModel.description = description
                }
                makeRestCall(
                    getRepoService(isEnterprise).createMilestone(login, repo, createMilestoneModel)
                ) { milestoneModel ->
                    if (milestoneModel != null) {
                        sendToView { view: CreateMilestoneMvp.View ->
                            view.onMilestoneAdded(
                                milestoneModel
                            )
                        }
                    } else {
                        sendToView { view ->
                            view.showMessage(
                                R.string.error,
                                R.string.error_creating_milestone
                            )
                        }
                    }
                }
            }
        }
    }
}