package com.fastaccess.ui.modules.repos.code.commit.details

import android.content.Intent
import com.fastaccess.data.dao.CommentRequestModel
import com.fastaccess.data.dao.MarkdownModel
import com.fastaccess.data.dao.model.Commit
import com.fastaccess.helper.BundleConstant
import com.fastaccess.helper.InputHelper.isEmpty
import com.fastaccess.helper.RxHelper.getObservable
import com.fastaccess.provider.rest.RestProvider.getErrorCode
import com.fastaccess.provider.rest.RestProvider.getRepoService
import com.fastaccess.ui.base.mvp.presenter.BasePresenter
import io.reactivex.Observable

/**
 * Created by Kosh on 10 Dec 2016, 9:23 AM
 */
class CommitPagerPresenter : BasePresenter<CommitPagerMvp.View>(),
    CommitPagerMvp.Presenter {
    @com.evernote.android.state.State
    override var commit: Commit? = null

    @JvmField
    @com.evernote.android.state.State
    var sha: String? = null

    @com.evernote.android.state.State
    override var login: String? = null

    @com.evernote.android.state.State
    override var repoId: String? = null

    @com.evernote.android.state.State
    var showToRepoBtn = false

    @JvmField
    @com.evernote.android.state.State
    var reviewComments = ArrayList<CommentRequestModel>()
    override fun onError(throwable: Throwable) {
        if (getErrorCode(throwable) == 404) {
            sendToView { it.onFinishActivity() }
        } else {
            onWorkOffline(sha!!, repoId!!, login!!)
        }
        super.onError(throwable)
    }

    override fun onActivityCreated(intent: Intent?) {
        if (intent != null && intent.extras != null) {
            sha = intent.extras!!.getString(BundleConstant.ID)
            login = intent.extras!!.getString(BundleConstant.EXTRA)
            repoId = intent.extras!!.getString(BundleConstant.EXTRA_TWO)
            showToRepoBtn = intent.extras!!.getBoolean(BundleConstant.EXTRA_THREE)
            if (commit != null) {
                sendToView { it.onSetup() }
                return
            } else if (!isEmpty(sha) && !isEmpty(login) && !isEmpty(repoId)) {
                makeRestCall(
                    getRepoService(isEnterprise)
                        .getCommit(login!!, repoId!!, sha!!)
                        .flatMap({ commit: Commit ->
                            if (commit.gitCommit != null && commit.gitCommit.message != null) {
                                val markdownModel = MarkdownModel()
                                markdownModel.context = "$login/$repoId"
                                markdownModel.text = commit.gitCommit.message
                                return@flatMap getRepoService(isEnterprise).convertReadmeToHtml(
                                    markdownModel
                                )
                            }
                            Observable.just(commit)
                        }) { commit: Commit, u: Any ->
                            if (!isEmpty(u.toString()) && u is String) {
                                commit.gitCommit.message = u.toString()
                            }
                            commit
                        }) { commit: Commit? ->
                    this.commit = commit
                    commit!!.repoId = repoId
                    commit.login = login
                    sendToView { it.onSetup() }
                    manageObservable(commit.save(this.commit).toObservable())
                }
                return
            }
        }
        sendToView { it.onSetup() }
    }

    override fun onWorkOffline(sha: String, repoId: String, login: String) {
        manageDisposable(
            getObservable(Commit.getCommit(sha, repoId, login))
                .subscribe { commit ->
                    this.commit = commit
                    sendToView { it.onSetup() }
                })
    }

    override fun showToRepoBtn(): Boolean {
        return showToRepoBtn
    }
}