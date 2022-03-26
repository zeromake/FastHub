package com.fastaccess.ui.modules.repos.pull_requests.pull_request.details.timeline.timeline

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.evernote.android.state.State
import com.fastaccess.R
import com.fastaccess.data.dao.EditReviewCommentModel
import com.fastaccess.data.dao.ReviewCommentModel
import com.fastaccess.data.dao.TimelineModel
import com.fastaccess.data.dao.TimelineModel.Companion.constructComment
import com.fastaccess.data.dao.model.Comment
import com.fastaccess.data.dao.model.PullRequest
import com.fastaccess.data.dao.model.User
import com.fastaccess.data.dao.types.ReactionTypes
import com.fastaccess.helper.ActivityHelper.startLauncher
import com.fastaccess.helper.BundleConstant
import com.fastaccess.helper.Bundler.Companion.start
import com.fastaccess.provider.rest.loadmore.OnLoadMore
import com.fastaccess.provider.timeline.CommentsHelper.getUsersByTimeline
import com.fastaccess.ui.adapter.IssuesTimelineAdapter
import com.fastaccess.ui.adapter.viewholder.TimelineCommentsViewHolder
import com.fastaccess.ui.base.BaseFragment
import com.fastaccess.ui.delegate.viewFind
import com.fastaccess.ui.modules.editor.EditorActivity
import com.fastaccess.ui.modules.editor.comment.CommentEditorFragment.CommentListener
import com.fastaccess.ui.modules.repos.issues.create.CreateIssueActivity
import com.fastaccess.ui.modules.repos.issues.issue.details.IssuePagerMvp.IssuePrCallback
import com.fastaccess.ui.modules.repos.reactions.ReactionsDialogFragment.Companion.newInstance
import com.fastaccess.ui.widgets.StateLayout
import com.fastaccess.ui.widgets.dialog.MessageDialogView
import com.fastaccess.ui.widgets.dialog.MessageDialogView.Companion.newInstance
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView
import com.fastaccess.ui.widgets.recyclerview.scroll.RecyclerViewFastScroller

/**
 * Created by Kosh on 31 Mar 2017, 7:35 PM
 */
class PullRequestTimelineFragment :
    BaseFragment<PullRequestTimelineMvp.View, PullRequestTimelinePresenter>(),
    PullRequestTimelineMvp.View {
    val recycler: DynamicRecyclerView? by viewFind(R.id.recycler)
    val refresh: SwipeRefreshLayout? by viewFind(R.id.refresh)
    val stateLayout: StateLayout? by viewFind(R.id.stateLayout)
    val fastScroller: RecyclerViewFastScroller? by viewFind(R.id.fastScroller)

    @State
    var toggleMap: HashMap<Long, Boolean?> = LinkedHashMap()
    private var adapter: IssuesTimelineAdapter? = null
    private var onLoadMore: OnLoadMore<PullRequest>? = null
    private var commentsCallback: CommentListener? = null
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
        commentsCallback = when {
            parentFragment is CommentListener -> {
                parentFragment as CommentListener?
            }
            context is CommentListener -> {
                context
            }
            else -> {
                throw IllegalArgumentException(
                    String.format(
                        "%s or parent fragment must implement CommentEditorFragment.CommentListener",
                        context.javaClass.simpleName
                    )
                )
            }
        }
    }

    override fun onDetach() {
        issueCallback = null
        commentsCallback = null
        super.onDetach()
    }

    override fun onRefresh() {
        presenter!!.onCallApi(1, pullRequest)
    }

    override fun fragmentLayout(): Int {
        return R.layout.micro_grid_refresh_list
    }

    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        if (pullRequest == null) {
            throw NullPointerException("PullRequest went missing!!!")
        }
        val isMerged = presenter!!.isMerged(pullRequest)
        adapter = IssuesTimelineAdapter(
            presenter!!.events, this, true,
            this, isMerged, presenter, pullRequest!!.login, pullRequest!!.user.login
        )
        stateLayout!!.setEmptyText(R.string.no_events)
        recycler!!.setEmptyView(stateLayout!!, refresh)
        refresh!!.setOnRefreshListener(this)
        stateLayout!!.setOnReloadListener(this)
        adapter!!.listener = presenter
        recycler!!.adapter = adapter
        fastScroller!!.visibility = View.VISIBLE
        fastScroller!!.attachRecyclerView(recycler!!)
        recycler!!.addDivider(TimelineCommentsViewHolder::class.java)
        loadMore.initialize(presenter!!.currentPage, presenter!!.previousTotal)
        recycler!!.addOnScrollListener(loadMore)
        if (savedInstanceState == null) {
            onSetHeader(TimelineModel(pullRequest))
            onRefresh()
        } else if (presenter!!.events.isEmpty() || presenter!!.events.size == 1) {
            onRefresh()
        }
    }

    override fun providePresenter(): PullRequestTimelinePresenter {
        return PullRequestTimelinePresenter()
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

    override fun onClick(view: View) {
        onRefresh()
    }

    override fun onToggle(id: Long, isCollapsed: Boolean): Boolean {
        toggleMap[id] = isCollapsed
        return true
    }

    override fun isCollapsed(id: Long): Boolean {
        return toggleMap[id] != null && toggleMap[id]!!
    }

    override fun onNotifyAdapter(items: List<TimelineModel>?, page: Int) {
        hideProgress()
        if (items == null) {
            adapter!!.subList(1, adapter!!.itemCount)
            return
        }
        if (page == 1) {
            adapter!!.subList(1, adapter!!.itemCount)
        }
        adapter!!.addItems(items)
    }

    override val loadMore: OnLoadMore<PullRequest>
        get() {
            if (onLoadMore == null) {
                onLoadMore = OnLoadMore(presenter)
            }
            onLoadMore!!.parameter = pullRequest
            return onLoadMore!!
        }

    override fun onEditComment(item: Comment) {
        val intent = Intent(context, EditorActivity::class.java)
        if (pullRequest == null) return
        intent.putExtras(
            start()
                .put(BundleConstant.ID, pullRequest!!.repoId)
                .put(BundleConstant.EXTRA_TWO, pullRequest!!.login)
                .put(BundleConstant.EXTRA_THREE, pullRequest!!.number)
                .put(BundleConstant.EXTRA_FOUR, item.id)
                .put(BundleConstant.EXTRA, item.body)
                .put(BundleConstant.EXTRA_TYPE, BundleConstant.ExtraType.EDIT_ISSUE_COMMENT_EXTRA)
                .putStringArrayList(
                    "participants",
                    getUsersByTimeline(adapter!!.data.filterNotNull())
                )
                .put(BundleConstant.IS_ENTERPRISE, isEnterprise)
                .end()
        )
        val view = fromView
        startLauncher(launcher, intent, view)
    }

    override fun onEditReviewComment(
        item: ReviewCommentModel,
        groupPosition: Int,
        childPosition: Int
    ) {
        val model = EditReviewCommentModel()
        model.commentPosition = childPosition
        model.groupPosition = groupPosition
        model.inReplyTo = item.id
        val intent = Intent(context, EditorActivity::class.java)
        if (pullRequest == null) return
        intent.putExtras(
            start()
                .put(BundleConstant.ID, pullRequest!!.repoId)
                .put(BundleConstant.EXTRA_TWO, pullRequest!!.login)
                .put(BundleConstant.EXTRA_THREE, pullRequest!!.number)
                .put(BundleConstant.EXTRA_FOUR, item.id)
                .put(BundleConstant.EXTRA, item.body)
                .put(BundleConstant.REVIEW_EXTRA, model)
                .put(BundleConstant.EXTRA_TYPE, BundleConstant.ExtraType.EDIT_REVIEW_COMMENT_EXTRA)
                .putStringArrayList(
                    "participants",
                    getUsersByTimeline(adapter!!.data.filterNotNull())
                )
                .put(BundleConstant.IS_ENTERPRISE, isEnterprise)
                .end()
        )
        val view = fromView
        startLauncher(reviewLauncher, intent, view)
    }

    override fun onRemove(timelineModel: TimelineModel) {
        hideProgress()
        adapter!!.removeItem(timelineModel)
    }

    override fun onShowDeleteMsg(id: Long) {
        newInstance(
            getString(R.string.delete), getString(R.string.confirm_message),
            start()
                .put(BundleConstant.EXTRA, id)
                .put(BundleConstant.YES_NO_EXTRA, false)
                .end()
        )
            .show(childFragmentManager, MessageDialogView.TAG)
    }

    override fun onReply(user: User?, message: String?) {
        val intent = Intent(context, EditorActivity::class.java)
        if (pullRequest == null) return
        intent.putExtras(
            start()
                .put(BundleConstant.ID, pullRequest!!.repoId)
                .put(BundleConstant.EXTRA_TWO, pullRequest!!.login)
                .put(BundleConstant.EXTRA_THREE, pullRequest!!.number)
                .put(BundleConstant.EXTRA, "@" + user!!.login)
                .put(BundleConstant.EXTRA_TYPE, BundleConstant.ExtraType.NEW_ISSUE_COMMENT_EXTRA)
                .putStringArrayList(
                    "participants",
                    getUsersByTimeline(adapter!!.data.filterNotNull())
                )
                .put(BundleConstant.IS_ENTERPRISE, isEnterprise)
                .put("message", message)
                .end()
        )
        val view = fromView
        startLauncher(launcher, intent, view)
    }

    override fun onHandleComment(text: String, bundle: Bundle?) {
        presenter!!.onHandleComment(text, bundle)
    }

    override fun onReplyOrCreateReview(
        user: User?, message: String?,
        groupPosition: Int, childPosition: Int,
        model: EditReviewCommentModel
    ) {
        val intent = Intent(context, EditorActivity::class.java)
        if (pullRequest == null) return
        intent.putExtras(
            start()
                .put(BundleConstant.ID, pullRequest!!.repoId)
                .put(BundleConstant.EXTRA_TWO, pullRequest!!.login)
                .put(BundleConstant.EXTRA_THREE, pullRequest!!.number)
                .put(BundleConstant.EXTRA, if (user != null) "@" + user.login else "")
                .put(BundleConstant.REVIEW_EXTRA, model)
                .put(BundleConstant.EXTRA_TYPE, BundleConstant.ExtraType.NEW_REVIEW_COMMENT_EXTRA)
                .putStringArrayList(
                    "participants",
                    getUsersByTimeline(adapter!!.data.filterNotNull())
                )
                .put(BundleConstant.IS_ENTERPRISE, isEnterprise)
                .put("message", message)
                .end()
        )
        val view = fromView
        startLauncher(reviewLauncher, intent, view)
    }

    override fun addComment(timelineModel: TimelineModel) {
        onHideBlockingProgress()
        adapter!!.addItem(timelineModel)
        if (commentsCallback != null) commentsCallback!!.onClearEditText()
    }

    override val namesToTag: ArrayList<String>
        get() = getUsersByTimeline(adapter!!.data.filterNotNull())

    override fun onHideBlockingProgress() {
        hideProgress()
        super.hideProgress()
    }

    override fun onEditHeader(pr: PullRequest) {
        CreateIssueActivity.startForResult(
            requireActivity(),
            launcher,
            pr.login,
            pr.repoId,
            pr,
            isEnterprise
        )
    }

    override fun showReactionsPopup(
        type: ReactionTypes, login: String, repoId: String,
        idOrNumber: Long, reactionType: Int
    ) {
        newInstance(login, repoId, type, idOrNumber, reactionType).show(
            childFragmentManager, "ReactionsDialogFragment"
        )
    }

    override fun onShowReviewDeleteMsg(commentId: Long, groupPosition: Int, commentPosition: Int) {
        newInstance(
            getString(R.string.delete), getString(R.string.confirm_message),
            start()
                .put(BundleConstant.EXTRA, commentId)
                .put(BundleConstant.YES_NO_EXTRA, true)
                .put(BundleConstant.EXTRA_TWO, groupPosition)
                .put(BundleConstant.EXTRA_THREE, commentPosition)
                .end()
        )
            .show(childFragmentManager, MessageDialogView.TAG)
    }

    override fun onRemoveReviewComment(groupPosition: Int, commentPosition: Int) {
        hideProgress()
        val timelineModel = adapter!!.getItem(groupPosition)
        if (timelineModel?.groupedReviewModel != null) {
            if (timelineModel.groupedReviewModel!!.comments != null) {
                timelineModel.groupedReviewModel!!.comments!!.removeAt(commentPosition)
                if (timelineModel.groupedReviewModel!!.comments!!.isEmpty()) {
                    adapter!!.removeItem(groupPosition)
                } else {
                    adapter!!.notifyItemChanged(groupPosition)
                }
            }
        }
    }

    override fun onSetHeader(timelineModel: TimelineModel) {
        if (adapter != null) {
            if (adapter!!.isEmpty) {
                adapter!!.addItem(timelineModel, 0)
            } else {
                adapter!!.swapItem(timelineModel, 0)
            }
        }
    }

    override val pullRequest: PullRequest?
        get() = issueCallback!!.data

    override fun onUpdateHeader() {
        if (pullRequest == null) return
        onSetHeader(TimelineModel(pullRequest))
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        val data = it.data
        if (it.resultCode == Activity.RESULT_OK) {
            if (data == null) {
                onRefresh()
                return@registerForActivityResult
            }
            data.extras?.let { bundle ->
                val isNew = bundle.getBoolean(BundleConstant.EXTRA)
                val commentsModel: Comment? = bundle.getParcelable(BundleConstant.ITEM)
                if (commentsModel == null) {
                    onRefresh() // bundle size is too large? refresh the api
                    return@let
                }
                if (isNew) {
                    adapter!!.addItem(constructComment(commentsModel))
                    recycler!!.smoothScrollToPosition(adapter!!.itemCount)
                } else {
                    val position = adapter!!.getItem(constructComment(commentsModel))
                    if (position != -1) {
                        adapter!!.swapItem(constructComment(commentsModel), position)
                        recycler!!.smoothScrollToPosition(position)
                    } else {
                        adapter!!.addItem(constructComment(commentsModel))
                        recycler!!.smoothScrollToPosition(adapter!!.itemCount)
                    }
                }
            }
        }
    }

    private val reviewLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        val data = it.data
        if (it.resultCode == Activity.RESULT_OK) {
            if (data == null) {
                onRefresh()
                return@registerForActivityResult
            }
            data.extras?.let { bundle ->
                val isNew = bundle.getBoolean(BundleConstant.EXTRA)
                val commentModel: EditReviewCommentModel? =
                    bundle.getParcelable(BundleConstant.ITEM)
                if (commentModel == null) {
                    onRefresh() // bundle size is too large? refresh the api
                    return@let
                }
                val timelineModel = adapter!!.getItem(commentModel.groupPosition)
                if (isNew) {
                    if (timelineModel!!.groupedReviewModel != null && timelineModel.groupedReviewModel!!.comments != null) {
                        timelineModel.groupedReviewModel!!.comments!!.add(commentModel.commentModel!!)
                        adapter!!.notifyItemChanged(commentModel.groupPosition)
                    } else {
                        onRefresh()
                    }
                } else {
                    if (timelineModel!!.groupedReviewModel != null && timelineModel.groupedReviewModel!!.comments != null) {
                        timelineModel.groupedReviewModel!!.comments!![commentModel.commentPosition] =
                            commentModel.commentModel!!
                        adapter!!.notifyItemChanged(commentModel.groupPosition)
                    } else {
                        onRefresh()
                    }
                }
            }
        }
    }

    override fun onMessageDialogActionClicked(isOk: Boolean, bundle: Bundle?) {
        super.onMessageDialogActionClicked(isOk, bundle)
        if (isOk) {
            presenter!!.onHandleDeletion(bundle)
        }
    }

    override fun isPreviouslyReacted(id: Long, vId: Int): Boolean {
        return presenter!!.isPreviouslyReacted(id, vId)
    }

    override fun isCallingApi(id: Long, vId: Int): Boolean {
        return presenter!!.isCallingApi(id, vId)
    }

    override fun onScrollTop(index: Int) {
        super.onScrollTop(index)
        if (recycler != null) recycler!!.scrollToPosition(0)
    }

    override fun showReload() {
        hideProgress()
        stateLayout!!.showReload(adapter!!.itemCount)
    }

    private val fromView: View
        get() = if (activity != null
            && requireActivity().findViewById<View?>(R.id.fab) != null
        ) requireActivity().findViewById(
            R.id.fab
        ) else recycler!!

    companion object {
        fun newInstance(): PullRequestTimelineFragment {
            return PullRequestTimelineFragment()
        }
    }
}