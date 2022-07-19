package com.fastaccess.data.dao

import com.google.gson.annotations.SerializedName

/**
 * Created by Kosh on 05 Mar 2017, 2:30 AM
 */
class CreateMilestoneModel {
    var title: String? = null
    var description: String? = null

    @SerializedName("due_on")
    var dueOn: String? = null
}