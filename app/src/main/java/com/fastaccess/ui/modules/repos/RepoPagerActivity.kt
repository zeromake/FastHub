package com.fastaccess.ui.modules.repos

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.format.Formatter
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.CheckBox
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
import com.evernote.android.state.State
import com.fastaccess.R
import com.fastaccess.data.dao.NameParser
import com.fastaccess.data.entity.dao.LoginDao
import com.fastaccess.data.entity.dao.PinnedReposDao
import com.fastaccess.helper.*
import com.fastaccess.helper.AnimHelper.mimicFabVisibility
import com.fastaccess.helper.InputHelper.isEmpty
import com.fastaccess.helper.InputHelper.toLong
import com.fastaccess.helper.ParseDateFormat.Companion.getTimeAgo
import com.fastaccess.provider.colors.ColorsProvider.getColorAsColor
import com.fastaccess.provider.crash.Report
import com.fastaccess.provider.scheme.LinkParserHelper.isEnterprise
import com.fastaccess.provider.scheme.SchemeParser.launchUri
import com.fastaccess.provider.tasks.git.GithubActionService
import com.fastaccess.provider.tasks.git.GithubActionService.Companion.startForRepo
import com.fastaccess.ui.adapter.TopicsAdapter
import com.fastaccess.ui.base.BaseActivity
import com.fastaccess.ui.modules.filter.issues.FilterIssuesActivity
import com.fastaccess.ui.modules.main.MainActivity.Companion.launchMainActivity
import com.fastaccess.ui.modules.repos.RepoPagerMvp.RepoNavigationType
import com.fastaccess.ui.modules.repos.code.RepoCodePagerFragment
import com.fastaccess.ui.modules.repos.extras.labels.LabelsDialogFragment
import com.fastaccess.ui.modules.repos.extras.license.RepoLicenseBottomSheet.Companion.newInstance
import com.fastaccess.ui.modules.repos.extras.milestone.create.MilestoneDialogFragment
import com.fastaccess.ui.modules.repos.extras.misc.RepoMiscDialogFragment
import com.fastaccess.ui.modules.repos.extras.misc.RepoMiscMvp
import com.fastaccess.ui.modules.repos.issues.RepoIssuesPagerFragment
import com.fastaccess.ui.modules.repos.pull_requests.RepoPullRequestPagerFragment
import com.fastaccess.ui.modules.repos.wiki.WikiActivity.Companion.getWiki
import com.fastaccess.ui.modules.user.UserPagerActivity.Companion.startActivity
import com.fastaccess.ui.widgets.AvatarLayout
import com.fastaccess.ui.widgets.FontTextView
import com.fastaccess.ui.widgets.ForegroundImageView
import com.fastaccess.ui.widgets.SpannableBuilder.Companion.builder
import com.fastaccess.ui.widgets.dialog.MessageDialogView
import com.fastaccess.ui.widgets.dialog.MessageDialogView.Companion.newInstance
import com.fastaccess.utils.setOnThrottleClickListener
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton.OnVisibilityChangedListener
import it.sephiroth.android.library.bottomnavigation.BottomNavigation
import java.text.NumberFormat

/**
 * Created by Kosh on 09 Dec 2016, 4:17 PM
 */
class RepoPagerActivity : BaseActivity<RepoPagerMvp.View, RepoPagerPresenter>(),
    RepoPagerMvp.View {
    val avatarLayout: AvatarLayout? by lazy { viewFind(R.id.avatarLayout) }
    val title: FontTextView? by lazy { viewFind(R.id.headerTitle) }
    val size: FontTextView? by lazy { viewFind(R.id.size) }
    val date: FontTextView? by lazy { viewFind(R.id.date) }
    private val forkRepo: FontTextView? by lazy { viewFind(R.id.forkRepo) }
    val starRepo: FontTextView? by lazy { viewFind(R.id.starRepo) }
    val watchRepo: FontTextView? by lazy { viewFind(R.id.watchRepo) }
    val license: FontTextView? by lazy { viewFind(R.id.license) }
    val bottomNavigation: BottomNavigation? by lazy { viewFind(R.id.bottomNavigation) }
    val fab: FloatingActionButton? by lazy { viewFind(R.id.fab) }
    val language: FontTextView? by lazy { viewFind(R.id.language) }
    val detailsIcon: View? by lazy { viewFind(R.id.detailsIcon) }
    private val tagsIcon: View? by lazy { viewFind(R.id.tagsIcon) }
    private val watchRepoImage: ForegroundImageView? by lazy { viewFind(R.id.watchRepoImage) }
    val starRepoImage: ForegroundImageView? by lazy { viewFind(R.id.starRepoImage) }
    private val forkRepoImage: ForegroundImageView? by lazy { viewFind(R.id.forkRepoImage) }
    val licenseLayout: View? by lazy { viewFind(R.id.licenseLayout) }
    val watchRepoLayout: View? by lazy { viewFind(R.id.watchRepoLayout) }
    val starRepoLayout: View? by lazy { viewFind(R.id.starRepoLayout) }
    private val forkRepoLayout: View? by lazy { viewFind(R.id.forkRepoLayout) }
    val pinImage: ForegroundImageView? by lazy { viewFind(R.id.pinImage) }
    val pinLayout: View? by lazy { viewFind(R.id.pinLayout) }
    val pinText: FontTextView? by lazy { viewFind(R.id.pinText) }
    val filterLayout: View? by lazy { viewFind(R.id.filterLayout) }
    private val topicsList: RecyclerView? by lazy { viewFind(R.id.topicsList) }
    private val sortByUpdated: CheckBox? by lazy { viewFind(R.id.sortByUpdated) }
    private val wikiLayout: View? by lazy { viewFind(R.id.wikiLayout) }

    @State
    @RepoNavigationType
    var navType = 0

    @State
    var login: String? = null

    @State
    var repoId: String? = null

    @State
    var showWhich = -1
    private val numberFormat = NumberFormat.getNumberInstance()
    private var userInteracted = false
    private var accentColor = 0
    private var iconColor = 0

    private fun onShowDateHint(): Boolean {
        showMessage(R.string.creation_date, R.string.creation_date_hint)
        return true
    }

    private fun onShowLastUpdateDateHint(): Boolean {
        showMessage(R.string.last_updated, R.string.last_updated_hint)
        return true
    }

    private fun onFabLongClick(): Boolean {
        if (navType == RepoPagerMvp.ISSUES) {
            onAddSelected()
            return true
        }
        return false
    }

    private fun onFabClicked() {
        if (navType == RepoPagerMvp.ISSUES) {
            fab!!.hide(object : OnVisibilityChangedListener() {
                override fun onHidden(fab: FloatingActionButton) {
                    super.onHidden(fab)
                    if (appbar != null) appbar!!.setExpanded(false, true)
                    bottomNavigation!!.setExpanded(expanded = false, animate = true)
                    mimicFabVisibility(true, filterLayout!!, null)
                }
            })
        } else if (navType == RepoPagerMvp.PULL_REQUEST) {
            val pullRequestPagerView = AppHelper.getFragmentByTag(
                supportFragmentManager,
                RepoPullRequestPagerFragment.TAG
            ) as RepoPullRequestPagerFragment?
            if (pullRequestPagerView != null) {
                FilterIssuesActivity.startActivity(
                    this, presenter!!.login(), presenter!!.repoId(), false,
                    pullRequestPagerView.currentItem == 0, isEnterprise
                )
            }
        } else {
            fab!!.hide()
        }
    }

    fun onAddIssues() {
        hideFilterLayout()
        onAddSelected()
    }

    fun onSearch() {
        hideFilterLayout()
        onSearchSelected()
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (navType == RepoPagerMvp.ISSUES && filterLayout!!.isShown) {
            val viewRect = ViewHelper.getLayoutPosition(filterLayout!!)
            if (!viewRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                hideFilterLayout()
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun onTitleClick() {
        val repoModel = presenter!!.repo
        if (repoModel != null && !isEmpty(repoModel.description)) {
            newInstance(
                repoModel.fullName!!, repoModel.description!!,
                isMarkDown = false,
                hideCancel = true
            )
                .show(supportFragmentManager, MessageDialogView.TAG)
        }
    }

    private fun onTagsClick() {
        if (topicsList!!.adapter!!.itemCount > 0) {
            TransitionManager.beginDelayedTransition(topicsList!!)
            topicsList!!.visibility =
                if (topicsList!!.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.forkRepoLayout -> newInstance(
                getString(R.string.fork),
                String.format("%s %s/%s?", getString(R.string.fork), login, repoId),
                Bundler.start().put(BundleConstant.EXTRA, true)
                    .put(BundleConstant.YES_NO_EXTRA, true).end()
            )
                .show(supportFragmentManager, MessageDialogView.TAG)
            R.id.starRepoLayout -> if (!isEmpty(presenter!!.login()) && !isEmpty(
                    presenter!!.repoId()
                )
            ) {
                startForRepo(
                    this,
                    presenter!!.login(),
                    presenter!!.repoId(),
                    if (presenter!!.isStarred) GithubActionService.UNSTAR_REPO else GithubActionService.STAR_REPO,
                    isEnterprise
                )
                presenter!!.onStar()
            }
            R.id.watchRepoLayout -> if (!isEmpty(presenter!!.login()) && !isEmpty(
                    presenter!!.repoId()
                )
            ) {
                startForRepo(
                    this,
                    presenter!!.login(),
                    presenter!!.repoId(),
                    if (presenter!!.isWatched) GithubActionService.UNWATCH_REPO else GithubActionService.WATCH_REPO,
                    isEnterprise
                )
                presenter!!.onWatch()
            }
            R.id.pinLayout -> {
                pinLayout!!.isEnabled = false
                presenter!!.onPinUnpinRepo()
            }
            R.id.wikiLayout -> ActivityHelper.startReveal(
                this,
                getWiki(this, repoId, login),
                wikiLayout!!
            )
            R.id.licenseLayout -> if (presenter!!.repo != null) {
                val licenseModel = presenter!!.repo!!.license!!
                val license =
                    if (!isEmpty(licenseModel.spdxId)) licenseModel.spdxId else licenseModel.name
                newInstance(presenter!!.login(), presenter!!.repoId(), license!!)
                    .show(supportFragmentManager, "RepoLicenseBottomSheet")
            }
        }
    }

    fun onLongClick(view: View?): Boolean {
        when (view!!.id) {
            R.id.forkRepoLayout -> {
                RepoMiscDialogFragment.show(
                    supportFragmentManager,
                    login!!,
                    repoId!!,
                    RepoMiscMvp.FORKS
                )
                return true
            }
            R.id.starRepoLayout -> {
                RepoMiscDialogFragment.show(
                    supportFragmentManager,
                    login!!,
                    repoId!!,
                    RepoMiscMvp.STARS
                )
                return true
            }
            R.id.watchRepoLayout -> {
                RepoMiscDialogFragment.show(
                    supportFragmentManager,
                    login!!,
                    repoId!!,
                    RepoMiscMvp.WATCHERS
                )
                return true
            }
        }
        return false
    }

    fun onSortIssues(isChecked: Boolean) {
        val pagerView = AppHelper.getFragmentByTag(
            supportFragmentManager,
            RepoIssuesPagerFragment.TAG
        ) as RepoIssuesPagerFragment?
        pagerView?.onChangeIssueSort(isChecked)
        hideFilterLayout()
    }

    override fun layout(): Int {
        return R.layout.repo_pager_activity
    }

    override val isTransparent: Boolean
        get() = true

    override fun canBack(): Boolean {
        return true
    }

    override val isSecured: Boolean
        get() = false

    override fun providePresenter(): RepoPagerPresenter {
        return RepoPagerPresenter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        date!!.setOnLongClickListener {
            onShowDateHint()
        }
        size!!.setOnLongClickListener {
            onShowLastUpdateDateHint()
        }
        fab!!.setOnLongClickListener {
            onFabLongClick()
        }
        fab!!.setOnThrottleClickListener {
            onFabClicked()
        }

        viewFind<View>(R.id.add)!!.setOnThrottleClickListener {
            onAddIssues()
        }
        viewFind<View>(R.id.search)!!.setOnThrottleClickListener {
            onSearch()
        }
        detailsIcon!!.setOnThrottleClickListener {
            onTitleClick()
        }
        tagsIcon!!.setOnThrottleClickListener {
            onTagsClick()
        }
        listOf(
            R.id.forkRepoLayout,
            R.id.starRepoLayout,
            R.id.watchRepoLayout,
            R.id.pinLayout,
            R.id.wikiLayout,
            R.id.licenseLayout
        ).map { viewFind<View>(it)!! }.setOnThrottleClickListener {
            onClick(it)
        }
        val longClickListener = View.OnLongClickListener {
            onLongClick(it)
        }
        listOf(
            forkRepoLayout!!,
            starRepoLayout!!,
            watchRepoLayout!!
        ).forEach {
            it.setOnLongClickListener(longClickListener)
        }
        sortByUpdated!!.setOnCheckedChangeListener { _, isChecked ->
            onSortIssues(isChecked)
        }
        if (savedInstanceState == null) {
            if (intent == null || intent.extras == null) {
                finish()
                return
            }
            val extras = intent.extras
            repoId = extras!!.getString(BundleConstant.ID)
            login = extras.getString(BundleConstant.EXTRA_TWO)
            navType = extras.getInt(BundleConstant.EXTRA_TYPE)
            showWhich = extras.getInt(BundleConstant.EXTRA_THREE)
            presenter!!.onUpdatePinnedEntry(repoId!!, login!!)
        }
        presenter!!.onActivityCreate(repoId!!, login!!, navType)
        setTitle("")
        accentColor = ViewHelper.getAccentColor(this)
        iconColor = ViewHelper.getIconColor(this)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.container, DummyFragment(), "DummyFragment")
                .commit()
        }
        val myTypeface = TypeFaceHelper.typeface
        bottomNavigation!!.setDefaultTypeface(myTypeface)
        fab!!.imageTintList = ColorStateList.valueOf(Color.WHITE)
        showHideFab()
    }

    override fun onNavigationChanged(@RepoNavigationType navType: Int) {
        if (navType == RepoPagerMvp.PROFILE) {
            presenter!!.onModuleChanged(supportFragmentManager, navType)
            bottomNavigation!!.setSelectedIndex(this.navType, true)
            return
        }
        this.navType = navType
        try {
            if (bottomNavigation!!.selectedIndex != navType) bottomNavigation!!.setSelectedIndex(
                navType,
                true
            )
        } catch (e: Exception) {
            Report.reportCatchException(e)
        }
        showHideFab()
        presenter!!.onModuleChanged(supportFragmentManager, navType)
    }

    override fun onFinishActivity() {
        //do nothing here, github might return 404 if even the repo don't have anything but issues.
    }

    override fun onInitRepo() {
        hideProgress()
        if (presenter!!.repo == null) {
            return
        }
        when (showWhich) {
            1 -> onLongClick(watchRepoLayout)
            2 -> onLongClick(starRepoLayout)
            3 -> onLongClick(forkRepoLayout)
            4 -> MilestoneDialogFragment.newInstance(
                login!!, repoId!!
            )
                .show(supportFragmentManager, "MilestoneDialogFragment")
            5 -> LabelsDialogFragment.newInstance(null, repoId!!, login!!)
                .show(supportFragmentManager, "LabelsDialogFragment")
        }
        showWhich = -1
        setTaskName(presenter!!.repo!!.fullName)
        val repoModel = presenter!!.repo
        if (repoModel!!.hasProjects) {
            bottomNavigation!!.inflateMenu(R.menu.repo_with_project_bottom_nav_menu)
        }
        bottomNavigation!!.menuItemSelectionListener = presenter
        if (repoModel.topics != null && !repoModel.topics!!.isEmpty()) {
            tagsIcon!!.visibility = View.VISIBLE
            topicsList!!.adapter = TopicsAdapter(repoModel.topics!!.filterNotNull().toMutableList())
        } else {
            topicsList!!.visibility = View.GONE
        }
        onRepoPinned(PinnedReposDao.isPinned(repoModel.fullName!!).blockingGet())
        wikiLayout!!.visibility = if (repoModel.hasWiki) View.VISIBLE else View.GONE
        pinText!!.setText(R.string.pin)
        detailsIcon!!.visibility = if (isEmpty(repoModel.description)) View.GONE else View.VISIBLE
        language!!.visibility = if (isEmpty(repoModel.language)) View.GONE else View.VISIBLE
        if (!isEmpty(repoModel.language)) {
            language!!.text = repoModel.language
            language!!.setTextColor(getColorAsColor(repoModel.language!!, language!!.context))
        }
        forkRepo!!.text = numberFormat.format(repoModel.forksCount)
        starRepo!!.text = numberFormat.format(repoModel.stargazersCount)
        watchRepo!!.text = numberFormat.format(repoModel.subsCount.toLong())
        if (repoModel.owner != null) {
            avatarLayout!!.setUrl(
                repoModel.owner!!.avatarUrl, repoModel.owner!!.login,
                repoModel.owner!!.isOrganizationType, isEnterprise(repoModel.htmlUrl)
            )
        } else if (repoModel.organization != null) {
            avatarLayout!!.setUrl(
                repoModel.organization!!.avatarUrl, repoModel.organization!!.login, true,
                isEnterprise(repoModel.htmlUrl)
            )
        }
        val repoSize = if (repoModel.size > 0) repoModel.size * 1000 else repoModel.size
        date!!.text = builder()
            .append(getTimeAgo(repoModel.pushedAt))
            .append(" ,")
            .append(" ")
            .append(Formatter.formatFileSize(this, repoSize))
        size!!.visibility = View.GONE
        title!!.text = repoModel.fullName
        TextViewCompat.setTextAppearance(title!!, R.style.TextAppearance_AppCompat_Medium)
        title!!.setTextColor(ViewHelper.getPrimaryTextColor(this))
        if (repoModel.license != null) {
            licenseLayout!!.visibility = View.VISIBLE
            val licenseModel = repoModel.license!!
            license!!.text =
                if (!isEmpty(licenseModel.spdxId)) licenseModel.spdxId else licenseModel.name
        }
        invalidateOptionsMenu()
        onRepoWatched(presenter!!.isWatched)
        onRepoStarred(presenter!!.isStarred)
        onRepoForked(presenter!!.isForked)
    }

    override fun onRepoWatched(isWatched: Boolean) {
        watchRepoImage!!.tintDrawableColor(if (isWatched) accentColor else iconColor)
        onEnableDisableWatch(true)
    }

    override fun onRepoStarred(isStarred: Boolean) {
        starRepoImage!!.setImageResource(if (isStarred) R.drawable.ic_star_filled else R.drawable.ic_star)
        starRepoImage!!.tintDrawableColor(if (isStarred) accentColor else iconColor)
        onEnableDisableStar(true)
    }

    override fun onRepoForked(isForked: Boolean) {
        forkRepoImage!!.tintDrawableColor(if (isForked) accentColor else iconColor)
        onEnableDisableFork(true)
    }

    override fun onRepoPinned(isPinned: Boolean) {
        pinImage!!.setImageResource(if (isPinned) R.drawable.ic_pin_filled else R.drawable.ic_pin)
        pinLayout!!.isEnabled = true
    }

    override fun onEnableDisableWatch(isEnabled: Boolean) {
        watchRepoLayout!!.isEnabled = isEnabled
    }

    override fun onEnableDisableStar(isEnabled: Boolean) {
        starRepoLayout!!.isEnabled = isEnabled
    }

    override fun onEnableDisableFork(isEnabled: Boolean) {
        forkRepoLayout!!.isEnabled = isEnabled
    }

    override fun onChangeWatchedCount(isWatched: Boolean) {
        val count = toLong(watchRepo!!)
        watchRepo!!.text =
            numberFormat.format(if (isWatched) count + 1 else if (count > 0) count - 1 else 0)
        updatePinnedRepo()
    }

    override fun onChangeStarCount(isStarred: Boolean) {
        val count = toLong(starRepo!!)
        starRepo!!.text =
            numberFormat.format(if (isStarred) count + 1 else if (count > 0) count - 1 else 0)
        updatePinnedRepo()
    }

    override fun onChangeForkCount(isForked: Boolean) {
        val count = toLong(forkRepo!!)
        forkRepo!!.text =
            numberFormat.format(if (isForked) count + 1 else if (count > 0) count - 1 else 0)
        updatePinnedRepo()
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        userInteracted = true
    }

    override fun hasUserInteractedWithView(): Boolean {
        return userInteracted
    }

    override fun disableIssueTab() {
        showMessage(R.string.error, R.string.repo_issues_is_disabled)
        bottomNavigation!!.setMenuItemEnabled(1, false)
        bottomNavigation!!.setSelectedIndex(navType, true)
    }

    override fun openUserProfile() {
        val login = LoginDao.getUser().blockingGet().or()
        startActivity(this, login.login!!, false, PrefGetter.isEnterprise, -1)
    }

    override fun onScrolled(isUp: Boolean) {
        if (fab != null) {
            if (isUp) {
                fab!!.hide()
            } else {
                fab!!.show()
            }
        }
    }

    override val isCollaborator: Boolean
        get() {
            return presenter!!.isRepoOwner
        }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.repo_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val repoModel = presenter!!.repo
        if (repoModel != null && repoModel.fork && repoModel.parent != null) {
            val menuItem = menu.findItem(R.id.originalRepo)
            menuItem.isVisible = true
            menuItem.title = repoModel.parent!!.fullName
        }
        //        menu.findItem(R.id.deleteRepo).setVisible(getPresenter().isRepoOwner());
        if (menu.findItem(R.id.deleteRepo) != null) menu.findItem(R.id.deleteRepo).isVisible =
            false //removing delete permission.
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                launchMainActivity(this, true)
                finish()
            }
            R.id.share -> {
                if (presenter!!.repo != null) ActivityHelper.shareUrl(
                    this, presenter!!.repo!!
                        .htmlUrl!!
                )
                return true
            }
            R.id.browser -> {
                if (presenter!!.repo != null) ActivityHelper.startCustomTab(
                    this, presenter!!.repo!!
                        .htmlUrl!!
                )
                return true
            }
            R.id.copy -> {
                if (presenter!!.repo != null) AppHelper.copyToClipboard(
                    this, presenter!!.repo!!
                        .htmlUrl!!
                )
                return true
            }
            R.id.originalRepo -> {
                if (presenter!!.repo != null && presenter!!.repo!!.parent != null) {
                    val parent = presenter!!.repo!!.parent!!
                    launchUri(this, parent.htmlUrl!!)
                }
                return true
            }
            R.id.deleteRepo -> {
                newInstance(
                    getString(R.string.delete_repo), getString(R.string.delete_repo_warning),
                    Bundler.start().put(BundleConstant.EXTRA_TWO, true)
                        .put(BundleConstant.YES_NO_EXTRA, true)
                        .end()
                ).show(supportFragmentManager, MessageDialogView.TAG)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onMessageDialogActionClicked(isOk: Boolean, bundle: Bundle?) {
        super.onMessageDialogActionClicked(isOk, bundle)
        if (isOk && bundle != null) {
            val isDelete = bundle.getBoolean(BundleConstant.EXTRA_TWO)
            val fork = bundle.getBoolean(BundleConstant.EXTRA)
            if (fork) {
                if (!presenter!!.isForked) {
                    startForRepo(
                        this, presenter!!.login(), presenter!!.repoId(),
                        GithubActionService.FORK_REPO, isEnterprise
                    )
                    presenter!!.onFork()
                }
            }
            if (isDelete) presenter!!.onDeleteRepo()
        }
    }

    override fun onBackPressed() {
        if (navType == RepoPagerMvp.CODE) {
            val codePagerView = AppHelper.getFragmentByTag(
                supportFragmentManager,
                RepoCodePagerFragment.TAG
            ) as RepoCodePagerFragment?
            if (codePagerView != null) {
                if (codePagerView.canPressBack()) {
                    super.onBackPressed()
                } else {
                    codePagerView.onBackPressed()
                    return
                }
            }
        } else if (navType == RepoPagerMvp.ISSUES && filterLayout!!.isShown) {
            hideFilterLayout()
            return
        }
        super.onBackPressed()
    }

    override fun onAddSelected() {
        val pagerView = AppHelper.getFragmentByTag(
            supportFragmentManager,
            RepoIssuesPagerFragment.TAG
        ) as RepoIssuesPagerFragment?
        pagerView?.onAddIssue()
    }

    override fun onSearchSelected() {
        var isOpen = true
        val pagerView = AppHelper.getFragmentByTag(
            supportFragmentManager,
            RepoIssuesPagerFragment.TAG
        ) as RepoIssuesPagerFragment?
        if (pagerView != null) {
            isOpen = pagerView.currentItem == 0
        }
        FilterIssuesActivity.startActivity(
            this,
            presenter!!.login(),
            presenter!!.repoId(),
            true,
            isOpen,
            isEnterprise
        )
    }

    private fun showHideFab() {
        when (navType) {
            RepoPagerMvp.ISSUES -> {
                fab!!.setImageResource(R.drawable.ic_menu)
                fab!!.show()
            }
            RepoPagerMvp.PULL_REQUEST -> {
                fab!!.setImageResource(R.drawable.ic_search)
                fab!!.show()
            }
            else -> {
                fab!!.hide()
            }
        }
    }

    private fun hideFilterLayout() {
        mimicFabVisibility(false, filterLayout!!, object : OnVisibilityChangedListener() {
            override fun onHidden(actionButton: FloatingActionButton?) {
                fab!!.show()
            }
        })
    }

    private fun updatePinnedRepo() {
        presenter!!.updatePinned(
            toLong(forkRepo!!).toInt(), toLong(starRepo!!).toInt(), toLong(
                watchRepo!!
            ).toInt()
        )
    }

    companion object {
        @JvmStatic
        fun startRepoPager(context: Context, nameParser: NameParser) {
            if (!isEmpty(nameParser.name) && !isEmpty(nameParser.username)) {
                val intent = Intent(context, RepoPagerActivity::class.java)
                intent.putExtras(
                    Bundler.start()
                        .put(BundleConstant.ID, nameParser.name)
                        .put(BundleConstant.EXTRA_TWO, nameParser.username)
                        .put(BundleConstant.EXTRA_TYPE, RepoPagerMvp.CODE)
                        .put(BundleConstant.IS_ENTERPRISE, nameParser.isEnterprise)
                        .end()
                )
                context.startActivity(intent)
            }
        }

        @JvmOverloads
        fun createIntent(
            context: Context, repoId: String, login: String,
            @RepoNavigationType navType: Int = RepoPagerMvp.CODE, showWhat: Int = -1
        ): Intent {
            val intent = Intent(context, RepoPagerActivity::class.java)
            intent.putExtras(
                Bundler.start()
                    .put(BundleConstant.ID, repoId)
                    .put(BundleConstant.EXTRA_TWO, login)
                    .put(BundleConstant.EXTRA_TYPE, navType)
                    .put(BundleConstant.EXTRA_THREE, showWhat)
                    .end()
            )
            return intent
        }
    }
}