package com.fastaccess.ui.modules.repos.extras.milestone

import com.fastaccess.data.dao.MilestoneModel
import com.fastaccess.ui.base.adapter.BaseViewHolder
import com.fastaccess.ui.base.mvp.BaseMvp.FAView
import com.fastaccess.ui.modules.repos.extras.milestone.create.CreateMilestoneMvp.OnMilestoneAdded

/**
 * Created by Kosh on 04 Mar 2017, 9:38 PM
 */
interface MilestoneMvp {
    interface OnMilestoneSelected {
        fun onMilestoneSelected(milestoneModel: MilestoneModel)
    }

    interface View : FAView, OnMilestoneAdded {
        fun onNotifyAdapter(items: List<MilestoneModel>?)
        fun onMilestoneSelected(milestoneModel: MilestoneModel)
    }

    interface Presenter : BaseViewHolder.OnItemClickListener<MilestoneModel> {
        fun onLoadMilestones(login: String, repo: String)
        val milestones: ArrayList<MilestoneModel>
    }
}