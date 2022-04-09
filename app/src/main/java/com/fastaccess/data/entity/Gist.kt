package com.fastaccess.data.entity

import android.os.Parcel
import android.os.Parcelable
import com.fastaccess.data.dao.FilesListModel
import com.fastaccess.data.dao.GithubFileModel
import com.fastaccess.data.entity.User
import com.fastaccess.data.entity.converters.GitHubFilesConverter
import com.fastaccess.data.entity.converters.UserConverter
import com.fastaccess.helper.*
import com.fastaccess.ui.widgets.SpannableBuilder
import com.google.gson.annotations.SerializedName
import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.NameInDb
import java.util.*

@Entity
data class Gist(
    @Id(assignable = true)
    @SerializedName("nooope")
    var id: Long = 0,
    var url: String? = null,
    var forksUrl: String? = null,
    var commitsUrl: String? = null,
    var gitPullUrl: String? = null,
    var gitPushUrl: String? = null,
    var htmlUrl: String? = null,
    var publicX: Boolean = false,
    var createdAt: Date? = null,
    var updatedAt: Date? = null,
    var description: String? = null,
    var comments: Int = 0,
    var commentsUrl: String? = null,
    var truncated: Boolean = false,
    var ownerName: String? = null,
    @SerializedName("id")
    var gistId: String? = null,

    @Convert(converter = GitHubFilesConverter::class, dbType = String::class)
    var files: GithubFileModel? = null,

    @NameInDb("user_column")
    @Convert(converter = UserConverter::class, dbType = String::class)
    var user: User? = null,

    @Convert(converter = UserConverter::class, dbType = String::class)
    var owner: User? = null,
) : Parcelable {
    val generateGistId: Long
        get() = gistId.hashCode().toLong()

    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readBooleanCompat(),
        parcel.readAtCompat(),
        parcel.readAtCompat(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readBooleanCompat(),
        parcel.readString(),
        parcel.readString(),
        parcel.readSerializable() as GithubFileModel,
        parcel.readParcelable(User::class.java.classLoader),
        parcel.readParcelable(User::class.java.classLoader)
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(url)
        parcel.writeString(forksUrl)
        parcel.writeString(commitsUrl)
        parcel.writeString(gitPullUrl)
        parcel.writeString(gitPushUrl)
        parcel.writeString(htmlUrl)
        parcel.writeBooleanCompat(publicX)
        parcel.writeAtCompat(createdAt)
        parcel.writeAtCompat(updatedAt)
        parcel.writeString(description)
        parcel.writeInt(comments)
        parcel.writeString(commentsUrl)
        parcel.writeBooleanCompat(truncated)
        parcel.writeString(ownerName)
        parcel.writeString(gistId)
        parcel.writeSerializable(files)
        parcel.writeParcelable(user, flags)
        parcel.writeParcelable(owner, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Gist> {
        override fun createFromParcel(parcel: Parcel): Gist {
            return Gist(parcel)
        }

        override fun newArray(size: Int): Array<Gist?> {
            return arrayOfNulls(size)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as Gist
        return if (url != null) url == that.url else that.url == null
    }

    override fun hashCode(): Int {
        return if (url != null) url.hashCode() else 0
    }

    fun getFilesAsList(): ArrayList<FilesListModel> {
        return ArrayList(files!!.map { it.value })
    }


    fun getDisplayTitle(isFromProfile: Boolean): SpannableBuilder {
        return getDisplayTitle(isFromProfile, false)
    }

    fun getDisplayTitle(isFromProfile: Boolean, gistView: Boolean): SpannableBuilder {
        val spannableBuilder = SpannableBuilder.builder()
        var addDescription = true
        if (!isFromProfile) {
            when {
                owner != null -> {
                    spannableBuilder.bold(owner!!.login!!)
                }
                user != null -> {
                    spannableBuilder.bold(user!!.login!!)
                }
                else -> {
                    spannableBuilder.bold("Anonymous")
                }
            }
            if (!gistView) {
                val files: List<FilesListModel> = getFilesAsList()
                if (files.isNotEmpty()) {
                    val filesListModel = files[0]
                    if (!InputHelper.isEmpty(filesListModel.filename) && filesListModel.filename!!.trim { it <= ' ' }.length > 2) {
                        spannableBuilder.append(" ").append("/").append(" ")
                            .append(filesListModel.filename)
                        addDescription = false
                    }
                }
            }
        }
        if (!InputHelper.isEmpty(description) && addDescription) {
            if (!InputHelper.isEmpty(spannableBuilder.toString())) {
                spannableBuilder.append(" ").append("/").append(" ")
            }
            spannableBuilder.append(description)
        }
        if (InputHelper.isEmpty(spannableBuilder.toString())) {
            if (isFromProfile) {
                val files: List<FilesListModel> = getFilesAsList()
                if (files.isNotEmpty()) {
                    val filesListModel = files[0]
                    if (!InputHelper.isEmpty(filesListModel.filename) && filesListModel.filename!!.trim { it <= ' ' }.length > 2) {
                        spannableBuilder.append(" ")
                            .append(filesListModel.filename)
                    }
                }
            }
        }
        return spannableBuilder
    }

    fun getSize(): Long {
        val models: List<FilesListModel> = getFilesAsList()
        return models.sumOf {
            it.size!!
        }
    }
}
