package com.fastaccess.ui.modules.profile.following

import android.view.View
import com.fastaccess.data.entity.User
import com.fastaccess.data.entity.dao.UserDao
import com.fastaccess.helper.RxHelper.getSingle
import com.fastaccess.provider.rest.RestProvider.getUserService
import com.fastaccess.ui.base.mvp.presenter.BasePresenter

/**
 * Created by Kosh on 03 Dec 2016, 3:48 PM
 */
class ProfileFollowingPresenter : BasePresenter<ProfileFollowingMvp.View>(),
    ProfileFollowingMvp.Presenter {
    override val following = ArrayList<User>()
    override var currentPage = 0
    override var previousTotal = 0
    private var lastPage = Int.MAX_VALUE
    override fun onError(throwable: Throwable) {
        sendToView { view ->  //wait view
            if (view.loadMore.parameter != null) {
                onWorkOffline(view.loadMore.parameter!!)
            }
        }
        super.onError(throwable)
    }

    override fun onCallApi(page: Int, parameter: String?): Boolean {
        if (parameter == null) {
            throw NullPointerException("Username is null")
        }
        if (page == 1) {
            lastPage = Int.MAX_VALUE
            sendToView { view -> view.loadMore.reset() }
        }
        if (page > lastPage || lastPage == 0) {
            sendToView { it.hideProgress() }
            return false
        }
        makeRestCall(
            getUserService(isEnterprise).getFollowing(parameter, page)
        ) { response ->
            lastPage = response.last
            if (currentPage == 1) {
                manageObservable(
                    UserDao.saveUserFollowingList(response.items!!, parameter).toObservable()
                )
            }
            sendToView { view ->
                view.onNotifyAdapter(
                    response.items,
                    page
                )
            }
        }
        return true
    }

    override fun onWorkOffline(login: String) {
        if (following.isEmpty()) {
            manageDisposable(getSingle(
                UserDao.getUserFollowingList(login).toList()
            ).subscribe { userModels ->
                sendToView { view: ProfileFollowingMvp.View ->
                    view.onNotifyAdapter(
                        userModels,
                        1
                    )
                }
            })
        } else {
            sendToView { it.hideProgress() }
        }
    }

    override fun onItemClick(position: Int, v: View?, item: User) {}
    override fun onItemLongClick(position: Int, v: View?, item: User) {}
}