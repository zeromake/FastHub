package com.fastaccess.data.dao.timeline

import android.os.Parcelable
import android.os.Parcel
import com.fastaccess.data.dao.model.Commit
import com.fastaccess.data.dao.model.Issue
import com.fastaccess.data.dao.model.PullRequest
import com.fastaccess.data.dao.model.Repo

/**
 * Created by kosh on 26/07/2017.
 */
class SourceModel : Parcelable {
    var type: String? = null
    var issue: Issue? = null
    var pullRequest: PullRequest? = null
    var commit: Commit? = null
    var repository: Repo? = null

    constructor() {}

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(type)
        dest.writeParcelable(issue, flags)
        dest.writeParcelable(pullRequest, flags)
        dest.writeParcelable(commit, flags)
        dest.writeParcelable(repository, flags)
    }

    private constructor(`in`: Parcel) {
        type = `in`.readString()
        issue = `in`.readParcelable(Issue::class.java.classLoader)
        pullRequest = `in`.readParcelable(PullRequest::class.java.classLoader)
        commit = `in`.readParcelable(Commit::class.java.classLoader)
        repository = `in`.readParcelable(Repo::class.java.classLoader)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<SourceModel> = object : Parcelable.Creator<SourceModel> {
            override fun createFromParcel(source: Parcel): SourceModel {
                return SourceModel(source)
            }

            override fun newArray(size: Int): Array<SourceModel?> {
                return arrayOfNulls(size)
            }
        }
    }
}