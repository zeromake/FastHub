package com.fastaccess.ui.modules.gists.create

import com.fastaccess.ui.base.mvp.BaseMvp.FAView
import com.fastaccess.ui.base.mvp.BaseMvp.FAPresenter
import com.fastaccess.data.dao.FilesListModel
import com.fastaccess.data.dao.CreateGistModel
import com.fastaccess.data.dao.model.Gist
import java.util.HashMap

/**
 * Created by Kosh on 30 Nov 2016, 10:43 AM
 */
interface CreateGistMvp {
    interface View : FAView {
        fun onDescriptionError(isEmptyDesc: Boolean)
        fun onFileNameError(isEmptyDesc: Boolean)
        fun onFileContentError(isEmptyDesc: Boolean)
        fun onSuccessSubmission(gistsModel: Gist?)
    }

    interface Presenter : FAPresenter {
        fun onSubmit(
            description: String,
            files: HashMap<String, FilesListModel>,
            isPublic: Boolean
        )

        fun onSubmit(model: CreateGistModel)
        fun onSubmitUpdate(
            id: String,
            description: String,
            files: HashMap<String, FilesListModel>
        )
    }
}