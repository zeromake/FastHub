package com.fastaccess.data.entity

import android.os.Parcel
import android.os.Parcelable
import com.fastaccess.helper.readAtCompat
import com.fastaccess.helper.writeAtCompat
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import java.util.*

@Entity
data class NotificationQueue(
    @Id(assignable = true)
    var notificationId: Long = 0,
    var updateAt: Date? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readAtCompat(),
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(notificationId)
        parcel.writeAtCompat(updateAt)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<NotificationQueue> {
        override fun createFromParcel(parcel: Parcel): NotificationQueue {
            return NotificationQueue(parcel)
        }

        override fun newArray(size: Int): Array<NotificationQueue?> {
            return arrayOfNulls(size)
        }
    }
}
