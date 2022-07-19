package com.fastaccess.data.dao

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

/**
 * Created by Kosh on 20 Nov 2016, 10:40 AM
 */
class CommentRequestModel : Parcelable {
    var body: String? = null

    @SerializedName("in_reply_to")
    var inReplyTo: Long? = null
    var path: String? = null
    var position: Int? = null
    var line: Int? = null

    constructor() {}

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as CommentRequestModel
        return path == that.path &&
                position == that.position
    }

    override fun hashCode(): Int {
        var result = if (path != null) path.hashCode() else 0
        result = 31 * result + if (position != null) position.hashCode() else 0
        return result
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(body)
        dest.writeValue(inReplyTo)
        dest.writeString(path)
        dest.writeValue(position)
        dest.writeValue(line)
    }

    override fun toString(): String {
        return "CommentRequestModel{" +
                "body='" + body + '\'' +
                ", inReplyTo=" + inReplyTo +
                ", path='" + path + '\'' +
                ", position=" + position +
                ", line=" + line +
                '}'
    }

    private constructor(`in`: Parcel) {
        body = `in`.readString()
        inReplyTo = `in`.readValue(Long::class.java.classLoader) as Long?
        path = `in`.readString()
        position = `in`.readValue(Int::class.java.classLoader) as Int?
        line = `in`.readValue(Int::class.java.classLoader) as Int?
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<CommentRequestModel> =
            object : Parcelable.Creator<CommentRequestModel> {
                override fun createFromParcel(source: Parcel): CommentRequestModel? {
                    return CommentRequestModel(source)
                }

                override fun newArray(size: Int): Array<CommentRequestModel?> {
                    return arrayOfNulls(size)
                }
            }
    }
}