package com.fastaccess.data.dao

import android.os.Parcel
import android.os.Parcelable
import com.fastaccess.data.entity.IssueEvent
import com.fastaccess.data.entity.PullRequest

/**
 * Created by Kosh on 10 Dec 2016, 3:34 PM
 */
class PullRequestAdapterModel : Parcelable {
    var type = 0
    var issueEvent: IssueEvent? = null
    var pullRequest: PullRequest? = null

    private constructor(type: Int, model: IssueEvent?) {
        this.type = type
        issueEvent = model
    }

    constructor(type: Int, pullRequest: PullRequest?) {
        this.type = type
        this.pullRequest = pullRequest
    }

    constructor() {}

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(type)
        dest.writeParcelable(issueEvent, flags)
        dest.writeParcelable(pullRequest, flags)
    }

    private constructor(`in`: Parcel) {
        type = `in`.readInt()
        issueEvent = `in`.readParcelable(IssueEvent::class.java.classLoader)
        pullRequest = `in`.readParcelable(PullRequest::class.java.classLoader)
    }

    companion object {
        const val HEADER = 1
        private const val ROW = 2
        fun addEvents(modelList: List<IssueEvent?>?): ArrayList<PullRequestAdapterModel> {
            val models = ArrayList<PullRequestAdapterModel>()
            if (modelList == null || modelList.isEmpty()) return models
           modelList.asSequence().forEach { issueEventModel: IssueEvent? ->
                models.add(
                    PullRequestAdapterModel(
                        ROW, issueEventModel
                    )
                )
            }
            return models
        }

        @JvmField
        val CREATOR: Parcelable.Creator<PullRequestAdapterModel> =
            object : Parcelable.Creator<PullRequestAdapterModel> {
                override fun createFromParcel(source: Parcel): PullRequestAdapterModel {
                    return PullRequestAdapterModel(source)
                }

                override fun newArray(size: Int): Array<PullRequestAdapterModel?> {
                    return arrayOfNulls(size)
                }
            }
    }
}
