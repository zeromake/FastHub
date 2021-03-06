package com.fastaccess.ui.modules.profile.overview

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.StringRes
import androidx.cardview.widget.CardView
import androidx.transition.AutoTransition
import androidx.transition.Transition
import androidx.transition.TransitionManager
import com.evernote.android.state.State
import com.fastaccess.R
import com.fastaccess.data.entity.User
import com.fastaccess.data.entity.dao.LoginDao
import com.fastaccess.github.GetPinnedReposQuery
import com.fastaccess.helper.*
import com.fastaccess.provider.crash.Report
import com.fastaccess.provider.emoji.EmojiParser
import com.fastaccess.provider.scheme.SchemeParser
import com.fastaccess.ui.adapter.ProfileOrgsAdapter
import com.fastaccess.ui.adapter.ProfilePinnedReposAdapter
import com.fastaccess.ui.base.BaseFragment
import com.fastaccess.ui.base.adapter.BaseViewHolder
import com.fastaccess.ui.delegate.viewFind
import com.fastaccess.ui.modules.profile.ProfilePagerMvp
import com.fastaccess.ui.widgets.AvatarLayout
import com.fastaccess.ui.widgets.FontButton
import com.fastaccess.ui.widgets.FontTextView
import com.fastaccess.ui.widgets.SpannableBuilder
import com.fastaccess.ui.widgets.contributions.GitHubContributionsView
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView
import com.fastaccess.ui.widgets.recyclerview.layout_manager.GridManager
import com.fastaccess.utils.setOnThrottleClickListener

/**
 * Created by Kosh on 03 Dec 2016, 9:16 AM
 */
class ProfileOverviewFragment : BaseFragment<ProfileOverviewMvp.View, ProfileOverviewPresenter>(),
    ProfileOverviewMvp.View {

    private val contributionsCaption: FontTextView by viewFind(R.id.contributionsCaption)
    private val organizationsCaption: FontTextView by viewFind(R.id.organizationsCaption)
    private val username: FontTextView by viewFind(R.id.username)
    private val fullname: FontTextView by viewFind(R.id.fullname)
    private val description: FontTextView by viewFind(R.id.description)
    private val avatarLayout: AvatarLayout by viewFind(R.id.avatarLayout)
    private val organization: FontTextView by viewFind(R.id.organization)
    private val location: FontTextView by viewFind(R.id.location)
    private val email: FontTextView by viewFind(R.id.email)
    private val link: FontTextView by viewFind(R.id.link)
    private val twitter: FontTextView by viewFind(R.id.twitter_link)
    private val joined: FontTextView by viewFind(R.id.joined)
    private val following: FontButton by viewFind(R.id.following)
    private val followers: FontButton by viewFind(R.id.followers)
    private val progress: View by viewFind(R.id.progress)
    private val followBtn: Button by viewFind(R.id.followBtn)
    private val orgsList: DynamicRecyclerView by viewFind(R.id.orgsList)
    private val orgsCard: CardView by viewFind(R.id.orgsCard)
    private val contributionView: GitHubContributionsView? by viewFind(R.id.contributionView)
    private val contributionCard: CardView by viewFind(R.id.contributionCard)
    private val pinnedReposTextView: FontTextView? by viewFind(R.id.pinnedReposTextView)
    private val pinnedList: DynamicRecyclerView by viewFind(R.id.pinnedList)
    private val pinnedReposCard: CardView by viewFind(R.id.pinnedReposCard)

    @State
    var userModel: User? = null
    private var profileCallback: ProfilePagerMvp.View? = null

    fun onClick(view: View) {
        when (view.id) {
            R.id.followers -> {
                profileCallback!!.onNavigateToFollowers()
            }
            R.id.following -> {
                profileCallback!!.onNavigateToFollowing()
            }
            R.id.followBtn -> {
                presenter!!.onFollowButtonClicked(presenter!!.login!!)
                followBtn.isEnabled = false
            }
        }
    }

    fun onOpenAvatar() {
        if (userModel != null) ActivityHelper.startCustomTab(
            requireActivity(),
            userModel!!.avatarUrl!!
        )
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        profileCallback = if (parentFragment is ProfilePagerMvp.View) {
            parentFragment as ProfilePagerMvp.View?
        } else {
            context as ProfilePagerMvp.View
        }
    }

    override fun onDetach() {
        profileCallback = null
        super.onDetach()
    }

    override fun fragmentLayout(): Int {
        return R.layout.profile_overview_layout
    }

    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {

        listOf(
            R.id.following, R.id.followers, R.id.followBtn
        ).map { view.findViewById<View>(it) }.setOnThrottleClickListener {
            onClick(it)
        }
        view.findViewById<View>(R.id.userInformation).setOnThrottleClickListener {
            onOpenAvatar()
        }

        onInitOrgs(presenter!!.orgs)
        onInitPinnedRepos(presenter!!.nodes)
        if (savedInstanceState == null) {
            presenter!!.onFragmentCreated(arguments)
        } else {
            if (userModel != null) {
                invalidateFollowBtn()
                onInitViews(userModel)
            } else {
                presenter!!.onFragmentCreated(arguments)
            }
        }
        if (isMeOrOrganization) {
            followBtn.visibility = View.GONE
        }
    }

    override fun providePresenter(): ProfileOverviewPresenter {
        return ProfileOverviewPresenter()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onInitViews(userModel: User?) {
        progress.visibility = View.GONE
        userModel ?: return
        if (profileCallback != null) profileCallback!!.onCheckType(userModel.isOrganizationType)
        if (view != null) {
            if (this.userModel == null) {
                TransitionManager.beginDelayedTransition(
                    (view as ViewGroup?)!!,
                    AutoTransition().addListener(object : Transition.TransitionListener {
                        override fun onTransitionStart(transition: Transition) {}
                        override fun onTransitionEnd(transition: Transition) {
                            try {
                                if (contributionView != null) presenter!!.onLoadContributionWidget(
                                    contributionView!!
                                )
                            }
                            catch (e: Exception) {
                                Report.reportCatchException(e)
                            }
                        }

                        override fun onTransitionCancel(transition: Transition) {}
                        override fun onTransitionPause(transition: Transition) {}
                        override fun onTransitionResume(transition: Transition) {}
                    })
                )
            } else {
                presenter!!.onLoadContributionWidget(contributionView!!)
            }
        }
        this.userModel = userModel
        followBtn.visibility = if (!isMeOrOrganization) View.VISIBLE else View.GONE
        if (userModel.login != null) {
            username.text = userModel.login
        }
        if (userModel.name != null) {
            fullname.text = userModel.name
        }
        if (userModel.bio != null) {
            description.text = EmojiParser.parseToUnicode(userModel.bio!!)
        } else {
            description.visibility = View.GONE
        }
        avatarLayout.setUrl(
            userModel.avatarUrl, null,
            isOrg = false,
            isEnterprise = false,
            reload = true
        )
        avatarLayout.findViewById<View>(R.id.avatar)
            .setOnTouchListener { _: View?, event: MotionEvent ->
                if (event.action == MotionEvent.ACTION_UP) {
                    ActivityHelper.startCustomTab(requireActivity(), userModel.avatarUrl!!)
                    return@setOnTouchListener true
                }
                false
            }
        if (InputHelper.isEmpty(userModel.company)) {
            organization.visibility = View.GONE
        } else {
            organization.text = userModel.company
            organization.visibility = View.VISIBLE
        }
        if (InputHelper.isEmpty(userModel.location)) {
            location.visibility = View.GONE
        } else {
            location.text = userModel.location
            location.visibility = View.VISIBLE
        }
        if (InputHelper.isEmpty(userModel.email)) {
            email.visibility = View.GONE
        } else {
            email.text = userModel.email
            email.visibility = View.VISIBLE
        }
        if (InputHelper.isEmpty(userModel.blog)) {
            link.visibility = View.GONE
        } else {
            link.text = userModel.blog!!
            link.visibility = View.VISIBLE
        }
        if (InputHelper.isEmpty(userModel.twitter)) {
            twitter.visibility = View.GONE
        } else {
            twitter.text = String.format("https://twitter.com/%s", userModel.twitter!!)
            twitter.visibility = View.VISIBLE
        }
        if (InputHelper.isEmpty(userModel.createdAt)) {
            joined.visibility = View.GONE
        } else {
            joined.text = ParseDateFormat.getTimeAgo(userModel.createdAt!!)
            joined.visibility = View.VISIBLE
        }
        followers.text = SpannableBuilder.builder()
            .append(getString(R.string.followers))
            .append(" (")
            .bold(userModel.followers.toString())
            .append(")")
        following.text = SpannableBuilder.builder()
            .append(getString(R.string.following))
            .append(" (")
            .bold(userModel.following.toString())
            .append(")")
    }

    override fun invalidateFollowBtn() {
        hideProgress()
        if (isMeOrOrganization) return
        if (presenter!!.isSuccessResponse) {
            followBtn.isEnabled = true
            followBtn.isActivated = presenter!!.isFollowing
            followBtn.text =
                if (presenter!!.isFollowing) getString(R.string.unfollow) else getString(R.string.follow)
        }
    }

    override fun onInitContributions(show: Boolean) {
        if (contributionView == null) return
        if (show) {
            contributionView!!.onResponse()
        }
        contributionCard.visibility = if (show) View.VISIBLE else View.GONE
        contributionsCaption.visibility =
            if (show) View.VISIBLE else View.GONE
    }

    override fun onInitOrgs(orgs: List<User>) {
        if (orgs.isNotEmpty()) {
            orgsList.isNestedScrollingEnabled = false
            val adapter = ProfileOrgsAdapter()
            adapter.addItems(orgs)
            orgsList.adapter = adapter
            orgsCard.visibility = View.VISIBLE
            organizationsCaption.visibility = View.VISIBLE
            (orgsList.layoutManager as GridManager?)!!.iconSize =
                resources.getDimensionPixelSize(R.dimen.header_icon_zie) + resources
                    .getDimensionPixelSize(R.dimen.spacing_xs_large)
        } else {
            organizationsCaption.visibility = View.GONE
            orgsCard.visibility = View.GONE
        }
    }

    override fun onUserNotFound() {
        showMessage(R.string.error, R.string.no_user_found)
    }

    override fun onInitPinnedRepos(nodes: List<GetPinnedReposQuery.Node>) {
        if (pinnedReposTextView == null) return
        if (nodes.isNotEmpty()) {
            pinnedReposTextView!!.visibility = View.VISIBLE
            pinnedReposCard.visibility = View.VISIBLE
            val adapter = ProfilePinnedReposAdapter(nodes.toMutableList())
            adapter.listener =
                object : BaseViewHolder.OnItemClickListener<GetPinnedReposQuery.Node> {
                    override fun onItemClick(
                        position: Int,
                        v: View?,
                        item: GetPinnedReposQuery.Node
                    ) {
                        SchemeParser.launchUri(requireContext(), item.onRepository!!.url.toString())
                    }

                    override fun onItemLongClick(
                        position: Int,
                        v: View?,
                        item: GetPinnedReposQuery.Node
                    ) {
                    }
                }
            pinnedList.addDivider()
            pinnedList.adapter = adapter
        } else {
            pinnedReposTextView!!.visibility = View.GONE
            pinnedReposCard.visibility = View.GONE
        }
    }

    override fun showProgress(@StringRes resId: Int) {
        progress.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        progress.visibility = View.GONE
    }

    override fun showErrorMessage(msgRes: String) {
        onHideProgress()
        super.showErrorMessage(msgRes)
    }

    override fun showMessage(titleRes: Int, msgRes: Int) {
        onHideProgress()
        super.showMessage(titleRes, msgRes)
    }

    private fun onHideProgress() {
        hideProgress()
    }

    private val isMeOrOrganization: Boolean
        get() {
            val login = LoginDao.getUser().blockingGet().get()
            return login != null && login.login.equals(
                presenter!!.login,
                ignoreCase = true
            ) ||
                    userModel != null && userModel!!.type != null && !userModel!!.type.equals(
                "user",
                ignoreCase = true
            )
        }

    companion object {
        @JvmStatic
        fun newInstance(login: String): ProfileOverviewFragment {
            val view = ProfileOverviewFragment()
            view.arguments = Bundler.start().put(BundleConstant.EXTRA, login).end()
            return view
        }
    }
}
