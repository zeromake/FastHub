package com.fastaccess.ui.modules.reviews.changes

import com.fastaccess.data.dao.ReviewRequestModel
import com.fastaccess.provider.rest.RestProvider
import com.fastaccess.ui.base.mvp.presenter.BasePresenter
import java.util.*

/**
 * Created by Kosh on 25 Jun 2017, 1:16 AM
 */
class ReviewChangesPresenter : BasePresenter<ReviewChangesMvp.View>(), ReviewChangesMvp.Presenter {

    override fun onSubmit(
        reviewRequest: ReviewRequestModel,
        repoId: String,
        owner: String,
        number: Long,
        comment: String,
        method: String
    ) {
        reviewRequest.body = comment
        reviewRequest.event = method.replace(" ", "_").uppercase(Locale.getDefault())
        makeRestCall(
            RestProvider.getReviewService(isEnterprise)
                .submitPrReview(owner, repoId, number, reviewRequest), { it ->
                if (it.isSuccessful && it.code() == 200) {
                    sendToView { view -> view.onSuccessfullySubmitted() }
                } else {
                    sendToView { view -> view.onErrorSubmitting() }
                }
            }, false
        )
    }
}