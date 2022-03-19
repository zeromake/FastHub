package com.fastaccess.ui.modules.repos

import androidx.annotation.IntDef
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.fastaccess.data.dao.model.Repo
import com.fastaccess.ui.base.mvp.BaseMvp.FAPresenter
import com.fastaccess.ui.base.mvp.BaseMvp.FAView
import com.fastaccess.ui.modules.filter.chooser.FilterAddChooserListener
import it.sephiroth.android.library.bottomnavigation.BottomNavigation

/**
 * Created by Kosh on 09 Dec 2016, 4:16 PM
 */
interface RepoPagerMvp {
    @IntDef(CODE, ISSUES, PULL_REQUEST, PROJECTS, PROFILE)
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    annotation class RepoNavigationType
    interface View : FAView, FilterAddChooserListener {
        fun onNavigationChanged(@RepoNavigationType navType: Int)
        fun onFinishActivity()
        fun onInitRepo()
        fun onRepoWatched(isWatched: Boolean)
        fun onRepoStarred(isStarred: Boolean)
        fun onRepoForked(isForked: Boolean)
        fun onRepoPinned(isPinned: Boolean)
        fun onEnableDisableWatch(isEnabled: Boolean)
        fun onEnableDisableStar(isEnabled: Boolean)
        fun onEnableDisableFork(isEnabled: Boolean)
        fun onChangeWatchedCount(isWatched: Boolean)
        fun onChangeStarCount(isStarred: Boolean)
        fun onChangeForkCount(isForked: Boolean)
        fun hasUserInteractedWithView(): Boolean
        fun disableIssueTab()
        fun openUserProfile()
        fun onScrolled(isUp: Boolean)
        val isCollaborator: Boolean
    }

    interface Presenter : FAPresenter, BottomNavigation.OnMenuItemSelectionListener {
        fun onUpdatePinnedEntry(repoId: String, login: String)
        fun onActivityCreate(repoId: String, login: String, @RepoNavigationType navTyp: Int)
        fun repoId(): String
        fun login(): String
        val repo: Repo?
        val isWatched: Boolean
        val isStarred: Boolean
        val isForked: Boolean
        val isRepoOwner: Boolean
        fun onWatch()
        fun onStar()
        fun onFork()
        fun onCheckWatching()
        fun onCheckStarring()
        fun onWorkOffline()
        fun onModuleChanged(fragmentManager: FragmentManager, @RepoNavigationType type: Int)
        fun onShowHideFragment(fragmentManager: FragmentManager, toShow: Fragment?, toHide: Fragment?)
        fun onAddAndHide(fragmentManager: FragmentManager, toAdd: Fragment?, toHide: Fragment?)
        fun onDeleteRepo()
        fun onPinUnpinRepo()
        fun updatePinned(forks: Int, stars: Int, watching: Int)
    }

    interface TabsBadgeListener {
        fun onSetBadge(tabIndex: Int, count: Int)
    }

    companion object {
        const val CODE = 0
        const val ISSUES = 1
        const val PULL_REQUEST = 2
        const val PROJECTS = 3
        const val PROFILE = 4
    }
}