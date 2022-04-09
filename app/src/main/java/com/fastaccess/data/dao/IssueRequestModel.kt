package com.fastaccess.data.dao

import android.os.Parcel
import android.os.Parcelable
import com.fastaccess.data.entity.Issue
import com.fastaccess.data.entity.PullRequest
import com.fastaccess.data.dao.types.IssueState
import com.fastaccess.utils.ParcelUtil


/**
 * Created by Kosh on 10 Dec 2016, 8:53 AM
 */
class IssueRequestModel : Parcelable {
    private var state: IssueState? = null
    private var title: String? = null
    private var body: String? = null
    private var milestone: Int? = null
    private var assignee: String? = null
    private var labels: List<String>? = null
    private var base: String? = null
    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(state?.ordinal ?: -1)
        dest.writeString(title)
        dest.writeString(body)
        dest.writeValue(milestone)
        dest.writeString(assignee)
        dest.writeStringList(labels)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<IssueRequestModel> =
            ParcelUtil.createParcel { IssueRequestModel(it) }

        @JvmStatic
        fun clone(issue: Issue, toClose: Boolean): IssueRequestModel {
            val model = IssueRequestModel()
            if (issue.labels != null) {
                model.labels = (issue.labels!!.filter {
                    it.name != null
                }.map {
                    it.name!!
                })
            }
            model.assignee = (if (issue.assignee != null) issue.assignee!!.name else null)
            model.body = (issue.body)
            model.milestone = (if (issue.milestone != null) issue.milestone!!.number else null)
            model.state =
                (if (toClose) if (issue.state == IssueState.closed) IssueState.open else IssueState.closed else issue.state)
            model.title = (issue.title)
            return model
        }

        @JvmStatic
        fun clone(issue: PullRequest, toClose: Boolean): IssueRequestModel {
            val model = IssueRequestModel()
            if (issue.labels != null) {
                model.labels = (issue.labels!!.filter {
                    it.name != null
                }.map {
                    it.name!!
                })
            }
            model.base = (if (issue.base != null) issue.base!!.ref else "master")
            model.assignee = (if (issue.assignee != null) issue.assignee!!.name else null)
            model.body = (issue.body)
            model.milestone = (if (issue.milestone != null) issue.milestone!!.number else null)
            model.state =
                (if (toClose) if (issue.state == IssueState.closed) IssueState.open else IssueState.closed else issue.state)
            model.title = (issue.title)
            return model
        }
    }

    private constructor() {}
    private constructor(`in`: Parcel) {
        val tmpState = `in`.readInt()
        state = if (tmpState == -1) null else IssueState.values()[tmpState]
        title = `in`.readString()
        body = `in`.readString()
        milestone = `in`.readValue(Int::class.java.classLoader) as Int?
        assignee = `in`.readString()
        labels = `in`.createStringArrayList()
    }
}