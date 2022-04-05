package com.fastaccess.ui.modules.repos.code.files.paths

import android.net.Uri
import android.os.Bundle
import android.view.View
import com.fastaccess.data.dao.model.RepoFile
import com.fastaccess.helper.BundleConstant
import com.fastaccess.helper.InputHelper.isEmpty
import com.fastaccess.ui.base.mvp.presenter.BasePresenter

/**
 * Created by Kosh on 15 Feb 2017, 10:10 PM
 */
class RepoFilePathPresenter : BasePresenter<RepoFilePathMvp.View>(),
    RepoFilePathMvp.Presenter {
    @com.evernote.android.state.State
    override var repoId: String? = null

    @com.evernote.android.state.State
    override var login: String? = null

    @com.evernote.android.state.State
    override var path: String? = null

    @com.evernote.android.state.State
    override var defaultBranch: String? = null
    override val paths = ArrayList<RepoFile>()
    override fun onItemClick(position: Int, v: View?, item: RepoFile) {
        if (!item.path.equals(path, ignoreCase = true)) if (view != null) view!!.onItemClicked(
            item,
            position
        )
    }

    override fun onItemLongClick(position: Int, v: View?, item: RepoFile) {}
    override fun onFragmentCreated(bundle: Bundle?) {
        if (bundle != null) {
            repoId = bundle.getString(BundleConstant.ID)
            login = bundle.getString(BundleConstant.EXTRA)
            path = bundle.getString(BundleConstant.EXTRA_TWO) ?: ""
            defaultBranch = bundle.getString(BundleConstant.EXTRA_THREE) ?: "master"
            val forceAppend = bundle.getBoolean(BundleConstant.EXTRA_FOUR)
            if (isEmpty(repoId) || isEmpty(login)) {
                throw NullPointerException(
                    String.format(
                        "error, repoId(%s) or login(%s) is null",
                        repoId,
                        login
                    )
                )
            }
            if (forceAppend && paths.isEmpty()) {
                val repoFiles: MutableList<RepoFile> = ArrayList()
                if (!isEmpty(path)) {
                    val uri = Uri.parse(path)
                    val builder = StringBuilder()
                    if (uri.pathSegments != null && uri.pathSegments.isNotEmpty()) {
                        val pathSegments = uri.pathSegments
                        for (i in pathSegments.indices) {
                            val name = pathSegments[i]
                            val file = RepoFile()
                            if (i == 0) {
                                builder.append(name)
                            } else {
                                builder.append("/").append(name)
                            }
                            file.path = builder.toString()
                            file.name = name
                            repoFiles.add(file)
                        }
                    }
                    if (repoFiles.isNotEmpty()) {
                        sendToView { view ->
                            view.onNotifyAdapter(
                                repoFiles,
                                1
                            )
                        }
                    }
                }
            }
            sendToView { it.onSendData() }
        } else {
            throw NullPointerException("Bundle is null")
        }
    }
}