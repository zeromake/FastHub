package com.fastaccess.ui.modules.repos.code.contributors.graph

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.fastaccess.R
import com.fastaccess.helper.Bundler
import com.fastaccess.ui.base.BaseDialogFragment
import com.fastaccess.ui.base.mvp.BaseMvp
import com.fastaccess.ui.base.mvp.presenter.BasePresenter
import com.fastaccess.ui.delegate.viewFind
import com.fastaccess.ui.modules.repos.code.contributors.graph.model.GraphStatModel
import com.fastaccess.ui.modules.repos.code.contributors.graph.viewcomponent.GraphView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*


class GraphContributorsFragment : BaseDialogFragment<BaseMvp.FAView, BasePresenter<BaseMvp.FAView>>() {
    val toolbar: Toolbar by viewFind(R.id.toolbar)
    val timelineTitle: TextView by viewFind(R.id.graphTimelineTitle)
    val commitsCount: TextView by viewFind(R.id.commitsCount)
    val additionsCount: TextView by viewFind(R.id.additionsCount)
    val deletionCount: TextView by viewFind(R.id.deletionsCount)
    private val graphView: GraphView by viewFind(R.id.graphView)

    override fun providePresenter(): BasePresenter<BaseMvp.FAView> = BasePresenter()

    override fun fragmentLayout(): Int = R.layout.dialog_contribution_graph

    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        toolbar.setNavigationOnClickListener { dismiss() }
        val weeks: List<GraphStatModel.ContributionStats.Week> = Gson().fromJson(
            arguments?.getString("weeks"), object: TypeToken<ArrayList<GraphStatModel.ContributionStats.Week>>() {}.type)
        val firstDate = Calendar.getInstance().apply {
            time = Date(weeks.first().starting_week * 1000)
        }
        val lastDate = Calendar.getInstance().apply {
            time = Date(weeks.last().starting_week * 1000)
        }
        val additions: Int = weeks.fold(0) {acc, w -> acc+w.additions}
        val deletions: Int = weeks.fold(0) {acc, w -> acc+w.deletions}
        val commits = "${getString(R.string.commits)} (${"%,d".format(arguments?.getInt("total"))})"
        timelineTitle.text = getString(R.string.graph_timeline_text, getDateString(firstDate), getDateString(lastDate))
        commitsCount.text = commits
        additionsCount.text = getString(R.string.additions, "%,d".format(additions))
        deletionCount.text = getString(R.string.deletions, "%,d".format(deletions))
        graphView.graphData = weeks
        graphView.visibility = View.VISIBLE
    }

    private fun getDateString(calendar: Calendar): String {
        return "${calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())} ${calendar.get(Calendar.DAY_OF_MONTH)}, ${calendar.get(Calendar.YEAR)}"
    }

    companion object {
        @JvmStatic
        fun newInstance(data: GraphStatModel.ContributionStats?): GraphContributorsFragment {
            val fragment = GraphContributorsFragment()
            fragment.arguments = Bundler.start()
                .put("weeks", Gson().toJson(data?.weeks))
                .put("total", data?.total)
                .end()
            return fragment
        }
    }
}