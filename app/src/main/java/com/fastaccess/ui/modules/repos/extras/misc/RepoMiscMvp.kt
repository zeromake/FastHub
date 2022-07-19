package com.fastaccess.ui.modules.repos.extras.misc

import androidx.annotation.IntDef
import com.fastaccess.data.entity.User
import com.fastaccess.provider.rest.loadmore.OnLoadMore
import com.fastaccess.ui.base.adapter.BaseViewHolder
import com.fastaccess.ui.base.mvp.BaseMvp.FAView
import com.fastaccess.ui.base.mvp.BaseMvp.PaginationListener

/**
 * Created by Kosh on 04 May 2017, 8:30 PM
 */
interface RepoMiscMvp {
    @IntDef(WATCHERS, FORKS, STARS)
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    annotation class MiscType
    interface View : FAView {
        fun onNotifyAdapter(items: List<User>?, page: Int)
        val loadMore: OnLoadMore<Int>
    }

    interface Presenter : PaginationListener<Int>, BaseViewHolder.OnItemClickListener<User> {
        val list: ArrayList<User>

        @get:MiscType
        val type: Int
    }

    companion object {
        const val WATCHERS = 0
        const val FORKS = 1
        const val STARS = 2
    }
}