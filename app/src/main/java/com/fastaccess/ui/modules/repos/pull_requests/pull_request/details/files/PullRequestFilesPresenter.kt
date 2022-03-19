package com.fastaccess.ui.modules.repos.pull_requests.pull_request.details.files

import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import com.fastaccess.R
import com.fastaccess.data.dao.CommitFileChanges
import com.fastaccess.data.dao.CommitFileChanges.Companion.construct
import com.fastaccess.data.dao.CommitFileModel
import com.fastaccess.data.dao.Pageable
import com.fastaccess.helper.ActivityHelper
import com.fastaccess.helper.AppHelper
import com.fastaccess.helper.BundleConstant
import com.fastaccess.helper.InputHelper.isEmpty
import com.fastaccess.provider.rest.RestProvider
import com.fastaccess.ui.base.mvp.presenter.BasePresenter
import com.fastaccess.ui.modules.code.CodeViewerActivity.Companion.createIntent
import com.fastaccess.ui.modules.repos.code.commit.details.CommitPagerActivity
import io.reactivex.Observable

/**
 * Created by Kosh on 03 Dec 2016, 3:48 PM
 */
class PullRequestFilesPresenter : BasePresenter<PullRequestFilesMvp.View>(),
    PullRequestFilesMvp.Presenter {
    @JvmField
    @com.evernote.android.state.State
    var login: String? = null

    @JvmField
    @com.evernote.android.state.State
    var repoId: String? = null

    @JvmField
    @com.evernote.android.state.State
    var number: Long = 0
    override val files = ArrayList<CommitFileChanges>()
    override var currentPage = 0
    override var previousTotal = 0
    private var lastPage = Int.MAX_VALUE
    override fun onError(throwable: Throwable) {
        onWorkOffline()
        super.onError(throwable)
    }

    override fun onCallApi(page: Int, parameter: String?): Boolean {
        if (page == 1) {
            lastPage = Int.MAX_VALUE
            sendToView { view -> view?.loadMore?.reset() }
        }
//        page = page
        if (page > lastPage || lastPage == 0) {
            sendToView { it?.hideProgress() }
            return false
        }
        if (repoId == null || login == null) return false
        makeRestCall<List<CommitFileChanges>>(RestProvider.getPullRequestService(isEnterprise)
            .getPullRequestFiles(login!!, repoId!!, number, page)
            .flatMap { commitFileModelPageable: Pageable<CommitFileModel> ->
                lastPage = commitFileModelPageable.last
                if (commitFileModelPageable.items != null) {
                    return@flatMap Observable.just(construct(commitFileModelPageable.items))
                }
                Observable.empty()
            }
        ) { response ->
            sendToView { view ->
                view?.onNotifyAdapter(
                    response,
                    page
                )
            }
        }
        return true
    }

    override fun onFragmentCreated(bundle: Bundle) {
        repoId = bundle.getString(BundleConstant.ID)
        login = bundle.getString(BundleConstant.EXTRA)
        number = bundle.getLong(BundleConstant.EXTRA_TWO)
        if (!isEmpty(login) && !isEmpty(repoId)) {
            onCallApi(1, null)
        }
    }

    override fun onWorkOffline() {
        sendToView { it?.hideProgress() }
    }

    override fun onItemClick(position: Int, v: View?, item: CommitFileChanges) {
        v ?: return
        if (v.id == R.id.patchList) {
            sendToView {
                it?.onOpenForResult(
                    position,
                    item
                )
            }
        } else if (v.id == R.id.open) {
            val fileModel = item.commitFileModel
            val popup = PopupMenu(v.context, v)
            val inflater = popup.menuInflater
            inflater.inflate(R.menu.commit_row_menu, popup.menu)
            popup.setOnMenuItemClickListener { item1: MenuItem ->
                when (item1.itemId) {
                    R.id.open -> v.context.startActivity(
                        createIntent(v.context, fileModel!!.contentsUrl!!, fileModel.blobUrl!!)
                    )
                    R.id.share -> ActivityHelper.shareUrl(v.context, fileModel!!.blobUrl!!)
                    R.id.download -> {
                        val activity = ActivityHelper.getActivity(v.context)
                        if (activity != null && ActivityHelper.checkAndRequestReadWritePermission(
                                activity
                            )
                        ) {
                            RestProvider.downloadFile(v.context, fileModel!!.rawUrl!!)
                        }
                    }
                    R.id.copy -> AppHelper.copyToClipboard(v.context, fileModel!!.blobUrl!!)
                }
                true
            }
            popup.show()
        }
    }

    override fun onItemLongClick(position: Int, v: View?, item: CommitFileChanges) {
        v ?: return
        v.context.startActivity(
            CommitPagerActivity.createIntent(
                v.context, repoId!!, login!!,
                Uri.parse(item.commitFileModel!!.contentsUrl).getQueryParameter("ref")!!
            )
        )
    }
}