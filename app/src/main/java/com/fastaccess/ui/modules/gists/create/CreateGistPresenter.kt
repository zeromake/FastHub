package com.fastaccess.ui.modules.gists.create

import com.fastaccess.data.dao.CreateGistModel
import com.fastaccess.data.dao.FilesListModel
import com.fastaccess.data.dao.model.Gist
import com.fastaccess.helper.InputHelper.isEmpty
import com.fastaccess.helper.InputHelper.toString
import com.fastaccess.provider.rest.RestProvider
import com.fastaccess.ui.base.mvp.presenter.BasePresenter

/**
 * Created by Kosh on 30 Nov 2016, 10:51 AM
 */
class CreateGistPresenter : BasePresenter<CreateGistMvp.View>(), CreateGistMvp.Presenter {
    override fun onSubmit(
        description: String,
        files: MutableMap<String, FilesListModel>,
        isPublic: Boolean
    ) {
        if (files.isEmpty()) return
        val createGistModel = CreateGistModel(files, toString(description), isPublic)
        onSubmit(createGistModel)
    }

    override fun onSubmit(model: CreateGistModel) {
        makeRestCall(
            RestProvider.getGistService(isEnterprise).createGist(model),
            { gistsModel: Gist? ->
                sendToView { view -> view?.onSuccessSubmission(gistsModel) }
            }, false
        )
    }

    override fun onSubmitUpdate(
        id: String,
        description: String,
        files: MutableMap<String, FilesListModel>
    ) {
        val isEmptyDesc = isEmpty(description)
        if (view != null) {
            view!!.onDescriptionError(isEmptyDesc)
        }
        if (isEmptyDesc) return
        val createGistModel = CreateGistModel(files, toString(description), false)
        makeRestCall(
            RestProvider.getGistService(isEnterprise).editGist(createGistModel, id),
            { gistsModel: Gist? ->
                sendToView { view -> view?.onSuccessSubmission(gistsModel) }
            }, false
        )
    }
}