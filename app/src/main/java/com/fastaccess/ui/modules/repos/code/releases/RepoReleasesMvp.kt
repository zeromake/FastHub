package com.fastaccess.ui.modules.repos.code.releases

import com.fastaccess.ui.base.mvp.BaseMvp.FAView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.fastaccess.ui.widgets.dialog.ListDialogView.OnSimpleItemSelection
import com.fastaccess.data.dao.SimpleUrlsModel
import com.fastaccess.data.entity.Release
import com.fastaccess.provider.rest.loadmore.OnLoadMore
import com.fastaccess.ui.base.mvp.BaseMvp.FAPresenter
import com.fastaccess.ui.base.mvp.BaseMvp.PaginationListener
import android.os.Bundle
import android.view.View
import com.fastaccess.ui.base.adapter.BaseViewHolder
import java.util.ArrayList

/**
 * Created by Kosh on 03 Dec 2016, 3:45 PM
 */
interface RepoReleasesMvp {
    interface View : FAView, OnRefreshListener, android.view.View.OnClickListener,
        OnSimpleItemSelection<SimpleUrlsModel> {
        fun onNotifyAdapter(items: List<Release>?, page: Int)
        val loadMore: OnLoadMore<String>
        fun onDownload(item: Release)
        fun onShowDetails(item: Release)
    }

    interface Presenter : FAPresenter, BaseViewHolder.OnItemClickListener<Release>,
        PaginationListener<String> {
        fun onFragmentCreated(bundle: Bundle)
        fun onWorkOffline()
        val releases: ArrayList<Release>
    }
}