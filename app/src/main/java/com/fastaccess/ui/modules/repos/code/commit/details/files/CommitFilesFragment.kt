package com.fastaccess.ui.modules.repos.code.commit.details.files

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.evernote.android.state.State
import com.fastaccess.R
import com.fastaccess.data.dao.*
import com.fastaccess.data.dao.model.Comment
import com.fastaccess.helper.ActivityHelper.startCustomTab
import com.fastaccess.helper.BundleConstant
import com.fastaccess.helper.Bundler.Companion.start
import com.fastaccess.helper.InputHelper.isEmpty
import com.fastaccess.helper.PrefGetter.isProEnabled
import com.fastaccess.provider.scheme.SchemeParser.launchUri
import com.fastaccess.ui.adapter.CommitFilesAdapter
import com.fastaccess.ui.base.BaseFragment
import com.fastaccess.ui.delegate.viewFind
import com.fastaccess.ui.modules.main.premium.PremiumActivity.Companion.startActivity
import com.fastaccess.ui.modules.repos.code.commit.details.CommitPagerMvp
import com.fastaccess.ui.modules.repos.pull_requests.pull_request.details.files.fullscreen.FullScreenFileChangeActivity.Companion.startLauncherForResult
import com.fastaccess.ui.modules.reviews.AddReviewDialogFragment.Companion.newInstance
import com.fastaccess.ui.widgets.StateLayout
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView
import com.fastaccess.ui.widgets.recyclerview.scroll.RecyclerViewFastScroller

/**
 * Created by Kosh on 15 Feb 2017, 10:16 PM
 */
class CommitFilesFragment : BaseFragment<CommitFilesMvp.View, CommitFilesPresenter>(),
    CommitFilesMvp.View {
    val recycler: DynamicRecyclerView? by viewFind(R.id.recycler)
    val refresh: SwipeRefreshLayout? by viewFind(R.id.refresh)
    val stateLayout: StateLayout? by viewFind(R.id.stateLayout)
    val fastScroller: RecyclerViewFastScroller? by viewFind(R.id.fastScroller)

    @JvmField
    @State
    var toggleMap: HashMap<Long, Boolean> = LinkedHashMap()
    private var viewCallback: CommitPagerMvp.View? = null
    private var adapter: CommitFilesAdapter? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (parentFragment is CommitPagerMvp.View) {
            viewCallback = parentFragment as CommitPagerMvp.View?
        } else if (context is CommitPagerMvp.View) {
            viewCallback = context
        }
    }

    override fun onDetach() {
        viewCallback = null
        super.onDetach()
    }

    override fun onNotifyAdapter(items: List<CommitFileChanges>?) {
        hideProgress()
        if (items != null) {
            adapter!!.insertItems(items)
        }
    }

    override fun onCommentAdded(newComment: Comment) {
        hideProgress()
        viewCallback?.onAddComment(newComment)
    }

    override fun clearAdapter() {
        refresh!!.isRefreshing = true
        adapter!!.clear()
    }

    val launcher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
    ) {
        if (it.resultCode == Activity.RESULT_OK && it.data != null) {
            val data = it.data!!
            val comments: List<CommentRequestModel>? =
                data.getParcelableArrayListExtra(BundleConstant.ITEM)
            if (comments != null && comments.isNotEmpty()) {
                if (viewCallback != null && !isEmpty(viewCallback!!.login)) {
                    presenter!!.onSubmit(
                        viewCallback!!.login,
                        viewCallback!!.repoId,
                        comments[0]
                    )
                }
            }
        }
    }

    override fun onOpenForResult(position: Int, model: CommitFileChanges?) {
        startLauncherForResult(
            requireContext(),
            launcher,
            model!!,
            position,
            true,
        )
    }

    override fun hideProgress() {
        refresh?.isRefreshing = false
    }

    override fun fragmentLayout(): Int {
        return R.layout.micro_grid_refresh_list
    }

    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        refresh!!.isEnabled = false
        stateLayout!!.setEmptyText(R.string.no_files)
        recycler!!.setEmptyView(stateLayout!!, refresh)
        adapter = CommitFilesAdapter(presenter!!.changes, this, this)
        adapter!!.listener = presenter
        recycler!!.adapter = adapter
        if (presenter!!.changes.isEmpty()) presenter!!.onFragmentCreated(arguments)
        fastScroller!!.attachRecyclerView(recycler!!)
    }

    override fun providePresenter(): CommitFilesPresenter {
        return CommitFilesPresenter()
    }

    override fun onToggle(id: Long, isCollapsed: Boolean): Boolean {
        val model = adapter!!.getItem(id.toInt()) ?: return false
        if (model.commitFileModel!!.patch == null) {
            if ("renamed".equals(model.commitFileModel!!.status, ignoreCase = true)) {
                launchUri(requireContext(), model.commitFileModel!!.blobUrl!!)
                toggleMap[id] = false
                return false
            }
            startCustomTab(
                requireActivity(),
                adapter!!.getItem(id.toInt())!!.commitFileModel!!.blobUrl!!
            )
            toggleMap[id] = false
            return false
        } else {
            toggleMap[id] = isCollapsed
        }
        return true
    }

    override fun isCollapsed(id: Long): Boolean {
        val toggle = toggleMap[id]
        return toggle ?: false
    }

    override fun onScrollTop(index: Int) {
        super.onScrollTop(index)
        recycler?.scrollToPosition(0)
    }

    override fun onPatchClicked(
        groupPosition: Int,
        childPosition: Int,
        v: View?,
        commit: CommitFileModel,
        item: CommitLinesModel
    ) {
        if (item.text!!.startsWith("@@")) return
        if (isProEnabled) {
            newInstance(
                item, start()
                    .put(BundleConstant.ITEM, commit.blobUrl)
                    .put(BundleConstant.EXTRA, commit.filename)
                    .end()
            )
                .show(childFragmentManager, "AddReviewDialogFragment")
        } else {
            startActivity(requireContext())
        }
    }

    override fun onCommentAdded(comment: String, item: CommitLinesModel, bundle: Bundle?) {
        presenter!!.onSubmitComment(comment, item, bundle)
    }

    companion object {
        fun newInstance(sha: String, commitFileModels: CommitFileListModel?): CommitFilesFragment {
            val view = CommitFilesFragment()
            if (commitFileModels != null) {
                CommitFilesSingleton.instance.putFiles(sha, commitFileModels)
            }
            val bundle = start().put(BundleConstant.ID, sha).end()
            view.arguments = bundle
            return view
        }
    }
}