package com.fastaccess.ui.modules.repos.code.commit.details.comments

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import com.fastaccess.R
import com.fastaccess.data.dao.CommentRequestModel
import com.fastaccess.data.dao.TimelineModel
import com.fastaccess.data.dao.TimelineModel.Companion.construct
import com.fastaccess.data.dao.TimelineModel.Companion.constructComment
import com.fastaccess.data.dao.types.ReactionTypes
import com.fastaccess.data.entity.Comment
import com.fastaccess.data.entity.dao.CommentDao
import com.fastaccess.data.entity.dao.LoginDao
import com.fastaccess.helper.ActivityHelper.shareUrl
import com.fastaccess.helper.BundleConstant
import com.fastaccess.helper.RxHelper.getObservable
import com.fastaccess.provider.rest.RestProvider.getRepoService
import com.fastaccess.provider.timeline.CommentsHelper.isOwner
import com.fastaccess.provider.timeline.ReactionsProvider
import com.fastaccess.ui.base.mvp.presenter.BasePresenter

/**
 * Created by Kosh on 11 Nov 2016, 12:36 PM
 */
class CommitCommentsPresenter : BasePresenter<CommitCommentsMvp.View>(),
    CommitCommentsMvp.Presenter {
    override val comments = ArrayList<TimelineModel>()
    private var reactionsProvider: ReactionsProvider? = null
        get() {
            if (field == null) {
                field = ReactionsProvider()
            }
            return field
        }
    override var currentPage = 0
    override var previousTotal = 0
    private var lastPage = Int.MAX_VALUE

    @com.evernote.android.state.State
    var repoId: String? = null

    @com.evernote.android.state.State
    var login: String? = null

    @com.evernote.android.state.State
    var sha: String? = null

    @JvmField
    @com.evernote.android.state.State
    var isCollaborator = false
    override fun onCallApi(page: Int, parameter: String?): Boolean {
        if (page == 1) {
            lastPage = Int.MAX_VALUE
            sendToView { view -> view.loadMore.reset() }
        }
        if (page > lastPage || lastPage == 0) {
            sendToView { it.hideProgress() }
            return false
        }
        if (page == 1) {
            manageObservable(
                LoginDao.getUser().toObservable()
                    .flatMap {
                        getRepoService(isEnterprise).isCollaborator(
                            login!!, repoId!!,
                            it.or().login!!
                        )
                    }
                    .doOnNext { booleanResponse ->
                        isCollaborator = booleanResponse.code() == 204
                    }
            )
        }
        makeRestCall(getRepoService(isEnterprise).getCommitComments(
            login!!, repoId!!, sha!!, page
        )
            .flatMap { listResponse ->
                lastPage = listResponse.last
                construct(listResponse.items)
            }
            .doOnComplete {
                if (lastPage <= 1) {
                    sendToView { it.showReload() }
                }
            }
        ) { listResponse ->
            sendToView { view ->
                view.onNotifyAdapter(
                    listResponse,
                    page
                )
            }
        }
        return true
    }

    override fun onFragmentCreated(bundle: Bundle?) {
        if (bundle == null) throw NullPointerException("Bundle is null?")
        repoId = bundle.getString(BundleConstant.ID)
        login = bundle.getString(BundleConstant.EXTRA)
        sha = bundle.getString(BundleConstant.EXTRA_TWO)
    }

    override fun onHandleDeletion(bundle: Bundle?) {
        if (bundle != null) {
            val commId = bundle.getLong(BundleConstant.EXTRA, 0)
            if (commId != 0L) {
                makeRestCall(
                    getRepoService(isEnterprise).deleteComment(
                        login!!, repoId!!, commId
                    )
                ) { booleanResponse ->
                    sendToView { view ->
                        if (booleanResponse.code() == 204) {
                            val comment = Comment()
                            comment.id = commId
                            view.onRemove(constructComment(comment))
                        } else {
                            view.showMessage(
                                R.string.error,
                                R.string.error_deleting_comment
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onWorkOffline() {
        if (comments.isEmpty()) {
            manageDisposable(
                getObservable(CommentDao.getCommitComments(repoId(), login(), sha!!).toObservable())
                    .flatMap { obj -> construct(obj) }
                    .subscribe { models ->
                        sendToView { view ->
                            view.onNotifyAdapter(
                                models,
                                1
                            )
                        }
                    })
        } else {
            sendToView { it.hideProgress() }
        }
    }

    override fun repoId(): String {
        return repoId!!
    }

    override fun login(): String {
        return login!!
    }

    override fun sha(): String? {
        return sha
    }

    override fun isPreviouslyReacted(commentId: Long, vId: Int): Boolean {
        return reactionsProvider!!.isPreviouslyReacted(commentId, vId)
    }

    override fun isCallingApi(id: Long, vId: Int): Boolean {
        return reactionsProvider!!.isCallingApi(id, vId)
    }

    override fun onHandleComment(text: String, bundle: Bundle?) {
        val model = CommentRequestModel()
        model.body = text
        manageDisposable(
            getObservable(
                getRepoService(isEnterprise).postCommitComment(
                    login!!, repoId!!, sha!!, model
                )
            )
                .doOnSubscribe {
                    sendToView { view -> view.showBlockingProgress(0) }
                }
                .subscribe(
                    { comment ->
                        sendToView { view ->
                            view.addComment(
                                comment!!
                            )
                        }
                    }
                ) { throwable: Throwable? ->
                    onError(throwable!!)
                    sendToView { it.hideBlockingProgress() }
                })
    }

    override fun onItemClick(position: Int, v: View?, item: TimelineModel) {
        if (view != null && v != null) {
            val comment = item.comment
            if (v.id == R.id.commentMenu) {
                val popupMenu = PopupMenu(v.context, v)
                popupMenu.inflate(R.menu.comments_menu)
                val username = LoginDao.getUser().blockingGet().or().login!!
                val isOwner = isOwner(username, login!!, comment!!.user!!.login!!) || isCollaborator
                popupMenu.menu.findItem(R.id.delete).isVisible = isOwner
                popupMenu.menu.findItem(R.id.edit).isVisible = isOwner
                popupMenu.setOnMenuItemClickListener { item1: MenuItem ->
                    if (view == null) return@setOnMenuItemClickListener false
                    when (item1.itemId) {
                        R.id.delete -> {
                            view!!.onShowDeleteMsg(comment.id)
                        }
                        R.id.reply -> {
                            view!!.onReply(comment.user, comment.body)
                        }
                        R.id.edit -> {
                            view!!.onEditComment(comment)
                        }
                        R.id.share -> {
                            shareUrl(v.context, comment.htmlUrl!!)
                        }
                    }
                    true
                }
                popupMenu.show()
            } else {
                onHandleReaction(v.id, comment!!.id)
            }
        }
    }

    override fun onItemLongClick(position: Int, v: View?, item: TimelineModel) {
        if (v!!.id == R.id.commentMenu) {
            val comment = item.comment
            if (view != null) view!!.onReply(comment!!.user, comment.body)
        } else {
            val reactionTypes = ReactionTypes[v.id]
            if (reactionTypes != null) {
                if (view != null) view!!.showReactionsPopup(
                    reactionTypes,
                    login!!,
                    repoId!!,
                    item.comment!!.id
                )
            } else {
                onItemClick(position, v, item)
            }
        }
    }

    private fun onHandleReaction(viewId: Int, id: Long) {
        val observable = reactionsProvider!!.onHandleReaction(
            viewId,
            id,
            login,
            repoId,
            ReactionsProvider.COMMIT,
            isEnterprise
        )
        observable?.let { manageObservable(it) }
    }
}