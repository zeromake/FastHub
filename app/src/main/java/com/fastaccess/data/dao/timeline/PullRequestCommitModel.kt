package com.fastaccess.data.dao.timeline

import android.os.Parcel
import android.os.Parcelable
import com.fastaccess.data.dao.model.Comment

/**
 * Created by kosh on 15/08/2017.
 */
open class PullRequestCommitModel : Parcelable {
    var login: String? = null
    var path: String? = null
    var position = 0
    var commitId: String? = null
    var comments: List<Comment>? = null
    var line = 0

    constructor() {}

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(login)
        dest.writeString(path)
        dest.writeInt(position)
        dest.writeString(commitId)
        dest.writeTypedList(comments)
        dest.writeInt(line)
    }

    protected constructor(`in`: Parcel) {
        login = `in`.readString()
        path = `in`.readString()
        position = `in`.readInt()
        commitId = `in`.readString()
        comments = `in`.createTypedArrayList(Comment.CREATOR)
        line = `in`.readInt()
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<PullRequestCommitModel> =
            object : Parcelable.Creator<PullRequestCommitModel> {
                override fun createFromParcel(source: Parcel): PullRequestCommitModel {
                    return PullRequestCommitModel(source)
                }

                override fun newArray(size: Int): Array<PullRequestCommitModel?> {
                    return arrayOfNulls(size)
                }
            }
    }
}