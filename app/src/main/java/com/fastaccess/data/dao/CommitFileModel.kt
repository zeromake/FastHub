package com.fastaccess.data.dao

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by Kosh on 01 Jan 2017, 9:00 PM
 */
open class CommitFileModel protected constructor(`in`: Parcel) : Parcelable {
    var sha: String? = `in`.readString()
    var filename: String? = `in`.readString()
    var status: String? = `in`.readString()
    var additions: Int = `in`.readInt()
    var deletions: Int = `in`.readInt()
    var changes: Int = `in`.readInt()
    var blobUrl: String? = `in`.readString()
    var rawUrl: String? = `in`.readString()
    var contentsUrl: String? = `in`.readString()
    var patch: String? = `in`.readString()
    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(sha)
        dest.writeString(filename)
        dest.writeString(status)
        dest.writeInt(additions)
        dest.writeInt(deletions)
        dest.writeInt(changes)
        dest.writeString(blobUrl)
        dest.writeString(rawUrl)
        dest.writeString(contentsUrl)
        dest.writeString(patch)
    }

    override fun toString(): String {
        return "CommitFileModel{" +
                "sha='" + sha + '\'' +
                ", filename='" + filename + '\'' +
                ", status='" + status + '\'' +
                ", additions=" + additions +
                ", deletions=" + deletions +
                ", changes=" + changes +
                ", blobUrl='" + blobUrl + '\'' +
                ", rawUrl='" + rawUrl + '\'' +
                ", contentsUrl='" + contentsUrl + '\'' +
                ", patch='" + patch + '\'' +
                '}'
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<CommitFileModel> =
            object : Parcelable.Creator<CommitFileModel> {
                override fun createFromParcel(source: Parcel): CommitFileModel {
                    return CommitFileModel(source)
                }

                override fun newArray(size: Int): Array<CommitFileModel?> {
                    return arrayOfNulls(size)
                }
            }
    }

}