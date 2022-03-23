package com.fastaccess.ui.modules.repos.code.files.paths

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView
import com.evernote.android.state.State
import com.fastaccess.R
import com.fastaccess.data.dao.BranchesModel
import com.fastaccess.data.dao.EditRepoFileModel
import com.fastaccess.data.dao.model.Login
import com.fastaccess.data.dao.model.RepoFile
import com.fastaccess.helper.BundleConstant
import com.fastaccess.helper.Bundler.Companion.start
import com.fastaccess.helper.InputHelper.isEmpty
import com.fastaccess.helper.PrefGetter.isAllFeaturesUnlocked
import com.fastaccess.helper.PrefGetter.isProEnabled
import com.fastaccess.provider.rest.RestProvider.downloadFile
import com.fastaccess.ui.adapter.RepoFilePathsAdapter
import com.fastaccess.ui.base.BaseFragment
import com.fastaccess.ui.delegate.viewFind
import com.fastaccess.ui.modules.main.premium.PremiumActivity.Companion.startActivity
import com.fastaccess.ui.modules.repos.RepoPagerMvp
import com.fastaccess.ui.modules.repos.code.files.RepoFilesFragment
import com.fastaccess.ui.modules.repos.extras.branches.pager.BranchesPagerFragment.Companion.newInstance
import com.fastaccess.ui.modules.repos.git.EditRepoFileActivity.Companion.startForResult
import com.fastaccess.ui.modules.search.repos.files.SearchFileActivity.Companion.createIntent
import com.fastaccess.ui.widgets.FontTextView
import com.fastaccess.ui.widgets.dialog.MessageDialogView
import com.fastaccess.ui.widgets.dialog.MessageDialogView.Companion.newInstance
import com.fastaccess.utils.setOnThrottleClickListener

/**
 * Created by Kosh on 18 Feb 2017, 2:10 AM
 */
class RepoFilePathFragment : BaseFragment<RepoFilePathMvp.View, RepoFilePathPresenter>(),
    RepoFilePathMvp.View {
    @State
    var refs: String? = null
    val recycler: RecyclerView? by viewFind(R.id.recycler)
    private val toParentFolder: View? by viewFind(R.id.toParentFolder)
    val branches: FontTextView? by viewFind(R.id.branches)
    private val addFile: View? by viewFind(R.id.addFile)

    private var repoCallback: RepoPagerMvp.View? = null
    private var adapter: RepoFilePathsAdapter? = null
    private var repoFilesView: RepoFilesFragment? = null
        get() {
            if (field == null) {
                field =
                    childFragmentManager.findFragmentById(
                        R.id.filesFragment
                    ) as RepoFilesFragment?
            }
            return field
        }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (parentFragment is RepoPagerMvp.View) {
            repoCallback = parentFragment as RepoPagerMvp.View?
        } else if (context is RepoPagerMvp.View) {
            repoCallback = context
        }
    }

    override fun onDetach() {
        repoCallback = null
        super.onDetach()
    }

    private fun onAddFile() {
        if (isProEnabled || isAllFeaturesUnlocked) {
            val repoFile =
                if (!adapter!!.isEmpty) adapter!!.getItem(adapter!!.itemCount - 1) else null
            val fileModel = EditRepoFileModel(
                presenter!!.login!!,
                presenter!!.repoId!!,
                repoFile?.path ?: "",
                refs!!,
                repoFile?.sha ?: "",
                null,
                null,
                false
            )
            startForResult(requireContext(), launcher, fileModel, isEnterprise)
        } else {
            startActivity(requireContext())
        }
    }

    private fun onDownloadRepoFiles() {
        if (isEmpty(refs)) {
            refs = presenter!!.defaultBranch
        }
        newInstance(
            getString(R.string.download), getString(R.string.confirm_message),
            start()
                .put(BundleConstant.YES_NO_EXTRA, true)
                .end()
        )
            .show(childFragmentManager, MessageDialogView.TAG)
    }

    private fun onSearchClicked() {
        startActivity(
            createIntent(
                requireContext(),
                presenter!!.login!!,
                presenter!!.repoId!!,
                isEnterprise
            )
        )
    }

    private fun onBackClicked() {
        if (adapter!!.itemCount > 0) {
            adapter!!.clear()
            repoFilesView!!.onSetData(
                presenter!!.login!!,
                presenter!!.repoId!!,
                "",
                refs!!,
                false,
                null
            )
        }
    }

    private fun onBranchesClicked() {
        newInstance(presenter!!.login!!, presenter!!.repoId!!)
            .show(childFragmentManager, "BranchesFragment")
    }

    override fun onNotifyAdapter(items: List<RepoFile>?, page: Int) {
        hideProgress()
        if (items == null || items.isEmpty()) {
            adapter!!.clear()
            return
        }
        if (page <= 1) {
            adapter!!.insertItems(items)
        } else {
            adapter!!.addItems(items)
        }
        recycler!!.smoothScrollToPosition(adapter!!.itemCount - 1)
    }

    override fun onItemClicked(model: RepoFile, position: Int) {
        if (repoFilesView?.isRefreshing == true) return
        repoFilesView!!.onSetData(
            presenter!!.login!!, presenter!!.repoId!!,
            model.path ?: "", refs!!, false, null
        )
        if (position + 1 < adapter!!.itemCount) {
            adapter!!.subList(position + 1, adapter!!.itemCount)
        }
        recycler!!.scrollToPosition(adapter!!.itemCount - 1)
    }

    override fun onAppendPath(model: RepoFile) {
        repoFilesView?.onSetData(
            presenter!!.login!!, presenter!!.repoId!!,
            model.path ?: "", refs!!, false, model
        )
    }

    override fun onAppenedtab(repoFile: RepoFile?) {
        if (repoFile != null) {
            adapter!!.addItem(repoFile)
            recycler!!.scrollToPosition(adapter!!.itemCount - 1)
        }
    }

    override fun onSendData() {
        if (isEmpty(refs)) {
            refs = presenter!!.defaultBranch
        }
        repoFilesView?.onSetData(
            presenter!!.login!!, presenter!!.repoId!!,
            presenter!!.path ?: "", refs!!, false, null
        )
    }

    override fun canPressBack(): Boolean {
        return adapter == null || adapter!!.itemCount == 0
    }

    override fun onBackPressed() {
        if (repoFilesView?.isRefreshing == true) return
        if (adapter!!.itemCount > 1) {
            adapter!!.removeItem(adapter!!.itemCount - 1)
            val model = adapter!!.getItem(adapter!!.itemCount - 1)
            repoFilesView!!.onSetData(
                presenter!!.login!!, presenter!!.repoId!!,
                model!!.path ?: "", refs!!, false, null
            )
            recycler!!.scrollToPosition(adapter!!.itemCount - 1)
        } else {
            onBackClicked()
        }
    }

    override fun showProgress(@StringRes resId: Int) {}
    override fun hideProgress() {}
    override fun showErrorMessage(msgRes: String) {
        showReload()
        super.showErrorMessage(msgRes)
    }

    override fun showMessage(titleRes: Int, msgRes: Int) {
        showReload()
        super.showMessage(titleRes, msgRes)
    }

    override fun fragmentLayout(): Int {
        return R.layout.repo_file_layout
    }

    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        addFile!!.setOnThrottleClickListener {
            onAddFile()
        }
        view.findViewById<View>(R.id.downloadRepoFiles).setOnThrottleClickListener {
            onDownloadRepoFiles()
        }
        view.findViewById<View>(R.id.searchRepoFiles).setOnThrottleClickListener {
            onSearchClicked()
        }
        toParentFolder!!.setOnThrottleClickListener {
            onBackClicked()
        }
        branches!!.setOnThrottleClickListener {
            onBranchesClicked()
        }
        adapter = RepoFilePathsAdapter(presenter!!.paths)
        adapter!!.listener = presenter
        recycler!!.adapter = adapter
        if (savedInstanceState == null) {
            presenter!!.onFragmentCreated(arguments)
        } else if (presenter!!.paths.isEmpty() && !presenter!!.isApiCalled) {
            presenter!!.onFragmentCreated(arguments)
        }
        refs = presenter!!.defaultBranch
        branches!!.text = refs
        if (Login.getUser().login.equals(
                presenter!!.login,
                ignoreCase = true
            ) || repoCallback != null && repoCallback!!.isCollaborator
        ) {
            addFile!!.visibility = View.VISIBLE
        }
    }

    override fun onMessageDialogActionClicked(isOk: Boolean, bundle: Bundle?) {
        super.onMessageDialogActionClicked(isOk, bundle)
        if (isOk && bundle != null) {
            val isDownload = bundle.getBoolean(BundleConstant.YES_NO_EXTRA)
            if (isDownload) {
                val uri = Uri.Builder()
                    .scheme("https")
                    .authority("github.com")
                    .appendPath(presenter!!.login)
                    .appendPath(presenter!!.repoId)
                    .appendPath("archive")
                    .appendPath("$refs.zip")
                    .build()
                downloadFile(requireContext(), uri.toString())
            }
        }
    }

    override fun providePresenter(): RepoFilePathPresenter {
        return RepoFilePathPresenter()
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isSafe && repoFilesView != null) repoFilesView!!.onHiddenChanged(!isVisibleToUser)
    }

    override fun onScrollTop(index: Int) {
        super.onScrollTop(index)
        if (repoFilesView != null) repoFilesView!!.onScrollTop(index)
    }

    override fun onBranchSelected(branch: BranchesModel) {
        refs = branch.name
        branches!!.text = refs
        repoFilesView?.onSetData(
            presenter!!.login!!,
            presenter!!.repoId!!,
            "",
            refs!!,
            true,
            null
        )
        onBackClicked()
    }

    val launcher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) {
            repoFilesView?.onRefresh()
        }
    }

    private fun showReload() {
        hideProgress()
    }

    fun getRef(): String {
        return if (!isEmpty(refs)) refs!! else "master"
    }

    companion object {
        @JvmOverloads
        fun newInstance(
            login: String, repoId: String,
            path: String?, defaultBranch: String,
            forceAppendPath: Boolean = false
        ): RepoFilePathFragment {
            val view = RepoFilePathFragment()
            view.arguments = start()
                .put(BundleConstant.ID, repoId)
                .put(BundleConstant.EXTRA, login)
                .put(BundleConstant.EXTRA_TWO, path)
                .put(BundleConstant.EXTRA_THREE, defaultBranch)
                .put(BundleConstant.EXTRA_FOUR, forceAppendPath)
                .end()
            return view
        }
    }
}