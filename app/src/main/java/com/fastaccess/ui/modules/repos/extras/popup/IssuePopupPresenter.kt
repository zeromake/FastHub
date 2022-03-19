package com.fastaccess.ui.modules.repos.extras.popup

import com.fastaccess.data.dao.CommentRequestModel
import com.fastaccess.provider.rest.RestProvider.getIssueService
import com.fastaccess.ui.base.mvp.presenter.BasePresenter

/**
 * Created by Kosh on 27 May 2017, 1:56 PM
 */
class IssuePopupPresenter : BasePresenter<IssuePopupMvp.View>(), IssuePopupMvp.Presenter {
    override fun onSubmit(login: String, repoId: String, issueNumber: Int, text: String) {
        val requestModel = CommentRequestModel()
        requestModel.body = text
        makeRestCall(
            getIssueService(isEnterprise).createIssueComment(
                login,
                repoId,
                issueNumber,
                requestModel
            )
        ) {
            sendToView { it.onSuccessfullySubmitted() }
        }
    }
}