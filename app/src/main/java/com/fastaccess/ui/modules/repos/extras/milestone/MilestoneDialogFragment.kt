package com.fastaccess.ui.modules.repos.extras.milestone

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import androidx.appcompat.widget.Toolbar
import butterknife.BindView
import com.fastaccess.R
import com.fastaccess.data.dao.MilestoneModel
import com.fastaccess.helper.BundleConstant
import com.fastaccess.helper.Bundler.Companion.start
import com.fastaccess.ui.adapter.MilestonesAdapter
import com.fastaccess.ui.base.BaseDialogFragment
import com.fastaccess.ui.base.BaseFragment
import com.fastaccess.ui.modules.repos.extras.milestone.MilestoneMvp.OnMilestoneSelected
import com.fastaccess.ui.modules.repos.extras.milestone.create.CreateMilestoneDialogFragment
import com.fastaccess.ui.widgets.AppbarRefreshLayout
import com.fastaccess.ui.widgets.StateLayout
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView
import com.fastaccess.ui.widgets.recyclerview.scroll.RecyclerViewFastScroller
import com.google.android.material.appbar.AppBarLayout

/**
 * Created by Kosh on 04 Mar 2017, 9:45 PM
 */
class MilestoneDialogFragment : BaseFragment<MilestoneMvp.View, MilestonePresenter>(),
    MilestoneMvp.View {
    @JvmField
    @BindView(R.id.toolbar)
    var toolbar: Toolbar? = null

    @JvmField
    @BindView(R.id.appbar)
    var appbar: AppBarLayout? = null

    @JvmField
    @BindView(R.id.recycler)
    var recycler: DynamicRecyclerView? = null

    @JvmField
    @BindView(R.id.refresh)
    var refresh: AppbarRefreshLayout? = null

    @JvmField
    @BindView(R.id.stateLayout)
    var stateLayout: StateLayout? = null

    @JvmField
    @BindView(R.id.fastScroller)
    var fastScroller: RecyclerViewFastScroller? = null
    private var adapter: MilestonesAdapter? = null
    private var onMilestoneSelected: OnMilestoneSelected? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (parentFragment != null && parentFragment is OnMilestoneSelected) {
            onMilestoneSelected = parentFragment as OnMilestoneSelected?
        } else if (context is OnMilestoneSelected) {
            onMilestoneSelected = context
        }
    }

    override fun onDetach() {
        onMilestoneSelected = null
        super.onDetach()
    }

    override fun onNotifyAdapter(items: List<MilestoneModel>?) {
        hideProgress()
        if (items == null || items.isEmpty()) {
            adapter!!.clear()
            return
        }
        adapter!!.insertItems(items)
    }

    override fun onMilestoneSelected(milestoneModel: MilestoneModel) {
        if (onMilestoneSelected != null) onMilestoneSelected!!.onMilestoneSelected(milestoneModel)
        if (parentFragment is BaseDialogFragment<*, *>) {
            (parentFragment as BaseDialogFragment<*, *>?)!!.dismiss()
        }
    }

    override fun fragmentLayout(): Int {
        return R.layout.milestone_dialog_layout
    }

    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        if (arguments == null) {
            return
        }
        val login = requireArguments().getString(BundleConstant.EXTRA)
        val repo = requireArguments().getString(BundleConstant.ID)
        if (login == null || repo == null) {
            return
        }
        stateLayout!!.setEmptyText(R.string.no_milestones)
        toolbar!!.setTitle(R.string.milestone)
        toolbar!!.setOnMenuItemClickListener { onAddMilestone() }
        if (onMilestoneSelected != null) toolbar!!.inflateMenu(R.menu.add_menu)
        toolbar!!.setNavigationIcon(R.drawable.ic_clear)
        toolbar!!.setNavigationOnClickListener {
            if (parentFragment is BaseDialogFragment<*, *>) {
                (parentFragment as BaseDialogFragment<*, *>?)!!.dismiss()
            }
        }
        recycler!!.addDivider()
        adapter = MilestonesAdapter(presenter.milestones)
        if (onMilestoneSelected != null) adapter!!.listener = presenter
        recycler!!.setEmptyView(stateLayout!!, refresh)
        recycler!!.adapter = adapter
        recycler!!.addKeyLineDivider()
        if (savedInstanceState == null || presenter.milestones
                .isEmpty() && !presenter!!.isApiCalled
        ) {
            presenter!!.onLoadMilestones(login, repo)
        }
        stateLayout!!.setOnReloadListener { presenter!!.onLoadMilestones(login, repo) }
        refresh!!.setOnRefreshListener { presenter!!.onLoadMilestones(login, repo) }
        fastScroller!!.attachRecyclerView(recycler!!)
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

    override fun providePresenter(): MilestonePresenter {
        return MilestonePresenter()
    }

    override fun onMilestoneAdded(milestoneModel: MilestoneModel) {
        adapter!!.addItem(milestoneModel, 0)
    }

    private fun showReload() {
        hideProgress()
        stateLayout!!.showReload(adapter!!.itemCount)
    }

    private fun onAddMilestone(): Boolean {
        CreateMilestoneDialogFragment.newInstance(
            requireArguments().getString(BundleConstant.EXTRA)!!,
            requireArguments().getString(BundleConstant.ID)!!
        )
            .show(childFragmentManager, CreateMilestoneDialogFragment.TAG)
        return true
    }

    companion object {
        val TAG: String = MilestoneDialogFragment::class.java.simpleName
        fun newInstance(login: String, repo: String): MilestoneDialogFragment {
            val fragment = MilestoneDialogFragment()
            fragment.arguments = start()
                .put(BundleConstant.EXTRA, login)
                .put(BundleConstant.ID, repo)
                .end()
            return fragment
        }
    }
}