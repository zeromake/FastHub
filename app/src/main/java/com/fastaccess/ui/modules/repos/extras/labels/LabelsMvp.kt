package com.fastaccess.ui.modules.repos.extras.labels

import com.fastaccess.data.dao.LabelModel
import com.fastaccess.ui.base.mvp.BaseMvp.FAView
import com.fastaccess.ui.adapter.LabelsAdapter.OnSelectLabel
import com.fastaccess.provider.rest.loadmore.OnLoadMore
import com.fastaccess.ui.base.mvp.BaseMvp.PaginationListener
import java.util.ArrayList

/**
 * Created by Kosh on 22 Feb 2017, 7:22 PM
 */
interface LabelsMvp {
    interface SelectedLabelsListener {
        fun onSelectedLabels(labels: ArrayList<LabelModel>)
    }

    interface View : FAView, OnSelectLabel {
        val loadMore: OnLoadMore<String>
        fun onNotifyAdapter(items: List<LabelModel>?, page: Int)
        fun onLabelAdded(labelModel: LabelModel)
    }

    interface Presenter : PaginationListener<String> {
        val labels: ArrayList<LabelModel>
    }
}