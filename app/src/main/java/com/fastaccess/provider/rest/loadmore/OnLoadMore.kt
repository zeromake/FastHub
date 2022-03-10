package com.fastaccess.provider.rest.loadmore

import com.fastaccess.ui.base.mvp.BaseMvp.PaginationListener
import com.fastaccess.ui.widgets.recyclerview.scroll.InfiniteScroll

open class OnLoadMore<P> @JvmOverloads constructor(
    private val presenter: PaginationListener<P>?,
    var parameter: P? = null
) : InfiniteScroll() {
    override fun onLoadMore(page: Int, totalItemsCount: Int): Boolean {
        if (presenter != null) {
            presenter.previousTotal = totalItemsCount
            return presenter.onCallApi(page + 1, parameter)
        }
        return false
    }
}