package com.fastaccess.data.entity

import android.os.Parcel
import android.os.Parcelable
import com.fastaccess.data.entity.converters.NotificationTypeConverter
import com.fastaccess.helper.readAtCompat
import com.fastaccess.helper.readEnum
import com.fastaccess.helper.writeBooleanCompat
import com.fastaccess.helper.writeEnum
import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.NameInDb
import java.util.*

@Entity
data class FastHubNotification(
    @Id
    var id: Long = 0,
    @NameInDb("notification_date")
    var date: Date? = null,
    var read: Boolean = false,
    var body: String? = null,
    var title: String? = null,
    @Convert(converter = NotificationTypeConverter::class, dbType = Int::class)
    var type: NotificationType? = null,
) : Parcelable {
    enum class NotificationType {
        UPDATE, GUIDE, PURCHASE, REPORT_ISSUE, PROMOTION, STAR_REPO
    }

    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readAtCompat(),
        parcel.readByte() != 0.toByte(),
        parcel.readString(),
        parcel.readString(),
        parcel.readEnum(NotificationType.values())
    )

    override fun toString(): String {
        return "FastHubNotification{" +
                "date=${date}" +
                ", isRead=${read}" +
                ", body='${body}'" +
                ", title='$title'" +
                ", type=$type" +
                '}'
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeBooleanCompat(read)
        parcel.writeString(body)
        parcel.writeString(title)
        parcel.writeEnum(type)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FastHubNotification> {
        override fun createFromParcel(parcel: Parcel): FastHubNotification {
            return FastHubNotification(parcel)
        }

        override fun newArray(size: Int): Array<FastHubNotification?> {
            return arrayOfNulls(size)
        }
    }
}