package com.fastaccess.data.dao

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import android.os.Parcel
import com.fastaccess.data.dao.model.User

/**
 * Created by Kosh on 08 Dec 2016, 8:59 PM
 */
class GitCommitModel : Parcelable {
    var sha: String? = null
    var url: String? = null
    var message: String? = null
    var author: User? = null
    var committer: User? = null
    var tree: User? = null

    @SerializedName("distinct")
    var distincted = false
    var parents: GitCommitListModel? = null
    var commentCount = 0

    constructor() {}

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(sha)
        dest.writeString(url)
        dest.writeString(message)
        dest.writeParcelable(author, flags)
        dest.writeParcelable(committer, flags)
        dest.writeParcelable(tree, flags)
        dest.writeByte(if (distincted) 1.toByte() else 0.toByte())
        dest.writeList(parents)
        dest.writeInt(commentCount)
    }

    protected constructor(`in`: Parcel) {
        sha = `in`.readString()
        url = `in`.readString()
        message = `in`.readString()
        author = `in`.readParcelable(User::class.java.classLoader)
        committer = `in`.readParcelable(User::class.java.classLoader)
        tree = `in`.readParcelable(User::class.java.classLoader)
        distincted = `in`.readByte().toInt() != 0
        parents = GitCommitListModel()
        `in`.readList(parents!!, parents!!.javaClass.classLoader)
        commentCount = `in`.readInt()
    }

    override fun toString(): String {
        if (message != null) {
            return (if (sha != null && sha!!.length > 7) sha!!.substring(
                0,
                7
            ) + " - " else "") + message!!.split(
                System.lineSeparator()
            ).toTypedArray()[0]
        } else if (sha != null && sha!!.length > 10) {
            return sha!!.substring(0, 10)
        }
        return "N/A"
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<GitCommitModel> =
            object : Parcelable.Creator<GitCommitModel> {
                override fun createFromParcel(source: Parcel): GitCommitModel {
                    return GitCommitModel(source)
                }

                override fun newArray(size: Int): Array<GitCommitModel?> {
                    return arrayOfNulls(size)
                }
            }
    }
}