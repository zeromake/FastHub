package com.fastaccess.data.dao.converters

import com.fastaccess.data.dao.model.PullRequest

/**
 * Created by Kosh on 15 Mar 2017, 7:58 PM
 */
class PullRequestConverter : BaseConverter<PullRequest>() {
    override val typeClass: Class<out PullRequest>
        get() = PullRequest::class.java
}