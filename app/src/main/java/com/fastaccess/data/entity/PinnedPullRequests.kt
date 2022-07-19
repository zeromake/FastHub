package com.fastaccess.data.entity

import android.os.Parcel
import android.os.Parcelable
import com.fastaccess.data.entity.converters.PullRequestConverter
import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
data class PinnedPullRequests(
    @Id
    var id: Long = 0,
    var entryCount: Int = 0,
    var login: String? = null,
    @Convert(converter = PullRequestConverter::class, dbType = String::class)
    var pullRequest: PullRequest? = null,
    var pullRequestId: Long = 0,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readParcelable(PullRequest::class.java.classLoader),
        parcel.readLong()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeInt(entryCount)
        parcel.writeString(login)
        parcel.writeParcelable(pullRequest, flags)
        parcel.writeLong(pullRequestId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PinnedPullRequests> {
        override fun createFromParcel(parcel: Parcel): PinnedPullRequests {
            return PinnedPullRequests(parcel)
        }

        override fun newArray(size: Int): Array<PinnedPullRequests?> {
            return arrayOfNulls(size)
        }
    }
}
