package com.fastaccess.data.dao

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by Kosh on 24 Jun 2017, 4:15 PM
 */
class ReviewRequestModel : Parcelable {
    var commitId: String? = null
    var body: String? = null
    var event: String? = null
    var comments: List<CommentRequestModel>? = null
    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(commitId)
        dest.writeString(body)
        dest.writeString(event)
        dest.writeTypedList(comments)
    }

    private constructor(`in`: Parcel) {
        commitId = `in`.readString()
        body = `in`.readString()
        event = `in`.readString()
        comments = `in`.createTypedArrayList(CommentRequestModel.CREATOR)
    }

    constructor() {}

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<ReviewRequestModel> =
            object : Parcelable.Creator<ReviewRequestModel> {
                override fun createFromParcel(source: Parcel): ReviewRequestModel {
                    return ReviewRequestModel(source)
                }

                override fun newArray(size: Int): Array<ReviewRequestModel?> {
                    return arrayOfNulls(size)
                }
            }
    }
}