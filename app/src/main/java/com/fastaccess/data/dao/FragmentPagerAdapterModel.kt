package com.fastaccess.data.dao

import android.content.Context
import androidx.fragment.app.Fragment
import com.fastaccess.ui.modules.profile.overview.ProfileOverviewFragment.Companion.newInstance
import com.fastaccess.ui.modules.theme.fragment.ThemeFragment.Companion.newInstance
import com.fastaccess.ui.modules.repos.extras.branches.BranchesFragment.Companion.newInstance
import com.fastaccess.ui.modules.repos.projects.list.RepoProjectFragment.Companion.newInstance
import com.fastaccess.ui.modules.repos.projects.columns.ProjectColumnFragment.Companion.newInstance
import com.fastaccess.R
import com.fastaccess.ui.modules.feeds.FeedsFragment
import com.fastaccess.ui.modules.profile.repos.ProfileReposFragment
import com.fastaccess.ui.modules.profile.starred.ProfileStarredFragment
import com.fastaccess.ui.modules.profile.gists.ProfileGistsFragment
import com.fastaccess.ui.modules.profile.followers.ProfileFollowersFragment
import com.fastaccess.ui.modules.profile.following.ProfileFollowingFragment
import com.fastaccess.ui.modules.repos.code.prettifier.ViewerFragment
import com.fastaccess.ui.modules.repos.code.files.paths.RepoFilePathFragment
import com.fastaccess.ui.modules.repos.code.commit.RepoCommitsFragment
import com.fastaccess.ui.modules.repos.code.releases.RepoReleasesFragment
import com.fastaccess.ui.modules.repos.code.contributors.RepoContributorsFragment
import com.fastaccess.ui.modules.search.repos.SearchReposFragment
import com.fastaccess.ui.modules.search.users.SearchUsersFragment
import com.fastaccess.ui.modules.search.issues.SearchIssuesFragment
import com.fastaccess.ui.modules.search.code.SearchCodeFragment
import com.fastaccess.ui.modules.repos.issues.issue.details.timeline.IssueTimelineFragment
import com.fastaccess.ui.modules.repos.pull_requests.pull_request.details.timeline.timeline.PullRequestTimelineFragment
import com.fastaccess.ui.modules.repos.pull_requests.pull_request.details.commits.PullRequestCommitsFragment
import com.fastaccess.ui.modules.repos.pull_requests.pull_request.details.files.PullRequestFilesFragment
import com.fastaccess.ui.modules.repos.issues.issue.RepoOpenedIssuesFragment
import com.fastaccess.ui.modules.repos.issues.issue.RepoClosedIssuesFragment
import com.fastaccess.ui.modules.repos.pull_requests.pull_request.RepoPullRequestFragment
import com.fastaccess.ui.modules.repos.code.commit.details.files.CommitFilesFragment
import com.fastaccess.ui.modules.repos.code.commit.details.comments.CommitCommentsFragment
import com.fastaccess.ui.modules.gists.gist.files.GistFilesListFragment
import com.fastaccess.ui.modules.gists.gist.comments.GistCommentsFragment
import com.fastaccess.ui.modules.notification.unread.UnreadNotificationsFragment
import com.fastaccess.ui.modules.notification.all.AllNotificationsFragment
import com.fastaccess.ui.modules.notification.fasthub.FastHubNotificationsFragment
import com.fastaccess.data.dao.model.Login
import com.fastaccess.ui.modules.gists.starred.StarredGistsFragment
import com.fastaccess.ui.modules.gists.GistsFragment
import com.fastaccess.ui.modules.main.issues.MyIssuesFragment
import com.fastaccess.data.dao.types.MyIssuesType
import com.fastaccess.ui.modules.main.pullrequests.MyPullRequestFragment
import com.fastaccess.ui.modules.profile.org.OrgProfileOverviewFragment
import com.fastaccess.ui.modules.profile.org.repos.OrgReposFragment
import com.fastaccess.ui.modules.profile.org.members.OrgMembersFragment
import com.fastaccess.ui.modules.profile.org.teams.OrgTeamFragment
import com.fastaccess.ui.modules.profile.org.teams.details.members.TeamMembersFragment
import com.fastaccess.ui.modules.profile.org.teams.details.repos.TeamReposFragment
import com.fastaccess.data.dao.model.Commit
import com.fastaccess.data.dao.model.Gist
import com.fastaccess.data.dao.model.PullRequest
import com.fastaccess.data.dao.types.IssueState
import com.fastaccess.ui.modules.pinned.repo.PinnedReposFragment
import com.fastaccess.ui.modules.pinned.issue.PinnedIssueFragment
import com.fastaccess.ui.modules.pinned.pullrequest.PinnedPullRequestFragment
import com.fastaccess.ui.modules.pinned.gist.PinnedGistFragment
import com.fastaccess.ui.modules.main.drawer.MainDrawerFragment
import com.fastaccess.ui.modules.main.drawer.AccountDrawerFragment

/**
 * Created by Kosh on 03 Dec 2016, 9:26 AM
 */
class FragmentPagerAdapterModel(var title: String, var fragment: Fragment?, var key: String?) {

    private constructor(title: String, fragment: Fragment?) : this(title, fragment, null) {}

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as FragmentPagerAdapterModel
        return key == that.key
    }

    override fun hashCode(): Int {
        return if (key != null) key.hashCode() else 0
    }

    companion object {
        @JvmStatic
        fun buildForProfile(context: Context, login: String): List<FragmentPagerAdapterModel> {
            return listOf(
                FragmentPagerAdapterModel(context.getString(R.string.overview), newInstance(login)),
                FragmentPagerAdapterModel(
                    context.getString(R.string.feed),
                    FeedsFragment.newInstance(login, false)
                ),
                FragmentPagerAdapterModel(
                    context.getString(R.string.repos),
                    ProfileReposFragment.newInstance(login)
                ),
                FragmentPagerAdapterModel(
                    context.getString(R.string.starred),
                    ProfileStarredFragment.newInstance(login)
                ),
                FragmentPagerAdapterModel(
                    context.getString(R.string.gists),
                    ProfileGistsFragment.newInstance(login)
                ),
                FragmentPagerAdapterModel(
                    context.getString(R.string.followers),
                    ProfileFollowersFragment.newInstance(login)
                ),
                FragmentPagerAdapterModel(
                    context.getString(R.string.following),
                    ProfileFollowingFragment.newInstance(login)
                )
            )
        }

        @JvmStatic
        fun buildForRepoCode(
            context: Context, repoId: String,
            login: String, url: String,
            defaultBranch: String,
            htmlUrl: String
        ): List<FragmentPagerAdapterModel> {
            return listOf(
                FragmentPagerAdapterModel(
                    context.getString(R.string.readme),
                    ViewerFragment.newInstance(url, htmlUrl, true, defaultBranch)
                ),
                FragmentPagerAdapterModel(
                    context.getString(R.string.files), RepoFilePathFragment.newInstance(
                        login, repoId, null,
                        defaultBranch
                    )
                ),
                FragmentPagerAdapterModel(
                    context.getString(R.string.commits),
                    RepoCommitsFragment.newInstance(repoId, login, defaultBranch)
                ),
                FragmentPagerAdapterModel(
                    context.getString(R.string.releases),
                    RepoReleasesFragment.newInstance(repoId, login)
                ),
                FragmentPagerAdapterModel(
                    context.getString(R.string.contributors),
                    RepoContributorsFragment.newInstance(repoId, login)
                )
            )
        }

        @JvmStatic
        fun buildForSearch(context: Context): List<FragmentPagerAdapterModel> {
            return listOf(
                FragmentPagerAdapterModel(
                    context.getString(R.string.repos),
                    SearchReposFragment.newInstance()
                ),
                FragmentPagerAdapterModel(
                    context.getString(R.string.users),
                    SearchUsersFragment.newInstance()
                ),
                FragmentPagerAdapterModel(
                    context.getString(R.string.issues),
                    SearchIssuesFragment.newInstance()
                ),
                FragmentPagerAdapterModel(
                    context.getString(R.string.code),
                    SearchCodeFragment.newInstance()
                )
            )
        }

        @JvmStatic
        fun buildForIssues(context: Context, commentId: Long): List<FragmentPagerAdapterModel> {
            return listOf(
                FragmentPagerAdapterModel(
                    context.getString(R.string.details),
                    IssueTimelineFragment.newInstance(commentId)
                )
            )
        }

        @JvmStatic
        fun buildForPullRequest(
            context: Context,
            pullRequest: PullRequest
        ): List<FragmentPagerAdapterModel> {
            val login = pullRequest.login
            val repoId = pullRequest.repoId
            val number = pullRequest.number
            return listOf(
                FragmentPagerAdapterModel(
                    context.getString(R.string.details),
                    PullRequestTimelineFragment.newInstance()
                ),
                FragmentPagerAdapterModel(
                    context.getString(R.string.commits),
                    PullRequestCommitsFragment.newInstance(repoId, login, number.toLong())
                ),
                FragmentPagerAdapterModel(
                    context.getString(R.string.files),
                    PullRequestFilesFragment.newInstance(repoId, login, number.toLong())
                )
            )
        }

        @JvmStatic
        fun buildForRepoIssue(
            context: Context, login: String,
            repoId: String
        ): List<FragmentPagerAdapterModel> {
            return listOf(
                FragmentPagerAdapterModel(
                    context.getString(R.string.opened),
                    RepoOpenedIssuesFragment.newInstance(repoId, login)
                ),
                FragmentPagerAdapterModel(
                    context.getString(R.string.closed),
                    RepoClosedIssuesFragment.newInstance(repoId, login)
                )
            )
        }

        @JvmStatic
        fun buildForRepoPullRequest(
            context: Context, login: String,
            repoId: String
        ): List<FragmentPagerAdapterModel> {
            return listOf(
                FragmentPagerAdapterModel(
                    context.getString(R.string.opened),
                    RepoPullRequestFragment.newInstance(repoId, login, IssueState.open)
                ),
                FragmentPagerAdapterModel(
                    context.getString(R.string.closed),
                    RepoPullRequestFragment.newInstance(repoId, login, IssueState.closed)
                )
            )
        }

        @JvmStatic
        fun buildForCommit(context: Context, commitModel: Commit): List<FragmentPagerAdapterModel> {
            val login = commitModel.login
            val repoId = commitModel.repoId
            val sha = commitModel.sha
            return listOf(
                FragmentPagerAdapterModel(
                    context.getString(R.string.files),
                    CommitFilesFragment.newInstance(commitModel.sha, commitModel.files)
                ),
                FragmentPagerAdapterModel(
                    context.getString(R.string.comments),
                    CommitCommentsFragment.newInstance(login, repoId, sha)
                )
            )
        }

        @JvmStatic
        fun buildForGist(context: Context, gistsModel: Gist): List<FragmentPagerAdapterModel> {
            return listOf(
                FragmentPagerAdapterModel(
                    context.getString(R.string.files), GistFilesListFragment.newInstance(
                        gistsModel
                            .filesAsList, false
                    )
                ),
                FragmentPagerAdapterModel(
                    context.getString(R.string.comments),
                    GistCommentsFragment.newInstance(gistsModel.gistId)
                )
            )
        }

        @JvmStatic
        fun buildForNotifications(context: Context): List<FragmentPagerAdapterModel> {
            return listOf(
                FragmentPagerAdapterModel(
                    context.getString(R.string.unread),
                    UnreadNotificationsFragment()
                ),
                FragmentPagerAdapterModel(
                    context.getString(R.string.all),
                    AllNotificationsFragment.newInstance()
                ),
                FragmentPagerAdapterModel(
                    context.getString(R.string.app_name),
                    FastHubNotificationsFragment()
                )
            )
        }

        @JvmStatic
        fun buildForGists(context: Context): List<FragmentPagerAdapterModel> {
            return listOf(
                FragmentPagerAdapterModel(
                    context.getString(R.string.my_gists), ProfileGistsFragment
                        .newInstance(Login.getUser().login)
                ),
                FragmentPagerAdapterModel(
                    context.getString(R.string.starred),
                    StarredGistsFragment.newInstance()
                ),
                FragmentPagerAdapterModel(
                    context.getString(R.string.public_gists),
                    GistsFragment.newInstance()
                )
            )
        }

        @JvmStatic
        fun buildForMyIssues(context: Context): List<FragmentPagerAdapterModel> {
            return listOf(
                FragmentPagerAdapterModel(
                    context.getString(R.string.created),
                    MyIssuesFragment.newInstance(IssueState.open, MyIssuesType.CREATED)
                ),
                FragmentPagerAdapterModel(
                    context.getString(R.string.assigned),
                    MyIssuesFragment.newInstance(IssueState.open, MyIssuesType.ASSIGNED)
                ),
                FragmentPagerAdapterModel(
                    context.getString(R.string.mentioned),
                    MyIssuesFragment.newInstance(IssueState.open, MyIssuesType.MENTIONED)
                ),
                FragmentPagerAdapterModel(
                    context.getString(R.string.participated),
                    MyIssuesFragment.newInstance(IssueState.open, MyIssuesType.PARTICIPATED)
                )
            )
        }

        @JvmStatic
        fun buildForMyPulls(context: Context): List<FragmentPagerAdapterModel> {
            return listOf(
                FragmentPagerAdapterModel(
                    context.getString(R.string.created),
                    MyPullRequestFragment.newInstance(IssueState.open, MyIssuesType.CREATED)
                ),
                FragmentPagerAdapterModel(
                    context.getString(R.string.assigned),
                    MyPullRequestFragment.newInstance(IssueState.open, MyIssuesType.ASSIGNED)
                ),
                FragmentPagerAdapterModel(
                    context.getString(R.string.mentioned),
                    MyPullRequestFragment.newInstance(IssueState.open, MyIssuesType.MENTIONED)
                ),
                FragmentPagerAdapterModel(
                    context.getString(R.string.review_requests),
                    MyPullRequestFragment.newInstance(IssueState.open, MyIssuesType.REVIEW)
                )
            )
        }

        @JvmStatic
        fun buildForOrg(
            context: Context,
            login: String,
            isMember: Boolean
        ): List<FragmentPagerAdapterModel> {
            return listOf(
                FragmentPagerAdapterModel(
                    context.getString(R.string.feeds),
                    if (isMember) FeedsFragment.newInstance(login, true) else null
                ),
                FragmentPagerAdapterModel(
                    context.getString(R.string.overview),
                    OrgProfileOverviewFragment.newInstance(login)
                ),
                FragmentPagerAdapterModel(
                    context.getString(R.string.repos),
                    OrgReposFragment.newInstance(login)
                ),
                FragmentPagerAdapterModel(
                    context.getString(R.string.people),
                    OrgMembersFragment.newInstance(login)
                ),
                FragmentPagerAdapterModel(
                    context.getString(R.string.teams),
                    if (isMember) OrgTeamFragment.newInstance(login) else null
                )
            )
                .filter { fragmentPagerAdapterModel: FragmentPagerAdapterModel -> fragmentPagerAdapterModel.fragment != null }
        }

        @JvmStatic
        fun buildForTeam(context: Context, id: Long): List<FragmentPagerAdapterModel> {
            return listOf(
                FragmentPagerAdapterModel(
                    context.getString(R.string.members),
                    TeamMembersFragment.newInstance(id)
                ),
                FragmentPagerAdapterModel(
                    context.getString(R.string.repos),
                    TeamReposFragment.newInstance(id)
                )
            )
        }

        fun buildForTheme(): List<FragmentPagerAdapterModel> {
            return listOf(
                FragmentPagerAdapterModel("", newInstance(R.style.ThemeLight)),
                FragmentPagerAdapterModel("", newInstance(R.style.ThemeDark)),
                FragmentPagerAdapterModel("", newInstance(R.style.ThemeAmlod)),
                FragmentPagerAdapterModel("", newInstance(R.style.ThemeBluish)),
                // FragmentPagerAdapterModel("", newInstance(R.style.ThemeMidnight)),
            )
        }

        fun buildForBranches(
            context: Context,
            repoId: String,
            login: String
        ): List<FragmentPagerAdapterModel> {
            return sequenceOf(
                FragmentPagerAdapterModel(
                    context.getString(R.string.branches),
                    newInstance(login, repoId, true)
                ),
                FragmentPagerAdapterModel(
                    context.getString(R.string.tags),
                    newInstance(login, repoId, false)
                )
            )
                .toList()
        }

        fun buildForRepoProjects(
            context: Context, repoId: String?,
            login: String
        ): List<FragmentPagerAdapterModel> {
            return sequenceOf(
                FragmentPagerAdapterModel(
                    context.getString(R.string.open),
                    newInstance(login, repoId, IssueState.open)
                ),
                FragmentPagerAdapterModel(
                    context.getString(R.string.closed),
                    newInstance(login, repoId, IssueState.closed)
                )
            )
                .toList()
        }

        fun buildForProjectColumns(
            models: List<ProjectColumnModel>,
            isCollaborator: Boolean
        ): List<FragmentPagerAdapterModel> {
            return models.asSequence()
                .map { projectColumnModel ->
                    FragmentPagerAdapterModel(
                        "",
                        newInstance(projectColumnModel, isCollaborator),
                        projectColumnModel.id.toString()
                    )
                }
                .toList()
        }

        @JvmStatic
        fun buildForPinned(context: Context): List<FragmentPagerAdapterModel> {
            return sequenceOf(
                FragmentPagerAdapterModel(
                    context.getString(R.string.repos),
                    PinnedReposFragment.newInstance()
                ),
                FragmentPagerAdapterModel(
                    context.getString(R.string.issues),
                    PinnedIssueFragment.newInstance()
                ),
                FragmentPagerAdapterModel(
                    context.getString(R.string.pull_requests),
                    PinnedPullRequestFragment.newInstance()
                ),
                FragmentPagerAdapterModel(
                    context.getString(R.string.gists),
                    PinnedGistFragment.newInstance()
                )
            )
                .toList()
        }

        fun buildForDrawer(
            context: Context,
        ): List<FragmentPagerAdapterModel> {
            return sequenceOf(
                FragmentPagerAdapterModel(
                    context.getString(R.string.menu_label),
                    MainDrawerFragment()
                ),
                FragmentPagerAdapterModel(
                    context.getString(R.string.profile),
                    AccountDrawerFragment()
                )
            )
                .toList()
        }
    }
}