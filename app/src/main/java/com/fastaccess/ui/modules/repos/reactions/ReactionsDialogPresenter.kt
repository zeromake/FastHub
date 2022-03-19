package com.fastaccess.ui.modules.repos.reactions

import android.os.Bundle
import com.fastaccess.data.dao.Pageable
import com.fastaccess.data.dao.ReactionsModel
import com.fastaccess.data.dao.model.User
import com.fastaccess.data.dao.types.ReactionTypes
import com.fastaccess.helper.BundleConstant
import com.fastaccess.provider.rest.RestProvider.getReactionsService
import com.fastaccess.provider.timeline.ReactionsProvider
import com.fastaccess.provider.timeline.ReactionsProvider.ReactionType
import com.fastaccess.ui.base.mvp.presenter.BasePresenter
import io.reactivex.Observable

/**
 * Created by Kosh on 11 Apr 2017, 11:20 AM
 */
class ReactionsDialogPresenter : BasePresenter<ReactionsDialogMvp.View>(),
    ReactionsDialogMvp.Presenter {
    override var currentPage = 0
    override var previousTotal = 0
    private var lastPage = Int.MAX_VALUE
    override val users = ArrayList<User>()

    @JvmField
    @com.evernote.android.state.State
    var login: String? = null

    @JvmField
    @com.evernote.android.state.State
    var repoId: String? = null

    @JvmField
    @com.evernote.android.state.State
    var id: Long = 0

    @JvmField
    @com.evernote.android.state.State
    var reactionType: ReactionTypes? = null

    @JvmField
    @com.evernote.android.state.State
    @ReactionType
    var reactionTypeMode = 0
    override fun onFragmentCreated(bundle: Bundle?) {
        if (bundle != null) {
            repoId = bundle.getString(BundleConstant.EXTRA)
            login = bundle.getString(BundleConstant.EXTRA_TWO)
            id = bundle.getLong(BundleConstant.ID)
            reactionType = bundle.getSerializable(BundleConstant.EXTRA_TYPE) as ReactionTypes?
            reactionTypeMode = bundle.getInt(BundleConstant.EXTRA_THREE)
            onCallApi(1, null)
        }
    }

    override fun onCallApi(page: Int, parameter: String?): Boolean {
        if (page == 1) {
            lastPage = Int.MAX_VALUE
            sendToView { view -> view.loadMore.reset() }
        }
        if (page > lastPage || lastPage == 0 || login == null || repoId == null || reactionType == null) {
            sendToView { it.hideProgress() }
            return false
        }
        var observable: Observable<Pageable<ReactionsModel>>? = null
        when (reactionTypeMode) {
            ReactionsProvider.COMMENT -> observable = getReactionsService(isEnterprise)
                .getIssueCommentReaction(login!!, repoId!!, id, reactionType!!.content, page)
            ReactionsProvider.COMMIT -> observable = getReactionsService(isEnterprise)
                .getCommitReaction(login!!, repoId!!, id, reactionType!!.content, page)
            ReactionsProvider.HEADER -> observable = getReactionsService(isEnterprise)
                .getIssueReaction(login!!, repoId!!, id, reactionType!!.content, page)
            ReactionsProvider.REVIEW_COMMENT -> observable = getReactionsService(isEnterprise)
                .getPullRequestReactions(login!!, repoId!!, id, reactionType!!.content, page)
        }
        if (observable == null) {
            throw NullPointerException("Reaction is null?")
        }
        makeRestCall(
            observable
        ) { response ->
            lastPage = response.last
            sendToView { view ->
                view.onNotifyAdapter(
                    (response.items ?: listOf())
                        .filter { reactionsModel -> reactionsModel.user != null }
                        .map { it.user!! }, page
                )
            }
        }
        return true
    }
}