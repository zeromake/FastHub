package com.fastaccess.ui.modules.repos.extras.labels

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.evernote.android.state.State
import com.fastaccess.R
import com.fastaccess.data.dao.LabelListModel
import com.fastaccess.data.dao.LabelModel
import com.fastaccess.helper.BundleConstant
import com.fastaccess.helper.Bundler.Companion.start
import com.fastaccess.helper.InputHelper.isEmpty
import com.fastaccess.provider.rest.loadmore.OnLoadMore
import com.fastaccess.ui.adapter.LabelsAdapter
import com.fastaccess.ui.base.BaseDialogFragment
import com.fastaccess.ui.delegate.viewFind
import com.fastaccess.ui.modules.repos.extras.labels.LabelsMvp.SelectedLabelsListener
import com.fastaccess.ui.modules.repos.extras.labels.create.CreateLabelDialogFragment.Companion.newInstance
import com.fastaccess.ui.widgets.FontTextView
import com.fastaccess.ui.widgets.StateLayout
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView
import com.fastaccess.ui.widgets.recyclerview.scroll.RecyclerViewFastScroller
import com.fastaccess.utils.setOnThrottleClickListener

/**
 * Created by Kosh on 22 Feb 2017, 7:23 PM
 */
class LabelsDialogFragment : BaseDialogFragment<LabelsMvp.View, LabelsPresenter>(),
    LabelsMvp.View {
    val title: FontTextView? by viewFind(R.id.title)
    val add: View? by viewFind(R.id.add)
    val recycler: DynamicRecyclerView? by viewFind(R.id.recycler)
    val refresh: SwipeRefreshLayout? by viewFind(R.id.refresh)
    val stateLayout: StateLayout? by viewFind(R.id.stateLayout)
    val fastScroller: RecyclerViewFastScroller? by viewFind(R.id.fastScroller)

    @State
    var labelModels: ArrayList<LabelModel>? = ArrayList()
    private var onLoadMore: OnLoadMore<String>? = null
    private var adapter: LabelsAdapter? = null
    private var labelsListener: SelectedLabelsListener? = null

    private fun onAddLabel() {
        val repo = requireArguments().getString(BundleConstant.EXTRA_TWO)
        val login = requireArguments().getString(BundleConstant.EXTRA_THREE)
        if (!isEmpty(repo) && !isEmpty(login)) {
            newInstance(login!!, repo!!).show(childFragmentManager, "CreateLabelDialogFragment")
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (parentFragment is SelectedLabelsListener) {
            labelsListener = parentFragment as SelectedLabelsListener?
        } else if (context is SelectedLabelsListener) {
            labelsListener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        labelsListener = null
    }

    override fun fragmentLayout(): Int {
        return R.layout.simple_footer_list_dialog
    }

    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        add!!.setOnThrottleClickListener {
            onAddLabel()
        }
        listOf(
            R.id.cancel, R.id.ok
        ).map { view.findViewById<View>(it) }.setOnThrottleClickListener {
            onClick(it)
        }
        stateLayout!!.setEmptyText(R.string.no_labels)
        recycler!!.setEmptyView(stateLayout!!, refresh)
        refresh!!.setOnRefreshListener { presenter!!.onCallApi(1, null) }
        stateLayout!!.setOnReloadListener { presenter!!.onCallApi(1, null) }
        recycler!!.addDivider()
        title!!.setText(R.string.labels)
        add!!.visibility = View.VISIBLE
        labelModels = requireArguments().getParcelableArrayList(BundleConstant.EXTRA)
        if (labelModels == null) {
            labelModels = ArrayList()
        }
        add!!.visibility = if (labelsListener == null) View.GONE else View.VISIBLE
        adapter = LabelsAdapter(presenter!!.labels, this)
        recycler!!.adapter = adapter
        fastScroller!!.attachRecyclerView(recycler!!)
        recycler!!.addOnScrollListener(loadMore)
        if (presenter!!.labels.isEmpty() && !presenter!!.isApiCalled) {
            presenter!!.onCallApi(1, null)
        }
    }

    override fun providePresenter(): LabelsPresenter {
        val bundle = arguments
        return LabelsPresenter(
            bundle!!.getString(BundleConstant.EXTRA_THREE)!!,
            bundle.getString(BundleConstant.EXTRA_TWO)!!
        )
    }

    override fun isLabelSelected(labelModel: LabelModel): Boolean {
        return labelModels!!.indexOf(labelModel) != -1
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onToggleSelection(labelModel: LabelModel, select: Boolean) {
        if (select) {
            labelModels!!.add(labelModel)
        } else {
            labelModels!!.remove(labelModel)
        }
        adapter!!.notifyDataSetChanged()
    }

    override val loadMore: OnLoadMore<String>
        get() {
            if (onLoadMore == null) {
                onLoadMore = OnLoadMore(presenter)
            }
            return onLoadMore!!
        }

    override fun onNotifyAdapter(items: List<LabelModel>?, page: Int) {
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

    override fun onLabelAdded(labelModel: LabelModel) {
        adapter!!.addItem(labelModel, 0)
        recycler!!.scrollToPosition(0)
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.cancel -> dismiss()
            R.id.ok -> {
                if (labelsListener != null) labelsListener!!.onSelectedLabels(
                    labelModels ?: arrayListOf()
                )
                dismiss()
            }
        }
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

    private fun showReload() {
        hideProgress()
        stateLayout!!.showReload(adapter!!.itemCount)
    }

    companion object {
        fun newInstance(
            selectedLabels: LabelListModel?,
            repo: String,
            login: String
        ): LabelsDialogFragment {
            val fragment = LabelsDialogFragment()
            fragment.arguments = start()
                .putParcelableArrayList(BundleConstant.EXTRA, selectedLabels)
                .put(BundleConstant.EXTRA_TWO, repo)
                .put(BundleConstant.EXTRA_THREE, login)
                .end()
            return fragment
        }
    }
}