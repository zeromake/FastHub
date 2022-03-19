package com.fastaccess.ui.modules.repos.extras.milestone.create

import com.fastaccess.data.dao.MilestoneModel
import com.fastaccess.ui.base.mvp.BaseMvp.FAView

/**
 * Created by Kosh on 04 Mar 2017, 10:47 PM
 */
interface CreateMilestoneMvp {
    interface OnMilestoneAdded {
        fun onMilestoneAdded(milestoneModel: MilestoneModel)
    }

    interface View : FAView {
        fun onShowTitleError(isError: Boolean)
        fun onMilestoneAdded(milestoneModel: MilestoneModel)
    }

    interface Presenter {
        fun onSubmit(
            title: String?, dueOn: String?, description: String?,
            login: String, repo: String
        )
    }
}