package com.fastaccess.ui.modules.repos

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.fastaccess.R
import com.fastaccess.data.entity.Repo
import com.fastaccess.data.entity.dao.LoginDao
import com.fastaccess.data.entity.dao.PinnedReposDao
import com.fastaccess.data.entity.dao.RepoDao
import com.fastaccess.helper.ActivityHelper.getVisibleFragment
import com.fastaccess.helper.AppHelper.getFragmentByTag
import com.fastaccess.helper.InputHelper.isEmpty
import com.fastaccess.helper.RxHelper.getObservable
import com.fastaccess.helper.RxHelper.getSingle
import com.fastaccess.provider.rest.RestProvider.getErrorCode
import com.fastaccess.provider.rest.RestProvider.getRepoService
import com.fastaccess.ui.base.mvp.presenter.BasePresenter
import com.fastaccess.ui.modules.repos.RepoPagerMvp.RepoNavigationType
import com.fastaccess.ui.modules.repos.code.RepoCodePagerFragment
import com.fastaccess.ui.modules.repos.issues.RepoIssuesPagerFragment
import com.fastaccess.ui.modules.repos.projects.RepoProjectsFragmentPager
import com.fastaccess.ui.modules.repos.projects.RepoProjectsFragmentPager.Companion.TAG
import com.fastaccess.ui.modules.repos.projects.RepoProjectsFragmentPager.Companion.newInstance
import com.fastaccess.ui.modules.repos.pull_requests.RepoPullRequestPagerFragment
import com.fastaccess.utils.Optional
import io.reactivex.Observable

/**
 * Created by Kosh on 09 Dec 2016, 4:17 PM
 */
class RepoPagerPresenter : BasePresenter<RepoPagerMvp.View>(), RepoPagerMvp.Presenter {
    @com.evernote.android.state.State
    override var isWatched = false

    @com.evernote.android.state.State
    override var isStarred = false

    @com.evernote.android.state.State
    override var isForked = false

    @com.evernote.android.state.State
    var login: String? = null

    @com.evernote.android.state.State
    var repoId: String? = null

    @com.evernote.android.state.State
    override var repo: Repo? = null

    @JvmField
    @com.evernote.android.state.State
    var navTyp = 0

    @JvmField
    @com.evernote.android.state.State
    var isCollaborator = false
    private fun callApi(navTyp: Int) {
        if (isEmpty(login) || isEmpty(repoId)) return
        val observable = LoginDao.getUser().toObservable().flatMap {
            Observable.zip(
                getRepoService(isEnterprise).getRepo(
                    login(),
                    repoId()
                ),
                getRepoService(isEnterprise).isCollaborator(
                    login!!,
                    repoId!!,
                    it.or().login!!
                )
            ) { repo1, booleanResponse ->
                isCollaborator = booleanResponse.code() == 204
                repo1
            }
        }
        makeRestCall(
            observable
        ) { repoModel: Repo ->
            repo = repoModel
            manageObservable(RepoDao.save(repo!!).toObservable())
            updatePinned(repoModel)
            sendToView { view ->
                view.onInitRepo()
                view.onNavigationChanged(navTyp)
            }
            onCheckStarring()
            onCheckWatching()
        }
    }

    override fun onError(throwable: Throwable) {
        val code = getErrorCode(throwable)
        if (code == 404) {
            sendToView { it.onOpenUrlInBrowser() }
        } else {
            onWorkOffline()
        }
        super.onError(throwable)
    }

    override fun onUpdatePinnedEntry(repoId: String, login: String) {
        manageObservable(PinnedReposDao.updateEntry("$login/$repoId").toObservable())
    }

    override fun onActivityCreate(repoId: String, login: String, navTyp: Int) {
        this.login = login
        this.repoId = repoId
        this.navTyp = navTyp
        if (repo == null || !isApiCalled) {
            callApi(navTyp)
        } else {
            sendToView { it.onInitRepo() }
        }
    }

    override fun repoId(): String {
        return repoId!!
    }

    override fun login(): String {
        return login!!
    }

    override val isRepoOwner: Boolean
        get() = if (repo != null && repo!!.owner != null) {
            val login = LoginDao.getUser().blockingGet().or()
            repo!!.owner!!.login == login.login || isCollaborator
        } else false

    override fun onWatch() {
        if (repo == null) return
        isWatched = !isWatched
        sendToView { view ->
            view.onRepoWatched(isWatched)
            view.onChangeWatchedCount(isWatched)
        }
    }

    override fun onStar() {
        if (repo == null) return
        isStarred = !isStarred
        sendToView { view ->
            view.onRepoStarred(isStarred)
            view.onChangeStarCount(isStarred)
        }
    }

    override fun onFork() {
        if (!isForked && repo != null) {
            isForked = true
            sendToView { view ->
                view.onRepoForked(isForked)
                view.onChangeForkCount(isForked)
            }
        }
    }

    override fun onCheckWatching() {
        if (repo != null) {
            val login = login()
            val name = repoId()
            manageDisposable(
                getObservable(getRepoService(isEnterprise).isWatchingRepo(login, name))
                    .doOnSubscribe {
                        sendToView { view -> view.onEnableDisableWatch(false) }
                    }
                    .doOnNext { subscriptionModel ->
                        sendToView { view ->
                            view.onRepoWatched(
                                subscriptionModel.isSubscribed.also { isWatched = it })
                        }
                    }
                    .subscribe({ }) {
                        isWatched = false
                        sendToView { view ->
                            view.onRepoWatched(
                                isWatched
                            )
                        }
                    })
        }
    }

    override fun onCheckStarring() {
        if (repo != null) {
            val login = login()
            val name = repoId()
            manageDisposable(
                getObservable(getRepoService(isEnterprise).checkStarring(login, name))
                    .doOnSubscribe {
                        sendToView { view -> view.onEnableDisableStar(false) }
                    }
                    .doOnNext { response ->
                        sendToView { view ->
                            view.onRepoStarred((response.code() == 204).also {
                                isStarred = it
                            })
                        }
                    }
                    .subscribe({ }) {
                        isStarred = false
                        sendToView { view ->
                            view.onRepoStarred(
                                isStarred
                            )
                        }
                    })
        }
    }

    override fun onWorkOffline() {
        if (!isEmpty(login()) && !isEmpty(repoId())) {
            manageDisposable(
                getSingle(
                    RepoDao.getRepo(
                        repoId!!, login!!
                    )
                )
                    .subscribe({ repoModel: Optional<Repo> ->
                        repo = repoModel.get()
                        if (repo != null) {
                            sendToView { view ->
                                view.onInitRepo()
                                view.onNavigationChanged(RepoPagerMvp.CODE)
                            }
                        } else {
                            callApi(navTyp)
                        }
                    }) { obj: Throwable -> obj.printStackTrace() })
        } else {
            sendToView { it.onFinishActivity() }
        }
    }

    override fun onModuleChanged(fragmentManager: FragmentManager, @RepoNavigationType type: Int) {
        val currentVisible = getVisibleFragment(fragmentManager)
        val codePagerView =
            getFragmentByTag(fragmentManager, RepoCodePagerFragment.TAG) as RepoCodePagerFragment?
        val repoIssuesPagerView = getFragmentByTag(
            fragmentManager,
            RepoIssuesPagerFragment.TAG
        ) as RepoIssuesPagerFragment?
        val pullRequestPagerView = getFragmentByTag(
            fragmentManager,
            RepoPullRequestPagerFragment.TAG
        ) as RepoPullRequestPagerFragment?
        val projectsFragmentPager = getFragmentByTag(
            fragmentManager,
            TAG
        ) as RepoProjectsFragmentPager?
        if (repo == null) {
            sendToView { it.onFinishActivity() }
            return
        }
        if (currentVisible == null) return
        when (type) {
            RepoPagerMvp.PROFILE -> {
                sendToView { it.openUserProfile() }
                if (codePagerView == null) {
                    onAddAndHide(
                        fragmentManager, RepoCodePagerFragment.newInstance(
                            repoId(), login(),
                            repo!!.htmlUrl!!, repo!!.url!!, repo!!.defaultBranch!!
                        ), currentVisible
                    )
                } else {
                    onShowHideFragment(fragmentManager, codePagerView, currentVisible)
                }
            }
            RepoPagerMvp.CODE -> if (codePagerView == null) {
                onAddAndHide(
                    fragmentManager, RepoCodePagerFragment.newInstance(
                        repoId(), login(),
                        repo!!.htmlUrl!!, repo!!.url!!, repo!!.defaultBranch!!
                    ), currentVisible
                )
            } else {
                onShowHideFragment(fragmentManager, codePagerView, currentVisible)
            }
            RepoPagerMvp.ISSUES -> {
                if (!repo!!.hasIssues) {
                    sendToView { view ->
                        view.showMessage(
                            R.string.error,
                            R.string.repo_issues_is_disabled
                        )
                    }
                } else if (repoIssuesPagerView == null) {
                    onAddAndHide(
                        fragmentManager,
                        RepoIssuesPagerFragment.newInstance(repoId(), login()),
                        currentVisible
                    )
                } else {
                    onShowHideFragment(fragmentManager, repoIssuesPagerView, currentVisible)
                }
            }
            RepoPagerMvp.PULL_REQUEST -> if (pullRequestPagerView == null) {
                onAddAndHide(
                    fragmentManager,
                    RepoPullRequestPagerFragment.newInstance(repoId(), login()),
                    currentVisible
                )
            } else {
                onShowHideFragment(fragmentManager, pullRequestPagerView, currentVisible)
            }
            RepoPagerMvp.PROJECTS -> if (projectsFragmentPager == null) {
                onAddAndHide(fragmentManager, newInstance(login(), repoId()), currentVisible)
            } else {
                onShowHideFragment(fragmentManager, projectsFragmentPager, currentVisible)
            }
        }
    }

    override fun onShowHideFragment(
        fragmentManager: FragmentManager,
        toShow: Fragment?,
        toHide: Fragment?
    ) {
        if (toShow != null && toHide != null) {
            fragmentManager
                .beginTransaction()
                .hide(toHide)
                .show(toShow)
                .commit()
            toHide.onHiddenChanged(true)
            toShow.onHiddenChanged(false)
        }

    }

    override fun onAddAndHide(
        fragmentManager: FragmentManager,
        toAdd: Fragment?,
        toHide: Fragment?
    ) {
        if (toAdd != null && toHide != null) {
            fragmentManager
                .beginTransaction()
                .hide(toHide)
                .add(R.id.container, toAdd, toAdd.javaClass.simpleName)
                .commit()
            toHide.onHiddenChanged(true)
            toAdd.onHiddenChanged(false)
        }
    }

    override fun onDeleteRepo() {
        if (isRepoOwner) {
            makeRestCall(
                getRepoService(isEnterprise).deleteRepo(
                    login!!, repoId!!
                )
            ) { booleanResponse ->
                if (booleanResponse.code() == 204) {
//                            if (repo != null) repo.delete().execute();
                    repo = null
                    sendToView { it.onInitRepo() }
                }
            }
        }
    }

    override fun onPinUnpinRepo() {
        if (repo == null) return
        manageObservable(PinnedReposDao.pinUpin(repo!!).toObservable()) {
            sendToView { view -> view.onRepoPinned(it) }
        }
    }

    override fun updatePinned(forks: Int, stars: Int, watching: Int) {
        repo!!.stargazersCount = stars.toLong()
        repo!!.forksCount = forks.toLong()
        repo!!.subsCount = watching
        updatePinned(repo)
    }

    override fun onMenuItemSelect(@IdRes itemId: Int, position: Int, fromUser: Boolean) {
        if (itemId == R.id.issues && repo != null && !repo!!.hasIssues) {
            sendToView { it.disableIssueTab() }
            return
        }
        if (view != null && isViewAttached && fromUser) {
            view!!.onNavigationChanged(position)
        }
    }

    override fun onMenuItemReselect(@IdRes itemId: Int, position: Int, fromUser: Boolean) {}
    private fun updatePinned(repoModel: Repo?) {
        manageObservable(
            PinnedReposDao.get(
                repoModel!!.fullName!!
            ).toObservable().flatMap {
                if (it.isEmpty()) {
                    return@flatMap Observable.empty()
                }
                val item = it.or()
                item.pinnedRepo = repoModel
                PinnedReposDao.update(item).toObservable()
            })
    }
}