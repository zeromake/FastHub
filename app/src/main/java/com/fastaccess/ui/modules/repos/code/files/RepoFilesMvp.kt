package com.fastaccess.ui.modules.repos.code.files

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.fastaccess.data.dao.model.RepoFile
import com.fastaccess.ui.base.adapter.BaseViewHolder
import com.fastaccess.ui.base.mvp.BaseMvp.FAPresenter
import com.fastaccess.ui.base.mvp.BaseMvp.FAView
import com.fastaccess.ui.modules.repos.git.delete.DeleteContentFileCallback

/**
 * Created by Kosh on 20 Nov 2016, 11:10 AM
 */
interface RepoFilesMvp {
    interface View : FAView, OnRefreshListener, DeleteContentFileCallback {
        fun onNotifyAdapter(list: List<RepoFile>)
        fun onItemClicked(model: RepoFile)
        fun onMenuClicked(position: Int, model: RepoFile, v: android.view.View?)
        fun onSetData(
            login: String, repoId: String, path: String, ref: String,
            clear: Boolean, toAppend: RepoFile?
        )

        val isRefreshing: Boolean
        fun onUpdateTab(toAppend: RepoFile?)
    }

    interface Presenter : FAPresenter, BaseViewHolder.OnItemClickListener<RepoFile> {
        val files: ArrayList<RepoFile>
        fun onWorkOffline()
        fun onCallApi(toAppend: RepoFile?)
        fun onInitDataAndRequest(
            login: String, repoId: String, path: String,
            ref: String, clear: Boolean, toAppend: RepoFile?
        )

        fun getCachedFiles(url: String, ref: String): List<RepoFile>?
        fun onDeleteFile(message: String, item: RepoFile, branch: String)
    }
}