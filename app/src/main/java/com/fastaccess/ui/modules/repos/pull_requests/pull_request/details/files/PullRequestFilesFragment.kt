package com.fastaccess.ui.modules.repos.pull_requests.pull_request.details.files

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.evernote.android.state.State
import com.fastaccess.R
import com.fastaccess.data.dao.CommentRequestModel
import com.fastaccess.data.dao.CommitFileChanges
import com.fastaccess.data.dao.CommitFileModel
import com.fastaccess.data.dao.CommitLinesModel
import com.fastaccess.data.entity.PullRequest
import com.fastaccess.helper.ActivityHelper
import com.fastaccess.helper.BundleConstant
import com.fastaccess.helper.Bundler
import com.fastaccess.helper.PrefGetter.isProEnabled
import com.fastaccess.provider.rest.loadmore.OnLoadMore
import com.fastaccess.provider.scheme.SchemeParser.launchUri
import com.fastaccess.ui.adapter.CommitFilesAdapter
import com.fastaccess.ui.base.BaseFragment
import com.fastaccess.ui.delegate.viewFind
import com.fastaccess.ui.modules.main.premium.PremiumActivity.Companion.startActivity
import com.fastaccess.ui.modules.repos.issues.issue.details.IssuePagerMvp.IssuePrCallback
import com.fastaccess.ui.modules.repos.pull_requests.pull_request.details.files.PullRequestFilesMvp.PatchCallback
import com.fastaccess.ui.modules.repos.pull_requests.pull_request.details.files.fullscreen.FullScreenFileChangeActivity.Companion.startLauncherForResult
import com.fastaccess.ui.modules.reviews.AddReviewDialogFragment.Companion.newInstance
import com.fastaccess.ui.widgets.StateLayout
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView
import com.fastaccess.ui.widgets.recyclerview.scroll.RecyclerViewFastScroller

/**
 * Created by Kosh on 03 Dec 2016, 3:56 PM
 */
class PullRequestFilesFragment :
    BaseFragment<PullRequestFilesMvp.View, PullRequestFilesPresenter>(),
    PullRequestFilesMvp.View {
    val recycler: DynamicRecyclerView? by viewFind(R.id.recycler)
    val refresh: SwipeRefreshLayout? by viewFind(R.id.refresh)
    val stateLayout: StateLayout? by viewFind(R.id.stateLayout)
    val fastScroller: RecyclerViewFastScroller? by viewFind(R.id.fastScroller)
    val changes: TextView? by viewFind(R.id.changes)
    val deletion: TextView? by viewFind(R.id.deletion)
    val addition: TextView? by viewFind(R.id.addition)

    @State
    var toggleMap: HashMap<Long, Boolean> = LinkedHashMap()
    private var viewCallback: PatchCallback? = null
    private var onLoadMore: OnLoadMore<String>? = null
    private var adapter: CommitFilesAdapter? = null
    private var issueCallback: IssuePrCallback<PullRequest>? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        issueCallback = when {
            parentFragment is IssuePrCallback<*> -> {
                @Suppress("UNCHECKED_CAST")
                parentFragment as IssuePrCallback<PullRequest>
            }
            context is IssuePrCallback<*> -> {
                @Suppress("UNCHECKED_CAST")
                context as IssuePrCallback<PullRequest>
            }
            else -> {
                throw IllegalArgumentException(
                    String.format(
                        "%s or parent fragment must implement IssuePagerMvp.IssuePrCallback",
                        context.javaClass
                            .simpleName
                    )
                )
            }
        }
        if (parentFragment is PatchCallback) {
            viewCallback = parentFragment as PatchCallback?
        } else if (context is PatchCallback) {
            viewCallback = context
        }
    }

    override fun onDetach() {
        issueCallback = null
        viewCallback = null
        super.onDetach()
    }

    override fun onNotifyAdapter(items: List<CommitFileChanges>, page: Int) {
        hideProgress()
        if (items.isEmpty()) {
            adapter!!.clear()
            return
        }
        if (page <= 1) {
            adapter!!.insertItems(items)
        } else {
            adapter!!.addItems(items)
        }
    }

    override fun fragmentLayout(): Int {
        return R.layout.pull_request_files_layout
    }

    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        if (arguments == null) {
            throw NullPointerException("Bundle is null, therefore, PullRequestFilesFragment can't be proceeded.")
        }
        setupChanges()
        stateLayout!!.setEmptyText(R.string.no_commits)
        stateLayout!!.setOnReloadListener(this)
        refresh!!.setOnRefreshListener(this)
        recycler!!.setEmptyView(stateLayout!!, refresh)
        adapter = CommitFilesAdapter(presenter!!.files, this, this)
        adapter!!.listener = presenter
        loadMore.initialize(presenter!!.currentPage, presenter!!.previousTotal)
        recycler!!.adapter = adapter
        recycler!!.addOnScrollListener(loadMore)
        if (savedInstanceState == null) {
            presenter!!.onFragmentCreated(requireArguments())
        } else if (presenter!!.files.isEmpty() && !presenter!!.isApiCalled) {
            onRefresh()
        }
        fastScroller!!.attachRecyclerView(recycler!!)
    }

    private fun setupChanges() {
        val pullRequest = issueCallback!!.data
        if (pullRequest != null) {
            addition!!.text = pullRequest.additions.toString()
            deletion!!.text = pullRequest.deletions.toString()
            changes!!.text = pullRequest.changedFiles.toString()
        }
    }

    override fun providePresenter(): PullRequestFilesPresenter {
        return PullRequestFilesPresenter()
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

    override val loadMore: OnLoadMore<String>
        get() {
            if (onLoadMore == null) {
                onLoadMore = OnLoadMore(presenter)
            }
            return onLoadMore!!
        }

    val launcher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK && it.data != null) {
            val data = it.data!!
            val comments: List<CommentRequestModel> =
                data.getParcelableArrayListExtra(BundleConstant.ITEM) ?: listOf()
            if (comments.isNotEmpty()) {
                if (viewCallback != null) {
                    for (comment in comments) {
                        viewCallback!!.onAddComment(comment)
                    }
                    showMessage(R.string.success, R.string.comments_added_successfully)
                }
            }
        }
    }

    override fun onOpenForResult(position: Int, linesModel: CommitFileChanges) {
        startLauncherForResult(requireContext(), launcher, linesModel, position, false)
    }

    override fun onRefresh() {
        presenter!!.onCallApi(1, null)
    }

    override fun onClick(view: View) {
        onRefresh()
    }

    override fun onToggle(id: Long, isCollapsed: Boolean): Boolean {
        val model = adapter!!.getItem(id.toInt()) ?: return false
        if (model.commitFileModel!!.patch == null) {
            if ("renamed".equals(model.commitFileModel!!.status, ignoreCase = true)) {
                launchUri(requireContext(), model.commitFileModel!!.blobUrl!!)
                return false
            }
            ActivityHelper.startCustomTab(
                requireActivity(),
                adapter!!.getItem(id.toInt())!!.commitFileModel!!.blobUrl!!
            )
            return false
        }
        toggleMap[id] = isCollapsed
        return true
    }

    override fun isCollapsed(id: Long): Boolean {
        val toggle = toggleMap[id]
        return toggle != null && toggle
    }

    override fun onScrollTop(index: Int) {
        super.onScrollTop(index)
        if (recycler != null) recycler!!.scrollToPosition(0)
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
                item, Bundler.start()
                    .put(BundleConstant.ITEM, commit.filename)
                    .put(BundleConstant.EXTRA_TWO, groupPosition)
                    .put(BundleConstant.EXTRA_THREE, childPosition)
                    .end()
            )
                .show(childFragmentManager, "AddReviewDialogFragment")
        } else {
            startActivity(requireContext())
        }
    }

    override fun onCommentAdded(comment: String, item: CommitLinesModel, bundle: Bundle?) {
        if (bundle != null) {
            val path = bundle.getString(BundleConstant.ITEM) ?: return
            val commentRequestModel = CommentRequestModel()
            commentRequestModel.body = comment
            commentRequestModel.path = path
            commentRequestModel.position = item.position
            if (viewCallback != null) viewCallback!!.onAddComment(commentRequestModel)
            val groupPosition = bundle.getInt(BundleConstant.EXTRA_TWO)
            val childPosition = bundle.getInt(BundleConstant.EXTRA_THREE)
            val commitFileChanges = adapter!!.getItem(groupPosition)!!
            val models: MutableList<CommitLinesModel> =
                commitFileChanges.linesModel?.toMutableList() ?: mutableListOf()
            if (models.isNotEmpty()) {
                val current = models[childPosition]
                current.isHasCommentedOn = true
                models[childPosition] = current
                adapter!!.notifyItemChanged(groupPosition)
            }
        }
    }

    private fun showReload() {
        hideProgress()
        stateLayout!!.showReload(adapter!!.itemCount)
    }

    companion object {
        fun newInstance(repoId: String, login: String, number: Long): PullRequestFilesFragment {
            val view = PullRequestFilesFragment()
            view.arguments = Bundler.start()
                .put(BundleConstant.ID, repoId)
                .put(BundleConstant.EXTRA, login)
                .put(BundleConstant.EXTRA_TWO, number)
                .end()
            return view
        }
    }
}