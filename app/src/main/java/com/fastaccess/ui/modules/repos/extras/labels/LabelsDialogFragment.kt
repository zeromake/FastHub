package com.fastaccess.ui.modules.repos.extras.labels

import android.annotation.SuppressLint
import android.content.Context
import com.fastaccess.helper.Bundler.Companion.start
import com.fastaccess.helper.InputHelper.isEmpty
import com.fastaccess.ui.modules.repos.extras.labels.create.CreateLabelDialogFragment.Companion.newInstance
import com.fastaccess.ui.base.BaseDialogFragment
import butterknife.BindView
import com.fastaccess.R
import com.fastaccess.ui.widgets.FontTextView
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.fastaccess.ui.widgets.StateLayout
import com.fastaccess.ui.widgets.recyclerview.scroll.RecyclerViewFastScroller
import com.fastaccess.data.dao.LabelModel
import com.fastaccess.provider.rest.loadmore.OnLoadMore
import com.fastaccess.ui.adapter.LabelsAdapter
import com.fastaccess.ui.modules.repos.extras.labels.LabelsMvp.SelectedLabelsListener
import butterknife.OnClick
import com.fastaccess.helper.BundleConstant
import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import com.evernote.android.state.State
import com.fastaccess.data.dao.LabelListModel
import java.util.ArrayList

/**
 * Created by Kosh on 22 Feb 2017, 7:23 PM
 */
class LabelsDialogFragment : BaseDialogFragment<LabelsMvp.View, LabelsPresenter>(),
    LabelsMvp.View {
    @JvmField
    @BindView(R.id.title)
    var title: FontTextView? = null

    @JvmField
    @BindView(R.id.recycler)
    var recycler: DynamicRecyclerView? = null

    @JvmField
    @BindView(R.id.refresh)
    var refresh: SwipeRefreshLayout? = null

    @JvmField
    @BindView(R.id.add)
    var add: View? = null

    @JvmField
    @BindView(R.id.stateLayout)
    var stateLayout: StateLayout? = null

    @JvmField
    @BindView(R.id.fastScroller)
    var fastScroller: RecyclerViewFastScroller? = null

    @JvmField
    @State
    var labelModels: ArrayList<LabelModel>? = ArrayList()
    private var onLoadMore: OnLoadMore<String>? = null
    private var adapter: LabelsAdapter? = null
    private var labelsListener: SelectedLabelsListener? = null

    @OnClick(R.id.add)
    fun onAddLabel() {
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

    @OnClick(R.id.cancel, R.id.ok)
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