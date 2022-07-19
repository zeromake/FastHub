package com.fastaccess.data.entity

import android.os.Parcel
import android.os.Parcelable
import com.fastaccess.data.entity.converters.IssueConverter
import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
data class PinnedIssues(
    @Id
    var id: Long = 0,
    var entryCount: Int = 0,
    var login: String? = null,
    @Convert(converter = IssueConverter::class, dbType = String::class)
    var issue: Issue? = null,
    var issueId: Long = 0,
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readParcelable(Issue::class.java.classLoader),
        parcel.readLong()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeInt(entryCount)
        parcel.writeString(login)
        parcel.writeParcelable(issue, flags)
        parcel.writeLong(issueId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PinnedIssues> {
        override fun createFromParcel(parcel: Parcel): PinnedIssues {
            return PinnedIssues(parcel)
        }

        override fun newArray(size: Int): Array<PinnedIssues?> {
            return arrayOfNulls(size)
        }
    }
}
