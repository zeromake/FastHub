package com.fastaccess.ui.modules.repos.extras.labels.create

import android.view.View
import com.fastaccess.data.dao.LabelModel
import com.fastaccess.provider.rest.RestProvider.getRepoService
import com.fastaccess.ui.base.mvp.presenter.BasePresenter

/**
 * Created by Kosh on 02 Apr 2017, 5:35 PM
 */
class CreateLabelPresenter : BasePresenter<CreateLabelMvp.View>(), CreateLabelMvp.Presenter {
    override fun onItemClick(position: Int, v: View?, item: String) {
        if (view != null) {
            view!!.onColorSelected(item)
        }
    }

    override fun onItemLongClick(position: Int, v: View?, item: String) {}
    override fun onSubmitLabel(name: String, color: String, repo: String, login: String) {
        val labelModel = LabelModel()
        labelModel.color = color.replace("#".toRegex(), "")
        labelModel.name = name
        makeRestCall(
            getRepoService(isEnterprise)
                .addLabel(login, repo, labelModel)
        ) { labelModel1 ->
            sendToView { view: CreateLabelMvp.View ->
                view.onSuccessfullyCreated(
                    labelModel1
                )
            }
        }
    }
}