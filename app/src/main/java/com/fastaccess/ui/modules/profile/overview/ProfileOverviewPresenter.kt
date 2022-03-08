package com.fastaccess.ui.modules.profile.overview

import com.fastaccess.provider.rest.ApolloProdivder.getApollo
import com.fastaccess.ui.base.mvp.presenter.BasePresenter
import com.fastaccess.ui.widgets.contributions.ContributionsDay
import android.text.TextUtils
import com.fastaccess.data.dao.model.Login
import com.fastaccess.helper.RxHelper
import com.fastaccess.provider.rest.RestProvider
import com.fastaccess.helper.InputHelper
import android.os.Bundle
import com.fastaccess.helper.BundleConstant
import com.fastaccess.github.GetPinnedReposQuery
import com.apollographql.apollo3.rx2.Rx2Apollo
import com.fastaccess.ui.widgets.contributions.GitHubContributionsView
import com.fastaccess.ui.widgets.contributions.ContributionsProvider
import android.graphics.Bitmap
import com.fastaccess.data.dao.Pageable
import com.fastaccess.data.dao.model.User
import io.reactivex.Observable
import retrofit2.Response
import java.lang.NullPointerException
import java.util.ArrayList

/**
 * Created by Kosh on 03 Dec 2016, 9:16 AM
 */
class ProfileOverviewPresenter : BasePresenter<ProfileOverviewMvp.View?>(),
    ProfileOverviewMvp.Presenter {
    @com.evernote.android.state.State
    var _isSuccessResponse = false

    @com.evernote.android.state.State
    var _isFollowing = false

    @com.evernote.android.state.State
    var _login: String? = null
    private val userOrgs = ArrayList<User>()
    private val nodes = ArrayList<GetPinnedReposQuery.Node>()
    private val contributions = ArrayList<ContributionsDay>()
    override fun onCheckFollowStatus(login: String) {
        if (!TextUtils.equals(login, Login.getUser().login)) {
            manageDisposable(RxHelper.getObservable(
                RestProvider.getUserService(
                    isEnterprise
                ).getFollowStatus(login)
            )
                .subscribe({ booleanResponse: Response<Boolean?> ->
                    _isSuccessResponse = true
                    _isFollowing = booleanResponse.code() == 204
                    sendToView {
                        it?.invalidateFollowBtn()
                    }
                }) { obj: Throwable -> obj.printStackTrace() })
        }
    }

    override fun isSuccessResponse(): Boolean {
        return _isSuccessResponse
    }

    override fun isFollowing(): Boolean {
        return _isFollowing
    }

    override fun onFollowButtonClicked(login: String) {
        manageDisposable(RxHelper.getObservable(
            if (!_isFollowing) RestProvider.getUserService(
                isEnterprise
            ).followUser(login) else RestProvider.getUserService(isEnterprise).unfollowUser(login)
        )
            .subscribe({ booleanResponse: Response<Boolean?> ->
                if (booleanResponse.code() == 204) {
                    _isFollowing = !_isFollowing
                    sendToView { it?.invalidateFollowBtn() }
                }
            }) { throwable: Throwable -> onError(throwable) })
    }

    override fun onError(throwable: Throwable) {
        val statusCode = RestProvider.getErrorCode(throwable)
        if (statusCode == 404) {
            sendToView { it?.onUserNotFound() }
            return
        }
        if (!InputHelper.isEmpty(_login)) {
            onWorkOffline(_login!!)
        }
        sendToView { it?.invalidateFollowBtn() }
        super.onError(throwable)
    }

    override fun onFragmentCreated(bundle: Bundle?) {
        if (bundle?.getString(BundleConstant.EXTRA) == null) {
            throw NullPointerException("Either bundle or User is null")
        }
        _login = bundle.getString(BundleConstant.EXTRA)
        if (_login != null) {
            makeRestCall(RestProvider.getUserService(
                isEnterprise
            )
                .getUser(_login!!)
                .doOnComplete {
                    loadPinnedRepos(_login!!)
                    loadOrgs()
                }) { userModel: User? ->
                onSendUserToView(userModel)
                if (userModel != null) {
                    userModel.save(userModel)
                    if (userModel.type != null && userModel.type.equals(
                            "user",
                            ignoreCase = true
                        )
                    ) {
                        onCheckFollowStatus(_login!!)
                    }
                }
            }
        }
    }

    private fun loadPinnedRepos(login: String) {
        val apolloCall = getApollo(isEnterprise)
            .query(GetPinnedReposQuery(login))
        val disposable =
            RxHelper.getObservable(Rx2Apollo.flowable(apolloCall).toObservable()).filter {
                !it.hasErrors()
            }.flatMap {
                if (it.data != null && it.data?.user != null) {
                    return@flatMap Observable.fromIterable(it.data?.user?.pinnedItems?.edges!!)
                }
                return@flatMap Observable.empty<GetPinnedReposQuery.Edge>()
            }.map { it.node!! }.toList()
                .toObservable()
                .subscribe(
                    { nodes1 ->
                        nodes.clear()
                        nodes.addAll(nodes1)
                        sendToView {
                            it?.onInitPinnedRepos(nodes1)
                        }
                    },
                    {
                        it.printStackTrace()
                    }
                )
        manageDisposable(disposable)
    }

    override fun onWorkOffline(login: String) {
        val userModel = User.getUser(login) ?: return
        onSendUserToView(userModel)
    }

    override fun onSendUserToView(userModel: User?) {
        sendToView { it!!.onInitViews(userModel) }
    }

    override fun onLoadContributionWidget(gitHubContributionsView: GitHubContributionsView) {
        if (!isEnterprise) {
            if (contributions.isEmpty()) {
                val url = String.format(URL, login)
                manageDisposable(RxHelper.getObservable(
                    RestProvider.getContribution().getContributions(url)
                )
                    .flatMap { s: String? ->
                        Observable.just(
                            ContributionsProvider().getContributions(
                                s
                            )
                        )
                    }
                    .subscribe({ lists: List<ContributionsDay>? ->
                        contributions.clear()
                        contributions.addAll(lists!!)
                        loadContributions(contributions, gitHubContributionsView)
                    }) { obj: Throwable -> obj.printStackTrace() })
            } else {
                loadContributions(contributions, gitHubContributionsView)
            }
        }
    }

    override fun getOrgs(): ArrayList<User> {
        return userOrgs
    }

    override fun getContributions(): ArrayList<ContributionsDay> {
        return contributions
    }

    override fun getNodes(): ArrayList<GetPinnedReposQuery.Node> {
        return nodes
    }

    override fun getLogin(): String {
        return _login!!
    }

    private fun loadContributions(
        contributions: ArrayList<ContributionsDay>?,
        gitHubContributionsView: GitHubContributionsView
    ) {
        val filter = gitHubContributionsView.getLastContributions(contributions)
        if (filter != null && contributions != null && contributions.isNotEmpty()) {
            val bitmapObservable =
                Observable.just(gitHubContributionsView.drawOnCanvas(filter, contributions))
            manageObservable(bitmapObservable
                .doOnNext { bitmap: Bitmap? ->
                    sendToView { view ->
                        view?.onInitContributions(
                            bitmap != null
                        )
                    }
                })
        }
    }

    private fun loadOrgs() {
        val isMe = _login.equals(
            if (Login.getUser() != null) Login.getUser().login else "",
            ignoreCase = true
        )
        manageDisposable(RxHelper.getObservable(
            if (isMe) RestProvider.getOrgService(
                isEnterprise
            ).myOrganizations else RestProvider.getOrgService(isEnterprise).getMyOrganizations(
                _login!!
            )
        )
            .subscribe({ response: Pageable<User>? ->
                if (response?.items != null) {
                    userOrgs.addAll(response.items!!)
                }
                sendToView { view ->
                    view?.onInitOrgs(
                        userOrgs
                    )
                }
            }) { obj: Throwable -> obj.printStackTrace() })
    }

    companion object {
        private const val URL = "https://github.com/users/%s/contributions"
    }
}