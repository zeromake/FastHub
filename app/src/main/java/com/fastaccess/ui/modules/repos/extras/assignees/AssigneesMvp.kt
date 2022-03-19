package com.fastaccess.ui.modules.repos.extras.assignees

import com.fastaccess.data.dao.model.User
import com.fastaccess.ui.base.mvp.BaseMvp.FAView
import com.fastaccess.ui.adapter.AssigneesAdapter.OnSelectAssignee
import java.util.ArrayList

/**
 * Created by Kosh on 22 Feb 2017, 7:22 PM
 */
interface AssigneesMvp {
    interface SelectedAssigneesListener {
        fun onSelectedAssignees(users: ArrayList<User>, isAssignees: Boolean)
    }

    interface View : FAView, OnSelectAssignee {
        fun onNotifyAdapter(items: List<User>?)
    }

    interface Presenter {
        fun onCallApi(login: String, repo: String, isAssignees: Boolean)
        val list: ArrayList<User>
    }
}