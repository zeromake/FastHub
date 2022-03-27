package com.fastaccess.ui.modules.repos.code.contributors.graph

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.fastaccess.R
import com.fastaccess.helper.Bundler
import com.fastaccess.provider.rest.RestProvider
import com.fastaccess.ui.base.BaseDialogFragment
import com.fastaccess.ui.base.mvp.BaseMvp
import com.fastaccess.ui.base.mvp.presenter.BasePresenter
import com.fastaccess.ui.delegate.viewFind
import com.fastaccess.ui.modules.repos.code.contributors.graph.model.GraphStatModel
import com.fastaccess.ui.modules.repos.code.contributors.graph.viewcomponent.GraphView
import java.util.*

class GraphContributorsFragment : BaseDialogFragment<GraphContributorsMvp.View,
        GraphContributorsPresenter>(), GraphContributorsMvp.View {
    val toolbar: Toolbar by viewFind(R.id.toolbar)
    val titleView: TextView by viewFind(R.id.titleView)
    private val graphView: GraphView by viewFind(R.id.graphView)

    override fun providePresenter(): GraphContributorsPresenter = GraphContributorsPresenter()

    override fun fragmentLayout(): Int = R.layout.dialog_contribution_graph

    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        toolbar.setNavigationIcon(R.drawable.ic_clear)
        toolbar.setNavigationOnClickListener { dismiss() }
        toolbar.title = getString(R.string.contributions)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter!!.onViewCreated(arguments)
    }

    private fun getDateString(calendar: Calendar): String {
        return "${calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())} ${calendar.get(Calendar.DAY_OF_MONTH)}, ${calendar.get(Calendar.YEAR)}"
    }

    override fun modelGraph(stats: GraphStatModel?) {
        val filterByLogin = arguments?.getString("FilterOwner")
        val data = stats?.contributions?.find { it ->  it.author.login == filterByLogin}
        val weeks = data!!.weeks
        val firstDate = Calendar.getInstance().apply {
            time = Date(weeks.first().starting_week * 1000)
        }
        val lastDate = Calendar.getInstance().apply {
            time = Date(weeks.last().starting_week * 1000)
        }
        titleView.text = "${getDateString(firstDate)} - ${getDateString(lastDate)}"
        graphView.graphData = weeks
        graphView.visibility = View.VISIBLE
        hideProgress()
    }

    companion object {
        @JvmStatic
        fun newInstance(owner: String, repo: String, filterByLogin: String? = null): GraphContributorsFragment {
            val fragment = GraphContributorsFragment()
            fragment.arguments = Bundler.start()
                .put("OwnerName", owner)
                .put("RepoName", repo)
                .put("FilterOwner", filterByLogin)
                .end()
            return fragment
        }
    }
}