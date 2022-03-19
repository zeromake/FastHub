package com.fastaccess.ui.modules.repos.extras.assignees

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import butterknife.BindView
import butterknife.OnClick
import com.evernote.android.state.State
import com.fastaccess.R
import com.fastaccess.data.dao.model.User
import com.fastaccess.helper.BundleConstant
import com.fastaccess.helper.Bundler.Companion.start
import com.fastaccess.ui.adapter.AssigneesAdapter
import com.fastaccess.ui.base.BaseDialogFragment
import com.fastaccess.ui.modules.repos.extras.assignees.AssigneesMvp.SelectedAssigneesListener
import com.fastaccess.ui.widgets.FontTextView
import com.fastaccess.ui.widgets.StateLayout
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView
import com.fastaccess.ui.widgets.recyclerview.scroll.RecyclerViewFastScroller

/**
 * Created by Kosh on 22 Feb 2017, 7:23 PM
 */
class AssigneesDialogFragment : BaseDialogFragment<AssigneesMvp.View, AssigneesPresenter>(),
    AssigneesMvp.View {
    @JvmField
    @BindView(R.id.title)
    var title: FontTextView? = null

    @JvmField
    @BindView(R.id.recycler)
    var recycler: DynamicRecyclerView? = null

    @JvmField
    @BindView(R.id.stateLayout)
    var stateLayout: StateLayout? = null

    @JvmField
    @BindView(R.id.refresh)
    var refresh: SwipeRefreshLayout? = null

    @JvmField
    @BindView(R.id.fastScroller)
    var fastScroller: RecyclerViewFastScroller? = null

    @JvmField
    @State
    var selectionMap: HashMap<Int, User?>? = null
    private var adapter: AssigneesAdapter? = null
    private var assigneesListener: SelectedAssigneesListener? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        assigneesListener = when {
            parentFragment is SelectedAssigneesListener -> {
                parentFragment as SelectedAssigneesListener?
            }
            context is SelectedAssigneesListener -> {
                context
            }
            else -> {
                throw IllegalArgumentException("Parent Fragment or Activity must implement AssigneesMvp.SelectedAssigneesListener")
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        assigneesListener = null
    }

    override fun fragmentLayout(): Int {
        return R.layout.simple_footer_list_dialog
    }

    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            callApi()
        }
        refresh!!.setOnRefreshListener { callApi() }
        stateLayout!!.setOnReloadListener { callApi() }
        val isAssinees = requireArguments().getBoolean(BundleConstant.EXTRA_TWO)
        stateLayout!!.setEmptyText(if (isAssinees) R.string.no_assignees else R.string.no_reviewers)
        recycler!!.setEmptyView(stateLayout!!, refresh)
        recycler!!.addKeyLineDivider()
        title!!.setText(if (isAssinees) R.string.assignees else R.string.reviewers)
        adapter = AssigneesAdapter(presenter!!.list, this)
        recycler!!.adapter = adapter
        fastScroller!!.attachRecyclerView(recycler!!)
    }

    override fun providePresenter(): AssigneesPresenter {
        return AssigneesPresenter()
    }

    override fun isAssigneeSelected(position: Int): Boolean {
        return getSelectionMap()[position] != null
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onToggleSelection(position: Int, select: Boolean) {
        if (select) {
            getSelectionMap()[position] = adapter!!.getItem(position)
        } else {
            getSelectionMap().remove(position)
        }
        adapter!!.notifyDataSetChanged()
    }

    @OnClick(R.id.cancel, R.id.ok)
    fun onClick(view: View) {
        when (view.id) {
            R.id.cancel -> dismiss()
            R.id.ok -> {
                val labels = (selectionMap ?: mapOf())
                    .filter { (_, value1) -> value1 != null }
                    .map { it.value!! }
                assigneesListener!!.onSelectedAssignees(
                    ArrayList(labels),
                    requireArguments().getBoolean(BundleConstant.EXTRA_TWO)
                )
                dismiss()
            }
        }
    }

    override fun onNotifyAdapter(items: List<User>?) {
        hideProgress()
        if (items == null || items.isEmpty()) {
            adapter!!.clear()
            return
        }
        adapter!!.insertItems(items)
    }

    override fun showProgress(@StringRes resId: Int) {
        stateLayout!!.showProgress()
        refresh!!.isRefreshing = true
    }

    override fun hideProgress() {
        stateLayout!!.hideProgress()
        refresh!!.isRefreshing = false
    }

    override fun showErrorMessage(msgRes: String) {
        showReload()
        super.showErrorMessage(msgRes)
    }

    override fun showMessage(titleRes: Int, msgRes: Int) {
        showReload()
        super.showMessage(titleRes, msgRes)
    }

    private fun showReload() {
        hideProgress()
        stateLayout!!.showReload(adapter!!.itemCount)
    }

    private fun getSelectionMap(): HashMap<Int, User?> {
        if (selectionMap == null) {
            selectionMap = linkedMapOf()
        }
        return selectionMap!!
    }

    private fun callApi() {
        presenter!!.onCallApi(
            requireArguments().getString(BundleConstant.EXTRA)!!,
            requireArguments().getString(BundleConstant.ID)!!,
            requireArguments().getBoolean(BundleConstant.EXTRA_TWO)
        )
    }

    companion object {
        fun newInstance(
            login: String,
            repoId: String,
            isAssignees: Boolean
        ): AssigneesDialogFragment {
            val fragment = AssigneesDialogFragment()
            fragment.arguments = start()
                .put(BundleConstant.ID, repoId)
                .put(BundleConstant.EXTRA, login)
                .put(BundleConstant.EXTRA_TWO, isAssignees)
                .end()
            return fragment
        }
    }
}