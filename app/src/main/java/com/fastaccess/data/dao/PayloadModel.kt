package com.fastaccess.data.dao

import android.os.Parcelable
import android.os.Parcel
import com.fastaccess.data.entity.*

/**
 * Created by Kosh on 08 Feb 2017, 10:03 PM
 */
open class PayloadModel : Parcelable {
    var action: String? = null
    var forkee: Repo? = null
    var issue: Issue? = null
    var pullRequest: PullRequest? = null
    var refType: String? = null
    var comment: Comment? = null
    var target: User? = null
    var member: User? = null
    var team: TeamsModel? = null
    var commitComment: Comment? = null
    var description: String? = null
    var download: ReleasesAssetsModel? = null
    var gist: Gist? = null
    var pages: List<WikiModel>? = null
    var before: String? = null
    var head: String? = null
    var ref: String? = null
    var size = 0
    var commits: List<GitCommitModel>? = null
    var user: User? = null
    var release: Release? = null
    var blockedUser: User? = null
    var organization: User? = null
    var invitation: User? = null

    constructor() {}

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(action)
        dest.writeParcelable(forkee, flags)
        dest.writeParcelable(issue, flags)
        dest.writeParcelable(pullRequest, flags)
        dest.writeString(refType)
        dest.writeParcelable(comment, flags)
        dest.writeParcelable(target, flags)
        dest.writeParcelable(member, flags)
        dest.writeParcelable(team, flags)
        dest.writeParcelable(commitComment, flags)
        dest.writeString(description)
        dest.writeParcelable(download, flags)
        dest.writeParcelable(gist, flags)
        dest.writeTypedList(pages)
        dest.writeString(before)
        dest.writeString(head)
        dest.writeString(ref)
        dest.writeInt(size)
        dest.writeTypedList(commits)
        dest.writeParcelable(user, flags)
        dest.writeParcelable(release, flags)
        dest.writeParcelable(blockedUser, flags)
        dest.writeParcelable(organization, flags)
        dest.writeParcelable(invitation, flags)
    }

    protected constructor(`in`: Parcel) {
        action = `in`.readString()
        forkee = `in`.readParcelable(Repo::class.java.classLoader)
        issue = `in`.readParcelable(Issue::class.java.classLoader)
        pullRequest = `in`.readParcelable(PullRequest::class.java.classLoader)
        refType = `in`.readString()
        comment = `in`.readParcelable(Comment::class.java.classLoader)
        target = `in`.readParcelable(User::class.java.classLoader)
        member = `in`.readParcelable(User::class.java.classLoader)
        team = `in`.readParcelable(TeamsModel::class.java.classLoader)
        commitComment = `in`.readParcelable(Comment::class.java.classLoader)
        description = `in`.readString()
        download = `in`.readParcelable(ReleasesAssetsModel::class.java.classLoader)
        gist = `in`.readParcelable(Gist::class.java.classLoader)
        pages = `in`.createTypedArrayList(WikiModel.CREATOR)
        before = `in`.readString()
        head = `in`.readString()
        ref = `in`.readString()
        size = `in`.readInt()
        commits = `in`.createTypedArrayList(GitCommitModel.CREATOR)
        user = `in`.readParcelable(User::class.java.classLoader)
        release = `in`.readParcelable(Release::class.java.classLoader)
        blockedUser = `in`.readParcelable(User::class.java.classLoader)
        organization = `in`.readParcelable(User::class.java.classLoader)
        invitation = `in`.readParcelable(User::class.java.classLoader)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<PayloadModel> = object : Parcelable.Creator<PayloadModel> {
            override fun createFromParcel(source: Parcel): PayloadModel {
                return PayloadModel(source)
            }

            override fun newArray(size: Int): Array<PayloadModel?> {
                return arrayOfNulls(size)
            }
        }
    }
}