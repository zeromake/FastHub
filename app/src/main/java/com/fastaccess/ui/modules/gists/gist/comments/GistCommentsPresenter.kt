package com.fastaccess.ui.modules.gists.gist.comments

import android.os.Bundle
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import com.fastaccess.R
import com.fastaccess.data.dao.CommentRequestModel
import com.fastaccess.data.entity.Comment
import com.fastaccess.data.entity.dao.CommentDao
import com.fastaccess.data.entity.dao.LoginDao
import com.fastaccess.helper.BundleConstant
import com.fastaccess.helper.RxHelper
import com.fastaccess.provider.rest.RestProvider
import com.fastaccess.ui.base.mvp.presenter.BasePresenter
import retrofit2.Response

/**
 * Created by Kosh on 11 Nov 2016, 12:36 PM
 */
class GistCommentsPresenter : BasePresenter<GistCommentsMvp.View>(),
    GistCommentsMvp.Presenter {
    override val comments = ArrayList<Comment>()
    override var currentPage = 0
    override var previousTotal = 0
    private var lastPage = Int.MAX_VALUE

    override fun onError(throwable: Throwable) {
        sendToView { view: GistCommentsMvp.View ->
            onWorkOffline(
                view.loadMore.parameter!!
            )
        }
        super.onError(throwable)
    }

    override fun onCallApi(page: Int, parameter: String?): Boolean {
        if (page == 1) {
            lastPage = Int.MAX_VALUE
            sendToView { view -> view?.loadMore?.reset() }
        }
        if (page > lastPage || parameter == null || lastPage == 0) {
            sendToView { it?.hideProgress() }
            return false
        }
        currentPage = page
        makeRestCall(
            RestProvider.getGistService(isEnterprise).getGistComments(parameter, page)
        ) { listResponse ->
            lastPage = listResponse.last
            if (currentPage == 1) {
                manageObservable(
                    CommentDao.saveForGist(listResponse.items!!, parameter).toObservable()
                )
            }
            sendToView { view: GistCommentsMvp.View? ->
                view?.onNotifyAdapter(
                    listResponse.items ?: listOf(),
                    page
                )
            }
        }
        return true
    }

    override fun onHandleDeletion(bundle: Bundle?) {
        if (bundle != null) {
            val commId = bundle.getLong(BundleConstant.EXTRA, 0)
            val gistId = bundle.getString(BundleConstant.ID)
            if (commId != 0L && gistId != null) {
                makeRestCall(
                    RestProvider.getGistService(isEnterprise)
                        .deleteGistComment(gistId, commId)
                ) { booleanResponse: Response<Boolean> ->
                    sendToView { view ->
                        view ?: return@sendToView
                        if (booleanResponse.code() == 204) {
                            val comment = Comment()
                            comment.id = commId
                            view.onRemove(comment)
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

    override fun onWorkOffline(gistId: String) {
        if (comments.isEmpty()) {
            manageDisposable(RxHelper.getObservable(
                CommentDao.getGistComments(gistId).toObservable()
            )
                .subscribe { localComments: List<Comment> ->
                    sendToView { view ->
                        view?.onNotifyAdapter(
                            localComments,
                            1
                        )
                    }
                })
        } else {
            sendToView { it?.hideProgress() }
        }
    }

    override fun onHandleComment(text: String, bundle: Bundle, gistId: String) {
        val model = CommentRequestModel()
        model.body = text
        manageDisposable(RxHelper.getObservable(
            RestProvider.getGistService(isEnterprise).createGistComment(gistId, model)
        )
            .doOnSubscribe {
                sendToView { view -> view?.showBlockingProgress(0) }
            }
            .subscribe({ comment ->
                sendToView { view ->
                    view?.onAddNewComment(
                        comment!!
                    )
                }
            }) { throwable: Throwable ->
                onError(throwable)
                sendToView { it?.hideBlockingProgress() }
            })
    }

    override fun onItemClick(position: Int, v: View?, item: Comment) {
        v ?: return
        if (v.id == R.id.toggle || v.id == R.id.toggleHolder) {
            val popupMenu = PopupMenu(v.context, v)
            popupMenu.inflate(R.menu.comments_menu)
            val username = LoginDao.getUser().blockingGet().or().login!!
            popupMenu.menu.findItem(R.id.delete).isVisible =
                username.equals(item.user!!.login, ignoreCase = true)
            popupMenu.menu.findItem(R.id.edit).isVisible =
                username.equals(item.user!!.login, ignoreCase = true)
            popupMenu.setOnMenuItemClickListener { item1: MenuItem ->
                view ?: return@setOnMenuItemClickListener false
                when (item1.itemId) {
                    R.id.delete -> {
                        view!!.onShowDeleteMsg(item.id)
                    }
                    R.id.reply -> {
                        view!!.onReply(item.user!!, item.body!!)
                    }
                    R.id.edit -> {
                        view!!.onEditComment(item)
                    }
                }
                true
            }
            popupMenu.show()
        }
    }

    override fun onItemLongClick(position: Int, v: View?, item: Comment) {
        v ?: return
        if (v.id == R.id.toggle) {
            view?.onReply(item.user!!, item.body!!)
        } else {
            if (item.user != null && TextUtils.equals(
                    item.user!!.login,
                    LoginDao.getUser().blockingGet().or().login
                )
            ) {
                view?.onShowDeleteMsg(item.id)
            } else {
                onItemClick(position, v, item)
            }
        }
    }
}