package com.fastaccess.ui.modules.gists.gist.files

import com.fastaccess.ui.base.mvp.BaseMvp.FAView
import com.fastaccess.ui.modules.gists.create.dialog.AddGistMvp.AddGistFileListener
import com.fastaccess.data.dao.FilesListModel
import com.fastaccess.ui.base.mvp.BaseMvp.FAPresenter
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder
import java.util.HashMap

/**
 * Created by Kosh on 13 Nov 2016, 1:35 PM
 */
interface GistFilesListMvp {
    interface View : FAView, AddGistFileListener {
        fun onOpenFile(item: FilesListModel, position: Int)
        fun onDeleteFile(item: FilesListModel, position: Int)
        fun onEditFile(item: FilesListModel, position: Int)
        fun onInitFiles(file: MutableList<FilesListModel>, isOwner: Boolean)
        fun onAddNewFile()
        val filesMap: HashMap<String, FilesListModel>
    }

    interface Presenter : FAPresenter, BaseViewHolder.OnItemClickListener<FilesListModel> {
        val files: MutableList<FilesListModel>
    }
}