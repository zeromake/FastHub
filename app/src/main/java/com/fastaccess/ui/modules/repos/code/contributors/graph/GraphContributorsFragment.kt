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
    val titleView: TextView by viewFind(R.id.titleView)
    private val graphView: GraphView by viewFind(R.id.graphView)

    override fun providePresenter(): BasePresenter<BaseMvp.FAView> = BasePresenter()

    override fun fragmentLayout(): Int = R.layout.dialog_contribution_graph

    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        toolbar.setNavigationIcon(R.drawable.ic_clear)
        toolbar.setNavigationOnClickListener { dismiss() }
        toolbar.title = "${getString(R.string.contributions)} (${arguments?.getInt("total")})"
        val weeks: List<GraphStatModel.ContributionStats.Week> = Gson().fromJson(
            arguments?.getString("weeks"), object: TypeToken<ArrayList<GraphStatModel.ContributionStats.Week>>() {}.type)
        val firstDate = Calendar.getInstance().apply {
            time = Date(weeks.first().starting_week * 1000)
        }
        val lastDate = Calendar.getInstance().apply {
            time = Date(weeks.last().starting_week * 1000)
        }
        titleView.text = "${getDateString(firstDate)} - ${getDateString(lastDate)}"
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