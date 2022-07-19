package com.fastaccess.data.entity

import android.os.Parcel
import android.os.Parcelable
import com.fastaccess.data.entity.converters.UserConverter
import com.fastaccess.helper.readAtCompat
import com.fastaccess.helper.writeAtCompat
import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import java.util.*

@Entity
data class GitHubPackage(
    @Id(assignable = true)
    var id: Long = 0,
    @Convert(converter = UserConverter::class, dbType = String::class)
    var owner: User? = null,
    var name: String? = null,
    var package_type: String? = null,
    var version_count: Int = 0,
    var visibility: String? = null,
    var url: String? = null,
    var htmlUrl: String? = null,
    var created_at: Date? = null,
    var updated_at: Date? = null,
    var description: String? = null,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readParcelable(User::class.java.classLoader),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readAtCompat(),
        parcel.readAtCompat(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeParcelable(owner, flags)
        parcel.writeString(name)
        parcel.writeString(package_type)
        parcel.writeInt(version_count)
        parcel.writeString(visibility)
        parcel.writeString(url)
        parcel.writeString(htmlUrl)
        parcel.writeAtCompat(created_at)
        parcel.writeAtCompat(updated_at)
        parcel.writeString(description)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GitHubPackage> {
        override fun createFromParcel(parcel: Parcel): GitHubPackage {
            return GitHubPackage(parcel)
        }

        override fun newArray(size: Int): Array<GitHubPackage?> {
            return arrayOfNulls(size)
        }
    }
}
