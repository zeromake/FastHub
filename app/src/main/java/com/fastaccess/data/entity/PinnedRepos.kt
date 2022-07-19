package com.fastaccess.data.entity

import android.os.Parcel
import android.os.Parcelable
import com.fastaccess.data.entity.converters.RepoConverter
import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.Unique

@Entity
data class PinnedRepos(
    @Id
    var id: Long = 0,
    @Unique
    var repoFullName: String? = null,
    @Convert(converter = RepoConverter::class, dbType = String::class)
    var pinnedRepo: Repo? = null,
    var entryCount: Int = 0,
    var login: String? = null,
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString(),
        parcel.readParcelable(Repo::class.java.classLoader),
        parcel.readInt(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(repoFullName)
        parcel.writeParcelable(pinnedRepo, flags)
        parcel.writeInt(entryCount)
        parcel.writeString(login)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PinnedRepos> {
        override fun createFromParcel(parcel: Parcel): PinnedRepos {
            return PinnedRepos(parcel)
        }

        override fun newArray(size: Int): Array<PinnedRepos?> {
            return arrayOfNulls(size)
        }
    }
}