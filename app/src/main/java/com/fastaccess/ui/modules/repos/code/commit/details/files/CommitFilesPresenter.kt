package com.fastaccess.ui.modules.repos.code.commit.details.files

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import com.fastaccess.R
import com.fastaccess.data.dao.CommentRequestModel
import com.fastaccess.data.dao.CommitFileChanges
import com.fastaccess.data.dao.CommitLinesModel
import com.fastaccess.data.dao.NameParser
import com.fastaccess.helper.ActivityHelper.checkAndRequestReadWritePermission
import com.fastaccess.helper.ActivityHelper.getActivity
import com.fastaccess.helper.ActivityHelper.shareUrl
import com.fastaccess.helper.AppHelper.copyToClipboard
import com.fastaccess.helper.BundleConstant
import com.fastaccess.helper.InputHelper.isEmpty
import com.fastaccess.provider.rest.RestProvider.downloadFile
import com.fastaccess.provider.rest.RestProvider.getRepoService
import com.fastaccess.ui.base.mvp.presenter.BasePresenter
import com.fastaccess.ui.modules.code.CodeViewerActivity.Companion.createIntent
import io.reactivex.Observable

/**
 * Created by Kosh on 15 Feb 2017, 10:10 PM
 */
class CommitFilesPresenter : BasePresenter<CommitFilesMvp.View>(),
    CommitFilesMvp.Presenter {
    @JvmField
    @com.evernote.android.state.State
    var sha: String? = null

    var changes = ArrayList<CommitFileChanges>()
    override fun onItemClick(position: Int, v: View?, item: CommitFileChanges) {
        if (v!!.id == R.id.patchList) {
            sendToView { view ->
                view.onOpenForResult(
                    position,
                    item
                )
            }
        } else if (v.id == R.id.open) {
            val commitFileModel = item.commitFileModel
            val popup = PopupMenu(v.context, v)
            val inflater = popup.menuInflater
            inflater.inflate(R.menu.commit_row_menu, popup.menu)
            popup.setOnMenuItemClickListener { item1: MenuItem ->
                when (item1.itemId) {
                    R.id.open -> v.context.startActivity(
                        createIntent(
                            v.context, commitFileModel!!.contentsUrl!!, commitFileModel.blobUrl!!
                        )
                    )
                    R.id.share -> shareUrl(v.context, commitFileModel!!.blobUrl!!)
                    R.id.download -> {
                        val activity = getActivity(v.context)
                        activity?.let {
                            if (checkAndRequestReadWritePermission(it)) {
                                downloadFile(v.context, commitFileModel!!.rawUrl!!)
                            }
                        }
                    }
                    R.id.copy -> copyToClipboard(v.context, commitFileModel!!.blobUrl!!)
                }
                true
            }
            popup.show()
        }
    }

    override fun onItemLongClick(position: Int, v: View?, item: CommitFileChanges) {}
    override fun onFragmentCreated(bundle: Bundle?) {
        if (sha == null) {
            if (bundle != null) {
                sha = bundle.getString(BundleConstant.ID)
            }
        }
        if (!isEmpty(sha)) {
            val commitFiles = CommitFilesSingleton.instance.getByCommitId(
                sha!!
            )
            if (commitFiles != null) {
                manageObservable(
                    Observable.just(commitFiles)
                        .map { CommitFileChanges.construct(it) }
                        .doOnSubscribe {
                            sendToView { it.clearAdapter() }
                        }
                        .doOnNext { commitFileChanges ->
                            sendToView { view ->
                                view.onNotifyAdapter(
                                    commitFileChanges
                                )
                            }
                        }
                        .doOnComplete { sendToView { it.hideProgress() } })
            }
        } else {
            throw NullPointerException("Bundle is null")
        }
    }

    override fun onSubmitComment(comment: String, item: CommitLinesModel, bundle: Bundle?) {
        if (bundle != null) {
            val blob = bundle.getString(BundleConstant.ITEM)
            val path = bundle.getString(BundleConstant.EXTRA)
            if (path == null || sha == null) return
            val commentRequestModel = CommentRequestModel()
            commentRequestModel.body = comment
            commentRequestModel.path = path
            commentRequestModel.position = item.position
            commentRequestModel.line =
                if (item.rightLineNo > 0) item.rightLineNo else item.leftLineNo
            val nameParser = NameParser(blob)
            onSubmit(nameParser.username, nameParser.name, commentRequestModel)
        }
    }

    override fun onSubmit(
        username: String?,
        name: String?,
        commentRequestModel: CommentRequestModel?
    ) {
        makeRestCall(
            getRepoService(isEnterprise).postCommitComment(
                username!!, name!!, sha!!,
                commentRequestModel!!
            )
        ) { newComment ->
            sendToView { view ->
                view.onCommentAdded(
                    newComment!!
                )
            }
        }
    }

    override fun onDestroy() {
        CommitFilesSingleton.instance.clear()
        super.onDestroy()
    }
}