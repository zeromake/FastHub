package com.fastaccess.ui.modules.repos.code.contributors.graph

import android.os.Bundle
import com.fastaccess.ui.base.mvp.BaseMvp.FAView
import com.fastaccess.ui.base.mvp.BaseMvp.FAPresenter
import com.fastaccess.ui.modules.repos.code.contributors.graph.model.GraphStatModel

interface GraphContributorsMvp {
    interface View : FAView {
        fun modelGraph(stats: GraphStatModel?)
    }

    interface Presenter : FAPresenter {
        fun onViewCreated(bundle: Bundle?)
    }
}