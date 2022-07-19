package com.fastaccess.ui.modules.repos.pull_requests.pull_request.merge

import com.fastaccess.ui.base.mvp.BaseMvp.FAView

/**
 * Created by Kosh on 18 Mar 2017, 12:11 PM
 */
interface MergePullRequestMvp {
    interface MergeCallback {
        fun onMerge(msg: String, mergeMethod: String)
    }

    interface View : FAView
    interface Presenter
}