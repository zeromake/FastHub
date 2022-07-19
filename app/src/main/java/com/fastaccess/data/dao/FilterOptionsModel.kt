package com.fastaccess.data.dao

import android.os.Parcel
import android.os.Parcelable
import com.fastaccess.helper.InputHelper
import java.util.*

/**
 * Serves a model for the filter in Repositories fragment
 */
class FilterOptionsModel : Parcelable {
    private var type: String? = null
    private var sort: String? = "Pushed"
    private var sortDirection: String? = "descending"
    private var queryMap: MutableMap<String, String>? = null
    private var isPersonalProfile = false
    private var typesListForPersonalProfile =
        sequenceOf("Select", "All", "Owner", "Public", "Private", "Member").toList()
    private var typesListForExternalProfile = sequenceOf("Select", "All", "Owner", "Member").toList()
    private var typesListForOrganizationProfile =
        sequenceOf("Select", "All", "Public", "Private", "Forks", "Sources", "Member").toList()
    var sortOptionList = sequenceOf("Pushed", "Created", "Updated", "Full Name").toList()
        private set
    var sortDirectionList = sequenceOf("Descending", "Ascending").toList()
        private set
    var isOrg = false

    constructor() {}

    fun setType(type: String?) {
        this.type = type
    }

    fun setSort(sort: String?) {
        this.sort = sort
    }

    fun setSortDirection(sortDirection: String?) {
        this.sortDirection = sortDirection
    }

    fun getQueryMap(): Map<String, String> {
        if (queryMap == null) {
            queryMap = HashMap()
        } else {
            queryMap!!.clear()
        }
        if (InputHelper.isEmpty(type) || "Select".equals(type, ignoreCase = true)) {
            queryMap!!.remove(TYPE)
            queryMap!![AFFILIATION] = "owner,collaborator"
        } else {
            queryMap!!.remove(AFFILIATION)
            queryMap!![TYPE] = type!!.lowercase(Locale.getDefault())
        }
        //Not supported for organization repo
        if (!isOrg) {
            if (sort!!.contains(" ")) {
                //full name should be full_name
                queryMap!![SORT] = sort!!.replace(" ", "_").lowercase(Locale.getDefault())
            } else {
                queryMap!![SORT] = sort!!.lowercase(Locale.getDefault())
            }
            if (sortDirection == sortDirectionList[0]) {
                //Descending should be desc
                queryMap!![DIRECTION] =
                    sortDirection!!.lowercase(Locale.getDefault()).substring(0, 4)
            } else {
                //Ascending should be asc
                queryMap!![DIRECTION] =
                    sortDirection!!.lowercase(Locale.getDefault()).substring(0, 3)
            }
        }
        return queryMap as MutableMap<String, String>
    }

    val selectedTypeIndex: Int
        get() = if (isPersonalProfile) {
            typesListForPersonalProfile.indexOf(type)
        } else {
            typesListForExternalProfile.indexOf(type)
        }
    val selectedSortOptionIndex: Int
        get() = sortOptionList.indexOf(sort)
    val selectedSortDirectionIndex: Int
        get() = sortDirectionList.indexOf(sortDirection)
    val typesList: List<String?>
        get() = if (isPersonalProfile) {
            typesListForPersonalProfile
        } else if (isOrg) {
            typesListForOrganizationProfile
        } else {
            typesListForExternalProfile
        }

    fun setIsPersonalProfile(isPersonalProfile: Boolean) {
        this.isPersonalProfile = isPersonalProfile
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(type)
        dest.writeString(sort)
        dest.writeString(sortDirection)
        dest.writeInt(queryMap!!.size)
        for ((key, value) in queryMap!!) {
            dest.writeString(key)
            dest.writeString(value)
        }
        dest.writeByte(if (isPersonalProfile) 1.toByte() else 0.toByte())
        dest.writeStringList(typesListForPersonalProfile)
        dest.writeStringList(typesListForExternalProfile)
        dest.writeStringList(typesListForOrganizationProfile)
        dest.writeStringList(sortOptionList)
        dest.writeStringList(sortDirectionList)
        dest.writeByte(if (isOrg) 1.toByte() else 0.toByte())
    }

    private constructor(`in`: Parcel) {
        type = `in`.readString()
        sort = `in`.readString()
        sortDirection = `in`.readString()
        val queryMapSize = `in`.readInt()
        queryMap = HashMap(queryMapSize)
        for (i in 0 until queryMapSize) {
            val key = `in`.readString()
            val value = `in`.readString()
            (queryMap as HashMap<String, String>)[key!!] = value!!
        }
        isPersonalProfile = `in`.readByte().toInt() != 0
        typesListForPersonalProfile = `in`.createStringArrayList()!!
        typesListForExternalProfile = `in`.createStringArrayList()!!
        typesListForOrganizationProfile = `in`.createStringArrayList()!!
        sortOptionList = `in`.createStringArrayList()!!
        sortDirectionList = `in`.createStringArrayList()!!
        isOrg = `in`.readByte().toInt() != 0
    }

    companion object {
        private const val TYPE = "type"
        private const val SORT = "sort"
        private const val AFFILIATION = "affiliation"
        private const val DIRECTION = "direction"
        @JvmField
        val CREATOR: Parcelable.Creator<FilterOptionsModel> =
            object : Parcelable.Creator<FilterOptionsModel> {
                override fun createFromParcel(source: Parcel): FilterOptionsModel {
                    return FilterOptionsModel(source)
                }

                override fun newArray(size: Int): Array<FilterOptionsModel?> {
                    return arrayOfNulls(size)
                }
            }
    }
}