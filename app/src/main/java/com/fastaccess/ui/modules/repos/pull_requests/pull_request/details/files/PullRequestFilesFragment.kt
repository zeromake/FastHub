package com.fastaccess.ui.modules.repos.pull_requests.pull_request.details.files

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import butterknife.BindView
import com.evernote.android.state.State
import com.fastaccess.R
import com.fastaccess.data.dao.CommentRequestModel
import com.fastaccess.data.dao.CommitFileChanges
import com.fastaccess.data.dao.CommitFileModel
import com.fastaccess.data.dao.CommitLinesModel
import com.fastaccess.data.dao.model.PullRequest
import com.fastaccess.helper.ActivityHelper
import com.fastaccess.helper.BundleConstant
import com.fastaccess.helper.Bundler
import com.fastaccess.helper.PrefGetter.isProEnabled
import com.fastaccess.provider.rest.loadmore.OnLoadMore
import com.fastaccess.provider.scheme.SchemeParser.launchUri
import com.fastaccess.ui.adapter.CommitFilesAdapter
import com.fastaccess.ui.base.BaseFragment
import com.fastaccess.ui.modules.main.premium.PremiumActivity.Companion.startActivity
import com.fastaccess.ui.modules.repos.issues.issue.details.IssuePagerMvp.IssuePrCallback
import com.fastaccess.ui.modules.repos.pull_requests.pull_request.details.files.PullRequestFilesMvp.PatchCallback
import com.fastaccess.ui.modules.repos.pull_requests.pull_request.details.files.fullscreen.FullScreenFileChangeActivity
import com.fastaccess.ui.modules.repos.pull_requests.pull_request.details.files.fullscreen.FullScreenFileChangeActivity.Companion.startActivityForResult
import com.fastaccess.ui.modules.reviews.AddReviewDialogFragment.Companion.newInstance
import com.fastaccess.ui.widgets.FontTextView
import com.fastaccess.ui.widgets.StateLayout
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView
import com.fastaccess.ui.widgets.recyclerview.scroll.RecyclerViewFastScroller

/**
 * Created by Kosh on 03 Dec 2016, 3:56 PM
 */
class PullRequestFilesFragment :
    BaseFragment<PullRequestFilesMvp.View, PullRequestFilesPresenter>(),
    PullRequestFilesMvp.View {
    @JvmField
    @BindView(R.id.recycler)
    var recycler: DynamicRecyclerView? = null

    @JvmField
    @BindView(R.id.refresh)
    var refresh: SwipeRefreshLayout? = null

    @JvmField
    @BindView(R.id.stateLayout)
    var stateLayout: StateLayout? = null

    @JvmField
    @BindView(R.id.fastScroller)
    var fastScroller: RecyclerViewFastScroller? = null

    @JvmField
    @State
    var toggleMap: HashMap<Long, Boolean> = LinkedHashMap()

    @JvmField
    @BindView(R.id.changes)
    var changes: FontTextView? = null

    @JvmField
    @BindView(R.id.addition)
    var addition: FontTextView? = null

    @JvmField
    @BindView(R.id.deletion)
    var deletion: FontTextView? = null
    private var viewCallback: PatchCallback? = null
    private var onLoadMore: OnLoadMore<*>? = null
    private var adapter: CommitFilesAdapter? = null
    private var issueCallback: IssuePrCallback<PullRequest>? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        issueCallback = when {
            parentFragment is IssuePrCallback<*> -> {
                parentFragment as IssuePrCallback<PullRequest>
            }
            context is IssuePrCallback<*> -> {
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
        fastScroller!!.attachRecyclerView(recycler)
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

    override val loadMore: OnLoadMore<*>
        get() {
            if (onLoadMore == null) {
                onLoadMore = OnLoadMore(presenter)
            }
            return onLoadMore!!
        }

    override fun onOpenForResult(position: Int, linesModel: CommitFileChanges) {
        startActivityForResult(this, linesModel, position, false)
    }

    override fun onRefresh() {
        presenter!!.onCallApi(1, null)
    }

    override fun onClick(view: View) {
        onRefresh()
    }

    override fun onToggle(position: Long, isCollapsed: Boolean) {
        val model = adapter!!.getItem(position.toInt()) ?: return
        if (model.commitFileModel!!.patch == null) {
            if ("renamed".equals(model.commitFileModel!!.status, ignoreCase = true)) {
                launchUri(requireContext(), model.commitFileModel!!.blobUrl!!)
                return
            }
            ActivityHelper.startCustomTab(
                requireActivity(),
                adapter!!.getItem(position.toInt()).commitFileModel!!.blobUrl!!
            )
        }
        toggleMap[position] = isCollapsed
    }

    override fun isCollapsed(position: Long): Boolean {
        val toggle = toggleMap[position]
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
            val commitFileChanges = adapter!!.getItem(groupPosition)
            val models: MutableList<CommitLinesModel> = commitFileChanges.linesModel?.toMutableList()?: mutableListOf()
            if (models.isNotEmpty()) {
                val current = models[childPosition]
                current.isHasCommentedOn = true
                models[childPosition] = current
                adapter!!.notifyItemChanged(groupPosition)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == FullScreenFileChangeActivity.FOR_RESULT_CODE && data != null) {
                val comments: List<CommentRequestModel> =
                    data.getParcelableArrayListExtra(BundleConstant.ITEM)?: listOf()
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
        super.onActivityResult(requestCode, resultCode, data)
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