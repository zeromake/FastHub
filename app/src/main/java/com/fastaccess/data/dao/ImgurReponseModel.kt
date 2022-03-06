package com.fastaccess.data.dao

import android.os.Parcelable
import android.os.Parcel

/**
 * Created by Kosh on 15 Apr 2017, 8:09 PM
 */
class ImgurReponseModel : Parcelable {
    var isSuccess = false
    var status = 0
    var data: ImgurImage? = null
    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeByte(if (isSuccess) 1.toByte() else 0.toByte())
        dest.writeInt(status)
        dest.writeParcelable(data, flags)
    }

    constructor() {}
    private constructor(`in`: Parcel) {
        isSuccess = `in`.readByte().toInt() != 0
        status = `in`.readInt()
        data = `in`.readParcelable(ImgurImage::class.java.classLoader)
    }

    class ImgurImage : Parcelable {
        var title: String? = null
        var description: String? = null
        var link: String? = null

        constructor() {}

        override fun describeContents(): Int {
            return 0
        }

        override fun writeToParcel(dest: Parcel, flags: Int) {
            dest.writeString(title)
            dest.writeString(description)
            dest.writeString(link)
        }

        private constructor(`in`: Parcel) {
            title = `in`.readString()
            description = `in`.readString()
            link = `in`.readString()
        }

        companion object {
            @JvmField
            val CREATOR: Parcelable.Creator<ImgurImage> = object : Parcelable.Creator<ImgurImage> {
                override fun createFromParcel(source: Parcel): ImgurImage {
                    return ImgurImage(source)
                }

                override fun newArray(size: Int): Array<ImgurImage?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<ImgurReponseModel> =
            object : Parcelable.Creator<ImgurReponseModel> {
                override fun createFromParcel(source: Parcel): ImgurReponseModel {
                    return ImgurReponseModel(source)
                }

                override fun newArray(size: Int): Array<ImgurReponseModel?> {
                    return arrayOfNulls(size)
                }
            }
    }
}