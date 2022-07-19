package com.fastaccess.data.entity

import android.os.Parcel
import android.os.Parcelable
import com.fastaccess.helper.readBooleanCompat
import com.fastaccess.helper.writeBooleanCompat
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.Unique

@Entity
data class ViewerFile(
    @Id
    var id: Long = 0,
    var markdown: Boolean = false,
    var content: String? = null,
    @Unique
    var fullUrl: String? = null,
    var repo:Boolean = false
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readBooleanCompat(),
        parcel.readString(),
        parcel.readString(),
        parcel.readBooleanCompat()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeBooleanCompat(markdown)
        parcel.writeString(content)
        parcel.writeString(fullUrl)
        parcel.writeBooleanCompat(repo)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ViewerFile> {
        override fun createFromParcel(parcel: Parcel): ViewerFile {
            return ViewerFile(parcel)
        }

        override fun newArray(size: Int): Array<ViewerFile?> {
            return arrayOfNulls(size)
        }
    }
}
