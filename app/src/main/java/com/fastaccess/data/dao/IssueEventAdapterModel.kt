package com.fastaccess.data.dao

import android.os.Parcelable
import com.fastaccess.data.entity.IssueEvent
import android.os.Parcel
import com.fastaccess.data.entity.Issue
import java.util.ArrayList

/**
 * Created by Kosh on 10 Dec 2016, 3:34 PM
 */
class IssueEventAdapterModel : Parcelable {
    var type = 0
    var issueEvent: IssueEvent? = null
    var issueModel: Issue? = null

    private constructor(type: Int, model: IssueEvent?) {
        this.type = type
        issueEvent = model
    }

    constructor(type: Int, issueModel: Issue?) {
        this.type = type
        this.issueModel = issueModel
    }

    constructor() {}

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(type)
        dest.writeParcelable(issueEvent, flags)
        dest.writeParcelable(issueModel, flags)
    }

    private constructor(`in`: Parcel) {
        type = `in`.readInt()
        issueEvent = `in`.readParcelable(IssueEvent::class.java.classLoader)
        issueModel = `in`.readParcelable(Issue::class.java.classLoader)
    }

    companion object {
        const val HEADER = 1
        private const val ROW = 2
        fun addEvents(modelList: List<IssueEvent?>?): ArrayList<IssueEventAdapterModel> {
            val models = ArrayList<IssueEventAdapterModel>()
            if (modelList == null || modelList.isEmpty()) return models
            modelList.asSequence().forEach { issueEventModel: IssueEvent? ->
                models.add(
                    IssueEventAdapterModel(
                        ROW, issueEventModel
                    )
                )
            }
            return models
        }

        @JvmField
        val CREATOR: Parcelable.Creator<IssueEventAdapterModel> =
            object : Parcelable.Creator<IssueEventAdapterModel> {
                override fun createFromParcel(source: Parcel): IssueEventAdapterModel {
                    return IssueEventAdapterModel(source)
                }

                override fun newArray(size: Int): Array<IssueEventAdapterModel?> {
                    return arrayOfNulls(size)
                }
            }
    }
}