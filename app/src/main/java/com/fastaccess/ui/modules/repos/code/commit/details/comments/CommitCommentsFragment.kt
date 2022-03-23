package com.fastaccess.ui.modules.repos.code.commit.details.comments

import android.annotation.SuppressLint
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
import com.fastaccess.data.dao.TimelineModel
import com.fastaccess.data.dao.TimelineModel.Companion.constructComment
import com.fastaccess.data.dao.model.Comment
import com.fastaccess.data.dao.model.User
import com.fastaccess.data.dao.types.ReactionTypes
import com.fastaccess.helper.ActivityHelper.startLauncher
import com.fastaccess.helper.BundleConstant
import com.fastaccess.helper.Bundler.Companion.start
import com.fastaccess.provider.rest.loadmore.OnLoadMore
import com.fastaccess.provider.timeline.CommentsHelper.getUsersByTimeline
import com.fastaccess.provider.timeline.ReactionsProvider
import com.fastaccess.ui.adapter.IssuesTimelineAdapter
import com.fastaccess.ui.base.BaseFragment
import com.fastaccess.ui.delegate.viewFind
import com.fastaccess.ui.modules.editor.EditorActivity
import com.fastaccess.ui.modules.editor.comment.CommentEditorFragment.CommentListener
import com.fastaccess.ui.modules.repos.reactions.ReactionsDialogFragment
import com.fastaccess.ui.widgets.StateLayout
import com.fastaccess.ui.widgets.dialog.MessageDialogView
import com.fastaccess.ui.widgets.dialog.MessageDialogView.Companion.newInstance
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView
import com.fastaccess.ui.widgets.recyclerview.scroll.RecyclerViewFastScroller

/**
 * Created by Kosh on 11 Nov 2016, 12:36 PM
 */
class CommitCommentsFragment : BaseFragment<CommitCommentsMvp.View, CommitCommentsPresenter>(),
    CommitCommentsMvp.View {
    val recycler: DynamicRecyclerView? by viewFind(R.id.recycler)
    val refresh: SwipeRefreshLayout? by viewFind(R.id.refresh)
    val stateLayout: StateLayout? by viewFind(R.id.stateLayout)
    val fastScroller: RecyclerViewFastScroller? by viewFind(R.id.fastScroller)

    @State
    var toggleMap: HashMap<Long, Boolean> = LinkedHashMap()
    private var commentsCallback: CommentListener? = null
    private var adapter: IssuesTimelineAdapter? = null
    private var onLoadMore: OnLoadMore<String>? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
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
        commentsCallback = null
        super.onDetach()
    }

    override fun fragmentLayout(): Int {
        return R.layout.fab_micro_grid_refresh_list
    }

    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        if (savedInstanceState == null) presenter!!.onFragmentCreated(arguments)
        stateLayout!!.setEmptyText(R.string.no_comments)
        recycler!!.setEmptyView(stateLayout!!, refresh)
        recycler!!.setItemViewCacheSize(30)
        refresh!!.setOnRefreshListener(this)
        stateLayout!!.setOnReloadListener(this)
        adapter = IssuesTimelineAdapter(
            presenter!!.comments, this, true,
            this, requireArguments().getString(BundleConstant.EXTRA), null
        )
        adapter!!.listener = presenter
        loadMore.initialize(presenter!!.currentPage, presenter!!.previousTotal)
        recycler!!.adapter = adapter
        recycler!!.addOnScrollListener(loadMore)
        recycler!!.addNormalSpacingDivider()
        if (presenter!!.comments.isEmpty() && !presenter!!.isApiCalled) {
            onRefresh()
        }
        fastScroller!!.attachRecyclerView(recycler!!)
    }

    override fun onRefresh() {
        presenter!!.onCallApi(1, null)
    }

    override fun onNotifyAdapter(items: List<TimelineModel>?, page: Int) {
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
    }

    override fun onRemove(comment: TimelineModel) {
        hideProgress()
        adapter!!.removeItem(comment)
    }

    override fun hideProgress() {
        super.hideProgress()
        refresh!!.isRefreshing = false
        stateLayout!!.hideProgress()
    }

    override fun showProgress(@StringRes resId: Int) {
        refresh!!.isRefreshing = true
        stateLayout!!.showProgress()
    }

    override fun showErrorMessage(msgRes: String) {
        showReload()
        super.showErrorMessage(msgRes)
    }

    override fun showMessage(titleRes: Int, msgRes: Int) {
        showReload()
        super.showMessage(titleRes, msgRes)
    }

    override fun providePresenter(): CommitCommentsPresenter {
        return CommitCommentsPresenter()
    }

    override val loadMore: OnLoadMore<String>
        get() {
            if (onLoadMore == null) {
                onLoadMore = OnLoadMore(presenter)
            }
            return onLoadMore!!
        }

    override fun onEditComment(item: Comment) {
        val intent = Intent(context, EditorActivity::class.java)
        intent.putExtras(
            start()
                .put(BundleConstant.ID, presenter!!.repoId())
                .put(BundleConstant.EXTRA_TWO, presenter!!.login())
                .put(BundleConstant.EXTRA_THREE, presenter!!.sha())
                .put(BundleConstant.EXTRA_FOUR, item.id)
                .put(BundleConstant.EXTRA, item.body)
                .put(BundleConstant.EXTRA_TYPE, BundleConstant.ExtraType.EDIT_COMMIT_COMMENT_EXTRA)
                .putStringArrayList("participants", getUsersByTimeline(adapter!!.data.filterNotNull()))
                .put(BundleConstant.IS_ENTERPRISE, isEnterprise)
                .end()
        )
        val view =
            if (activity != null && requireActivity().findViewById<View?>(R.id.fab) != null) requireActivity().findViewById<View>(
                R.id.fab
            ) else recycler!!
        startLauncher(launcher, intent, view)
    }

    override fun onShowDeleteMsg(id: Long) {
        newInstance(
            getString(R.string.delete), getString(R.string.confirm_message),
            start()
                .put(BundleConstant.EXTRA, id)
                .put(BundleConstant.YES_NO_EXTRA, true)
                .end()
        )
            .show(childFragmentManager, MessageDialogView.TAG)
    }

    override fun onTagUser(user: User?) {
        val intent = Intent(context, EditorActivity::class.java)
        intent.putExtras(
            start()
                .put(BundleConstant.ID, presenter!!.repoId())
                .put(BundleConstant.EXTRA_TWO, presenter!!.login())
                .put(BundleConstant.EXTRA_THREE, presenter!!.sha())
                .put(BundleConstant.EXTRA, if (user != null) "@" + user.login else "")
                .put(BundleConstant.EXTRA_TYPE, BundleConstant.ExtraType.NEW_COMMIT_COMMENT_EXTRA)
                .putStringArrayList("participants", getUsersByTimeline(adapter!!.data.filterNotNull()))
                .put(BundleConstant.IS_ENTERPRISE, isEnterprise)
                .end()
        )
        val view =
            if (activity != null && requireActivity().findViewById<View?>(R.id.fab) != null) requireActivity().findViewById<View>(
                R.id.fab
            ) else recycler!!
        startLauncher(launcher, intent, view)
    }

    override fun onReply(user: User?, message: String?) {
        onTagUser(user)
    }

    override fun showReactionsPopup(
        reactionTypes: ReactionTypes,
        login: String,
        repoId: String,
        commentId: Long
    ) {
        ReactionsDialogFragment.newInstance(
            login,
            repoId,
            reactionTypes,
            commentId,
            ReactionsProvider.COMMIT
        )
            .show(childFragmentManager, "ReactionsDialogFragment")
    }

    override fun addComment(newComment: Comment) {
        hideBlockingProgress()
        if (adapter != null) {
            adapter!!.addItem(constructComment(newComment))
        }
        if (commentsCallback != null) commentsCallback!!.onClearEditText()
    }

    override fun onDestroyView() {
        recycler!!.removeOnScrollListener(loadMore)
        super.onDestroyView()
    }

    override fun onClick(view: View) {
        onRefresh()
    }

    @SuppressLint("NotifyDataSetChanged")
    private val launcher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) {
            val data = it.data
            if (data == null) {
                onRefresh()
                return@registerForActivityResult
            }
            val bundle = data.extras
            if (bundle != null) {
                val isNew = bundle.getBoolean(BundleConstant.EXTRA)
                val commentsModel: Comment? = bundle.getParcelable(BundleConstant.ITEM)
                if (commentsModel == null) {
                    onRefresh() // shit happens, refresh()?
                    return@registerForActivityResult
                }
                adapter!!.notifyDataSetChanged()
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
            } else {
                onRefresh() // bundle size is too large? refresh the api
            }
        }
    }

    override fun onMessageDialogActionClicked(isOk: Boolean, bundle: Bundle?) {
        super.onMessageDialogActionClicked(isOk, bundle)
        if (isOk) {
            presenter!!.onHandleDeletion(bundle)
        }
    }

    override fun onToggle(id: Long, isCollapsed: Boolean): Boolean {
        toggleMap[id] = isCollapsed
        return true
    }

    override fun isCollapsed(id: Long): Boolean {
        val toggle = toggleMap[id]
        return toggle != null && toggle
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

    override fun onHandleComment(text: String, bundle: Bundle?) {
        presenter!!.onHandleComment(text, bundle)
    }

    override val namesToTags: ArrayList<String>
        get() = getUsersByTimeline(adapter!!.data.filterNotNull())

    override fun hideBlockingProgress() {
        hideProgress()
        super.hideProgress()
    }

    companion object {
        fun newInstance(login: String, repoId: String, sha: String): CommitCommentsFragment {
            val view = CommitCommentsFragment()
            view.arguments = start()
                .put(BundleConstant.ID, repoId)
                .put(BundleConstant.EXTRA, login)
                .put(BundleConstant.EXTRA_TWO, sha)
                .end()
            return view
        }
    }
}