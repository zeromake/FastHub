package com.fastaccess.data.dao

import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.DrawableRes
import java.io.Serializable

/**
 * Created by Kosh on 27 Apr 2017, 6:10 PM
 */
open class TabsCountStateModel : Parcelable, Serializable {
    var count = 0
    var tabIndex = 0

    @DrawableRes
    var drawableId = 0
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val model = other as TabsCountStateModel
        return tabIndex == model.tabIndex
    }

    override fun hashCode(): Int {
        return tabIndex
    }

    constructor() {}

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(count)
        dest.writeInt(tabIndex)
        dest.writeInt(drawableId)
    }

    protected constructor(`in`: Parcel) {
        count = `in`.readInt()
        tabIndex = `in`.readInt()
        drawableId = `in`.readInt()
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<TabsCountStateModel> =
            object : Parcelable.Creator<TabsCountStateModel> {
                override fun createFromParcel(source: Parcel): TabsCountStateModel? {
                    return TabsCountStateModel(source)
                }

                override fun newArray(size: Int): Array<TabsCountStateModel?> {
                    return arrayOfNulls(size)
                }
            }
    }
}