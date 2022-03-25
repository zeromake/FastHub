package com.fastaccess.ui.modules.repos.code.files

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.fastaccess.R
import com.fastaccess.data.dao.EditRepoFileModel
import com.fastaccess.data.dao.model.Login
import com.fastaccess.data.dao.model.RepoFile
import com.fastaccess.data.dao.types.FilesType
import com.fastaccess.helper.ActivityHelper.shareUrl
import com.fastaccess.helper.AppHelper.copyToClipboard
import com.fastaccess.helper.BundleConstant
import com.fastaccess.helper.Bundler.Companion.start
import com.fastaccess.helper.FileHelper
import com.fastaccess.helper.InputHelper.isEmpty
import com.fastaccess.helper.PrefGetter.isAllFeaturesUnlocked
import com.fastaccess.helper.PrefGetter.isProEnabled
import com.fastaccess.provider.markdown.MarkDownProvider.isArchive
import com.fastaccess.provider.markdown.MarkDownProvider.isImage
import com.fastaccess.provider.rest.RestProvider.downloadFile
import com.fastaccess.ui.adapter.RepoFilesAdapter
import com.fastaccess.ui.base.BaseFragment
import com.fastaccess.ui.delegate.viewFind
import com.fastaccess.ui.modules.code.CodeViewerActivity.Companion.startActivity
import com.fastaccess.ui.modules.main.premium.PremiumActivity.Companion.startActivity
import com.fastaccess.ui.modules.repos.RepoPagerMvp
import com.fastaccess.ui.modules.repos.code.files.activity.RepoFilesActivity.Companion.startActivity
import com.fastaccess.ui.modules.repos.code.files.paths.RepoFilePathFragment
import com.fastaccess.ui.modules.repos.git.EditRepoFileActivity.Companion.startForResult
import com.fastaccess.ui.modules.repos.git.delete.DeleteFileBottomSheetFragment
import com.fastaccess.ui.modules.repos.git.delete.DeleteFileBottomSheetFragment.Companion.newInstance
import com.fastaccess.ui.widgets.StateLayout
import com.fastaccess.ui.widgets.dialog.MessageDialogView.Companion.newInstance
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView
import com.fastaccess.ui.widgets.recyclerview.scroll.RecyclerViewFastScroller

/**
 * Created by Kosh on 18 Feb 2017, 2:10 AM
 */
class RepoFilesFragment : BaseFragment<RepoFilesMvp.View, RepoFilesPresenter>(),
    RepoFilesMvp.View {
    val recycler: DynamicRecyclerView? by viewFind(R.id.recycler)
    val refresh: SwipeRefreshLayout? by viewFind(R.id.refresh)
    val stateLayout: StateLayout? by viewFind(R.id.stateLayout)
    val fastScroller: RecyclerViewFastScroller? by viewFind(R.id.fastScroller)
    private var adapter: RepoFilesAdapter? = null
    private var login: Login? = null
    private var parentFragment: RepoFilePathFragment? = null
    private var repoCallback: RepoPagerMvp.View? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (getParentFragment() is RepoPagerMvp.View) {
            repoCallback = getParentFragment() as RepoPagerMvp.View?
        } else if (context is RepoPagerMvp.View) {
            repoCallback = context
        }
    }

    override fun onDetach() {
        repoCallback = null
        super.onDetach()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onNotifyAdapter(list: List<RepoFile>) {
        adapter!!.insertItems(list)
        hideProgress()
        adapter!!.notifyDataSetChanged()
    }

    override fun onNotifyFile(f: RepoFile) {
        hideProgress()
        val url = if (isEmpty(f.downloadUrl)) f.url else f.downloadUrl
        if (isEmpty(url)) return
        startActivity(requireContext(), url, f.htmlUrl)
    }

    override fun onItemClicked(model: RepoFile) {
        if (refresh!!.isRefreshing) return
        if (model.type === FilesType.dir) {
            if (parent != null) {
                parent!!.onAppendPath(model)
            }
        } else {
            if (model.size == 0L && isEmpty(model.downloadUrl) && !isEmpty(model.gitUrl)) {
                startActivity(requireContext(), model.gitUrl.replace("trees/", ""), isEnterprise)
            } else {
                val url = if (isEmpty(model.downloadUrl)) model.url else model.downloadUrl
                if (isEmpty(url)) return
                if (model.size > FileHelper.ONE_MB && !isImage(url)) {
                    newInstance(
                        getString(R.string.big_file), getString(R.string.big_file_description),
                        isMarkDown = false, hideCancel = true, bundle = start()
                            .put(BundleConstant.EXTRA, model.downloadUrl)
                            .put(BundleConstant.YES_NO_EXTRA, true)
                            .end()
                    )
                        .show(childFragmentManager, "MessageDialogView")
                } else {
                    startActivity(requireContext(), url, model.htmlUrl)
                }
            }
        }
    }

    override fun onMenuClicked(position: Int, model: RepoFile, v: View?) {
        if (login == null) {
            login = Login.getUser()
        }
        if (refresh!!.isRefreshing) return
        val isOwner =
            login!!.login == presenter!!.login || repoCallback != null && repoCallback!!.isCollaborator
        val popup = PopupMenu(context, v)
        val inflater = popup.menuInflater
        inflater.inflate(R.menu.download_share_menu, popup.menu)
        popup.menu.findItem(R.id.download).isVisible = model.type === FilesType.file
        val canOpen = canOpen(model)
        popup.menu.findItem(R.id.editFile).isVisible =
            isOwner && model.type === FilesType.file && canOpen
        popup.menu.findItem(R.id.deleteFile).isVisible = isOwner && model.type === FilesType.file
        popup.setOnMenuItemClickListener { item1: MenuItem ->
            when (item1.itemId) {
                R.id.share -> shareUrl(
                    v!!.context, model.htmlUrl
                )
                R.id.download -> downloadFile(requireContext(), model.downloadUrl)
                R.id.copy -> copyToClipboard(
                    v!!.context,
                    if (!isEmpty(model.htmlUrl)) model.htmlUrl else model.url
                )
                R.id.editFile -> if (isProEnabled || isAllFeaturesUnlocked) {
                    if (canOpen) {
                        val fileModel = EditRepoFileModel(
                            presenter!!.login!!,
                            presenter!!.repoId!!,
                            model.path,
                            presenter!!.ref!!,
                            model.sha,
                            model.downloadUrl,
                            model.name,
                            true
                        )
                        startForResult(requireContext(), launcher, fileModel, isEnterprise)
                    }
                } else {
                    startActivity(requireContext())
                }
                R.id.deleteFile -> if (isProEnabled || isAllFeaturesUnlocked) {
                    newInstance(position, model.name)
                        .show(
                            childFragmentManager,
                            DeleteFileBottomSheetFragment::class.java.simpleName
                        )
                } else {
                    startActivity(requireContext())
                }
            }
            true
        }
        popup.show()
    }

    override fun onSetData(
        login: String, repoId: String, path: String,
        ref: String, clear: Boolean, toAppend: RepoFile?
    ) {
        presenter!!.onInitDataAndRequest(login, repoId, path, ref, clear, toAppend)
    }

    override val isRefreshing: Boolean
        get() = refresh!!.isRefreshing

    override fun onUpdateTab(toAppend: RepoFile?) {
        parent!!.onAppenedtab(toAppend)
    }

    override fun fragmentLayout(): Int {
        return R.layout.vertical_refresh_list
    }

    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        stateLayout!!.setEmptyText(R.string.no_files)
        refresh!!.setOnRefreshListener(this)
        stateLayout!!.setOnReloadListener { onRefresh() }
        recycler!!.setEmptyView(stateLayout!!, refresh)
        adapter = RepoFilesAdapter(presenter!!.files)
        adapter!!.listener = presenter
        recycler!!.adapter = adapter
        fastScroller!!.visibility = View.GONE
    }

    override fun showProgress(@StringRes resId: Int) {
        refresh!!.isRefreshing = true
        stateLayout!!.showProgress()
    }

    override fun hideProgress() {
        refresh!!.isRefreshing = false
        stateLayout!!.hideProgress()
    }

    override fun showErrorMessage(msgRes: String) {
        showReload()
        super.showErrorMessage(msgRes)
    }

    override fun showMessage(titleRes: Int, msgRes: Int) {
        showReload()
        super.showMessage(titleRes, msgRes)
    }

    override fun providePresenter(): RepoFilesPresenter {
        return RepoFilesPresenter()
    }

    override fun onRefresh() {
        presenter!!.onCallApi(null)
    }

    override fun onMessageDialogActionClicked(isOk: Boolean, bundle: Bundle?) {
        super.onMessageDialogActionClicked(isOk, bundle)
        if (isOk && bundle != null) {
            val url = bundle.getString(BundleConstant.EXTRA)
            if (!isEmpty(url)) {
                downloadFile(requireContext(), url!!)
            }
        }
    }

    val launcher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) {
            onRefresh()
        }
    }

    override fun onDelete(message: String, position: Int) {
        presenter!!.onDeleteFile(
            message,
            adapter!!.getItem(position)!!,
            if (parent != null) parent!!.getRef() else "master"
        )
    }

    private fun showReload() {
        hideProgress()
        stateLayout!!.showReload(adapter!!.itemCount)
    }

    private val parent: RepoFilePathFragment?
        get() {
            if (parentFragment == null) {
                parentFragment = getParentFragment() as RepoFilePathFragment?
            }
            return parentFragment
        }

    private fun canOpen(item: RepoFile): Boolean {
        return (item.downloadUrl != null && !isImage(item.downloadUrl)
                && !isArchive(item.downloadUrl))
    }
}