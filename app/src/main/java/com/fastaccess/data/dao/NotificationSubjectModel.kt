package com.fastaccess.data.dao

import android.os.Parcelable
import android.os.Parcel
import com.fastaccess.data.dao.types.NotificationType

/**
 * Created by Kosh on 19 Feb 2017, 6:11 PM
 */
open class NotificationSubjectModel : Parcelable {
    var title: String? = null
    var url: String? = null
    var type: NotificationType? = null
    var latestCommentUrl: String? = null

    constructor() {}

    override fun toString(): String {
        return "NotificationSubjectModel{" +
                ", title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", type='" + type + '\'' +
                ", latestCommentUrl='" + latestCommentUrl + '\'' +
                '}'
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(title)
        dest.writeString(url)
        dest.writeInt(if (type == null) -1 else type!!.ordinal)
        dest.writeString(latestCommentUrl)
    }

    protected constructor(`in`: Parcel) {
        title = `in`.readString()
        url = `in`.readString()
        val tmpType = `in`.readInt()
        type = if (tmpType == -1) null else NotificationType.values()[tmpType]
        latestCommentUrl = `in`.readString()
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<NotificationSubjectModel> =
            object : Parcelable.Creator<NotificationSubjectModel> {
                override fun createFromParcel(source: Parcel): NotificationSubjectModel? {
                    return NotificationSubjectModel(source)
                }

                override fun newArray(size: Int): Array<NotificationSubjectModel?> {
                    return arrayOfNulls(size)
                }
            }
    }
}