package com.fastaccess.data.entity

import android.os.Parcel
import android.os.Parcelable
import com.fastaccess.data.dao.NotificationSubjectModel
import com.fastaccess.data.dao.types.NotificationReason
import com.fastaccess.data.entity.converters.NotificationSubjectConverter
import com.fastaccess.data.entity.converters.RepoConverter
import com.fastaccess.helper.*
import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.converter.PropertyConverter
import java.util.*

@Entity
data class Notification(
    @Id(assignable = true)
    var id: Long = 0,
    @Convert(converter = RepoConverter::class, dbType = String::class)
    var repository: Repo? = null,
    @Convert(converter = NotificationSubjectConverter::class, dbType = String::class)
    var subject: NotificationSubjectModel? = null,
    @Convert(converter = NotificationReasonConvert::class, dbType = Int::class)
    var reason: NotificationReason? = null,
    var url: String? = null,
    var unread: Boolean = false,
    var updatedAt: Date? = null,
    var lastReadAt: Date? = null,
    var isSubscribed: Boolean = false,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readParcelable(Repo::class.java.classLoader),
        parcel.readParcelable(NotificationSubjectModel::class.java.classLoader),
        parcel.readEnum(NotificationReason.values()),
        parcel.readString(),
        parcel.readBooleanCompat(),
        parcel.readAtCompat(),
        parcel.readAtCompat(),
        parcel.readBooleanCompat()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeParcelable(repository, flags)
        parcel.writeParcelable(subject, flags)
        parcel.writeEnum(reason)
        parcel.writeString(url)
        parcel.writeBooleanCompat(unread)
        parcel.writeAtCompat(updatedAt)
        parcel.writeAtCompat(lastReadAt)
        parcel.writeBooleanCompat(isSubscribed)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Notification> {
        override fun createFromParcel(parcel: Parcel): Notification {
            return Notification(parcel)
        }

        override fun newArray(size: Int): Array<Notification?> {
            return arrayOfNulls(size)
        }
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val that = o as Notification
        return repository != null && that.repository != null && repository!!.id == that.repository!!.id
    }

    override fun hashCode(): Int {
        return if (repository != null) repository!!.id.toInt() else 0
    }

    class NotificationReasonConvert : PropertyConverter<NotificationReason?, Int?> {
        override fun convertToEntityProperty(databaseValue: Int?): NotificationReason? {
            if (databaseValue == null || databaseValue == -1) {
                return null
            }
            return NotificationReason.values()[databaseValue]
        }

        override fun convertToDatabaseValue(entityProperty: NotificationReason?): Int {
            return entityProperty?.ordinal ?: -1
        }
    }
}
