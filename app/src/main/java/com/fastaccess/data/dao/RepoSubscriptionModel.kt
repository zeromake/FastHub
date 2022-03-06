package com.fastaccess.data.dao

import java.util.*

/**
 * Created by Kosh on 14 Mar 2017, 9:10 PM
 */
class RepoSubscriptionModel {
    var isSubscribed = false
    var isIgnored = false
    var createdAt: Date? = null
    var url: String? = null
    var repositoryUrl: String? = null
}