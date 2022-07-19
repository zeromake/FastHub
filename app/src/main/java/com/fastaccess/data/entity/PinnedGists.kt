package com.fastaccess.data.entity

import android.os.Parcel
import android.os.Parcelable
import com.fastaccess.data.entity.converters.GistConverter
import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
data class PinnedGists(
    @Id
    var id: Long = 0,
    var entryCount: Int = 0,
    var login: String? = null,
    @Convert(converter = GistConverter::class, dbType = String::class)
    var gist: Gist? = null,
    var gistId: Long = 0,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readParcelable(Gist::class.java.classLoader),
        parcel.readLong()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeInt(entryCount)
        parcel.writeString(login)
        parcel.writeParcelable(gist, flags)
        parcel.writeLong(gistId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PinnedGists> {
        override fun createFromParcel(parcel: Parcel): PinnedGists {
            return PinnedGists(parcel)
        }

        override fun newArray(size: Int): Array<PinnedGists?> {
            return arrayOfNulls(size)
        }
    }
}
