package com.fastaccess.ui.modules.gists.gist.files

import android.view.View
import com.fastaccess.R
import com.fastaccess.data.dao.FilesListModel
import com.fastaccess.ui.base.mvp.presenter.BasePresenter

/**
 * Created by Kosh on 13 Nov 2016, 1:35 PM
 */
class GistFilesListPresenter : BasePresenter<GistFilesListMvp.View>(), GistFilesListMvp.Presenter {
    override fun onItemClick(position: Int, v: View?, item: FilesListModel) {
        v ?: return
        when (v.id) {
            R.id.delete -> {
                view!!.onDeleteFile(item, position)
            }
            R.id.edit -> {
                view!!.onEditFile(item, position)
            }
            else -> {
                view!!.onOpenFile(item, position)
            }
        }
    }

    override fun onItemLongClick(position: Int, v: View?, item: FilesListModel) {}
    override var files: MutableList<FilesListModel> = mutableListOf()
    val filesMap: MutableMap<String, FilesListModel> = mutableMapOf()
}