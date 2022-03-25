package com.fastaccess.ui.modules.repos.reactions

import android.os.Bundle
import com.fastaccess.data.dao.model.User
import com.fastaccess.provider.rest.loadmore.OnLoadMore
import com.fastaccess.ui.base.mvp.BaseMvp.FAView
import com.fastaccess.ui.base.mvp.BaseMvp.PaginationListener

/**
 * Created by Kosh on 11 Apr 2017, 11:19 AM
 */
interface ReactionsDialogMvp {
    interface View : FAView {
        fun onNotifyAdapter(items: List<User>?, page: Int)
        val loadMore: OnLoadMore<String>
    }

    interface Presenter : PaginationListener<String> {
        fun onFragmentCreated(bundle: Bundle?)
        val users: ArrayList<User>
    }
}