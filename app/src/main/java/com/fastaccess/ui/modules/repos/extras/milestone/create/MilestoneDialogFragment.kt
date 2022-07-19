package com.fastaccess.ui.modules.repos.extras.milestone.create

import android.content.Context
import android.os.Bundle
import android.view.View
import com.fastaccess.R
import com.fastaccess.data.dao.MilestoneModel
import com.fastaccess.helper.BundleConstant
import com.fastaccess.helper.Bundler.Companion.start
import com.fastaccess.ui.base.BaseDialogFragment
import com.fastaccess.ui.base.mvp.presenter.BasePresenter
import com.fastaccess.ui.modules.repos.extras.milestone.MilestoneMvp.OnMilestoneSelected
import com.fastaccess.ui.modules.repos.issues.issue.details.IssuePagerMvp
import com.fastaccess.ui.modules.repos.pull_requests.pull_request.details.PullRequestPagerMvp

/**
 * Created by Kosh on 04 Mar 2017, 10:58 PM
 */
class MilestoneDialogFragment : BaseDialogFragment<IssuePagerMvp.View, BasePresenter<IssuePagerMvp.View>>(), OnMilestoneSelected {
    private var issueCallback: IssuePagerMvp.View? = null
    private var pullRequestCallback: PullRequestPagerMvp.View? = null
    private var milestoneCallback: OnMilestoneSelected? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is IssuePagerMvp.View) {
            issueCallback = context
        } else if (parentFragment is IssuePagerMvp.View) {
            issueCallback = parentFragment as IssuePagerMvp.View?
        }
        if (context is PullRequestPagerMvp.View) {
            pullRequestCallback = context
        } else if (parentFragment is PullRequestPagerMvp.View) {
            pullRequestCallback = parentFragment as PullRequestPagerMvp.View?
        }
        if (context is OnMilestoneSelected) {
            milestoneCallback = context
        } else if (parentFragment is OnMilestoneSelected) {
            milestoneCallback = parentFragment as OnMilestoneSelected?
        }
    }

    override fun providePresenter(): BasePresenter<IssuePagerMvp.View> {
        return BasePresenter()
    }

    override fun fragmentLayout(): Int {
        return R.layout.single_container_layout
    }

    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            val bundle = arguments
            val milestoneView =
                com.fastaccess.ui.modules.repos.extras.milestone.MilestoneDialogFragment()
            milestoneView.arguments = bundle
            childFragmentManager
                .beginTransaction()
                .replace(
                    R.id.singleContainer,
                    milestoneView,
                    com.fastaccess.ui.modules.repos.extras.milestone.MilestoneDialogFragment.TAG
                )
                .commit()
        }
    }

    override fun onMilestoneSelected(milestoneModel: MilestoneModel) {
        if (issueCallback != null) issueCallback!!.onMileStoneSelected(milestoneModel)
        if (pullRequestCallback != null) pullRequestCallback!!.onMileStoneSelected(milestoneModel)
        if (milestoneCallback != null) milestoneCallback!!.onMilestoneSelected(milestoneModel)
    }

    companion object {
        fun newInstance(login: String, repo: String): MilestoneDialogFragment {
            val view = MilestoneDialogFragment()
            view.arguments = start()
                .put(BundleConstant.EXTRA, login)
                .put(BundleConstant.ID, repo)
                .end()
            return view
        }
    }
}