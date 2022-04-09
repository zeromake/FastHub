package com.fastaccess.data.entity

import android.os.Parcel
import android.os.Parcelable
import com.fastaccess.data.dao.types.FilesType
import com.fastaccess.data.entity.converters.FilesTypeConverter
import com.google.gson.annotations.SerializedName
import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.converter.PropertyConverter

@Entity
data class RepoFile(
    @Id(assignable = true)
    var id: Long = 0,
    var name: String? = null,
    var path: String? = null,
    var sha: String? = null,
    var size: Long = 0,
    var url: String? = null,
    @SerializedName("html_url")
    var htmlUrl: String? = null,

    @SerializedName("git_url")
    var gitUrl: String? = null,

    @SerializedName("download_url")
    var downloadUrl: String? = null,

    @Convert(converter = FilesTypeConverter::class, dbType = Int::class)
    var type: FilesType? = null,

    @SerializedName("repo_id")
    var repoId: String? = null,
    var login: String? = null,
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readLong(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        fun():FilesType? {
            val r = parcel.readInt()
            return if (r == -1) null else FilesType.values()[r]
        }(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(name)
        parcel.writeString(path)
        parcel.writeString(sha)
        parcel.writeLong(size)
        parcel.writeString(url)
        parcel.writeString(htmlUrl)
        parcel.writeString(gitUrl)
        parcel.writeString(downloadUrl)
        parcel.writeInt(type?.ordinal ?: -1)
        parcel.writeString(repoId)
        parcel.writeString(login)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RepoFile> {
        override fun createFromParcel(parcel: Parcel): RepoFile {
            return RepoFile(parcel)
        }

        override fun newArray(size: Int): Array<RepoFile?> {
            return arrayOfNulls(size)
        }
    }


}
