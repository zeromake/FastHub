package com.fastaccess.ui.modules.repos.code.files.paths

import android.os.Bundle
import com.fastaccess.data.dao.model.RepoFile
import com.fastaccess.ui.base.adapter.BaseViewHolder
import com.fastaccess.ui.base.mvp.BaseMvp.FAPresenter
import com.fastaccess.ui.base.mvp.BaseMvp.FAView
import com.fastaccess.ui.modules.repos.extras.branches.BranchesMvp.BranchSelectionListener

/**
 * Created by Kosh on 20 Nov 2016, 11:10 AM
 */
interface RepoFilePathMvp {
    interface View : FAView, BranchSelectionListener {
        fun onNotifyAdapter(items: List<RepoFile>?, page: Int)
        fun onItemClicked(model: RepoFile, position: Int)
        fun onAppendPath(model: RepoFile)
        fun onAppenedtab(repoFile: RepoFile?)
        fun onSendData()
        fun canPressBack(): Boolean
        fun onBackPressed()
    }

    interface Presenter : FAPresenter, BaseViewHolder.OnItemClickListener<RepoFile> {
        fun onFragmentCreated(bundle: Bundle?)
        val repoId: String?
        val login: String?
        val path: String?
        val paths: ArrayList<RepoFile>
        val defaultBranch: String?
    }
}