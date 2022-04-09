package com.fastaccess.data.entity

import android.os.Parcel
import android.os.Parcelable
import com.fastaccess.data.dao.ReleasesAssetsListModel
import com.fastaccess.data.entity.User
import com.fastaccess.data.entity.converters.ReleasesAssetsConverter
import com.fastaccess.data.entity.converters.UserConverter
import com.fastaccess.helper.readAtCompat
import com.fastaccess.helper.readBooleanCompat
import com.fastaccess.helper.writeAtCompat
import com.fastaccess.helper.writeBooleanCompat
import com.google.gson.annotations.SerializedName
import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import java.util.*

@Entity
data class Release(
    @Id(assignable = true)
    var id: Long = 0,
    var url: String? = null,
    var htmlUrl: String? = null,
    var assetsUrl: String? = null,
    var uploadUrl: String? = null,
    var tagName: String? = null,
    var targetCommitish: String? = null,
    var name: String? = null,
    var draft: Boolean = false,
    var preRelease: Boolean = false,
    var createdAt: Date? = null,
    var publishedAt: Date? = null,
    var repoId: String? = null,
    var login: String? = null,
    @SerializedName("tarball_url")
    var tarballUrl: String? = null,

    @SerializedName("body_html")
    var body: String? = null,

    @SerializedName("zipball_url")
    var zipBallUrl: String? = null,

    @Convert(converter = UserConverter::class, dbType = String::class)
    var author: User? = null,

    @Convert(converter = ReleasesAssetsConverter::class, dbType = String::class)
    var assets: ReleasesAssetsListModel? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readBooleanCompat(),
        parcel.readBooleanCompat(),
        parcel.readAtCompat(),
        parcel.readAtCompat(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readParcelable(User::class.java.classLoader),
        fun(): ReleasesAssetsListModel {
            val assets = ReleasesAssetsListModel()
            parcel.readList(assets, ReleasesAssetsListModel::class.java.classLoader)
            return assets
        }(),
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(url)
        parcel.writeString(htmlUrl)
        parcel.writeString(assetsUrl)
        parcel.writeString(uploadUrl)
        parcel.writeString(tagName)
        parcel.writeString(targetCommitish)
        parcel.writeString(name)
        parcel.writeBooleanCompat(draft)
        parcel.writeBooleanCompat(preRelease)
        parcel.writeAtCompat(createdAt)
        parcel.writeAtCompat(publishedAt)
        parcel.writeString(repoId)
        parcel.writeString(login)
        parcel.writeString(tarballUrl)
        parcel.writeString(body)
        parcel.writeString(zipBallUrl)
        parcel.writeParcelable(author, flags)
        parcel.writeList(assets)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Release> {
        override fun createFromParcel(parcel: Parcel): Release {
            return Release(parcel)
        }

        override fun newArray(size: Int): Array<Release?> {
            return arrayOfNulls(size)
        }
    }
}
