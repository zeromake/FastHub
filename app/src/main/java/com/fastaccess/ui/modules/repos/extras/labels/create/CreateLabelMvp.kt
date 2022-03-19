package com.fastaccess.ui.modules.repos.extras.labels.create

import com.fastaccess.ui.base.mvp.BaseMvp.FAView
import com.fastaccess.data.dao.LabelModel
import com.fastaccess.ui.base.adapter.BaseViewHolder

/**
 * Created by Kosh on 02 Apr 2017, 5:30 PM
 */
interface CreateLabelMvp {
    interface View : FAView {
        fun onSuccessfullyCreated(labelModel1: LabelModel)
        fun onColorSelected(color: String)
    }

    interface Presenter : BaseViewHolder.OnItemClickListener<String> {
        fun onSubmitLabel(
            name: String, color: String,
            repo: String, login: String
        )
    }
}