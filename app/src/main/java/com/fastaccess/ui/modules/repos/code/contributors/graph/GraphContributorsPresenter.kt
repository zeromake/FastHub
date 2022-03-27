package com.fastaccess.ui.modules.repos.code.contributors.graph

import android.os.Bundle
import android.view.View
import com.fastaccess.provider.crash.Report
import com.fastaccess.provider.rest.RestProvider
import com.fastaccess.ui.base.mvp.presenter.BasePresenter
import com.fastaccess.ui.modules.repos.code.contributors.graph.model.GraphStatModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.ResponseBody

class GraphContributorsPresenter : BasePresenter<GraphContributorsMvp.View>(), GraphContributorsMvp.Presenter {

    override fun onViewCreated(bundle: Bundle?) {
        val observable = RestProvider.getRepoService(isEnterprise).getContributorsStats(
            bundle!!.getString("OwnerName")!!,
            bundle.getString("RepoName")!!
        )
        makeRestCall(observable) { response: ResponseBody ->
            val statsModel: GraphStatModel? =
                    Gson().fromJson(response.string(), object : TypeToken<GraphStatModel>() {}.type)
            sendToView { view: GraphContributorsMvp.View ->
                view.modelGraph(statsModel)
            }
        }
    }

}