package com.fastaccess.data.dao.converters

import com.fastaccess.data.dao.model.Issue

/**
 * Created by Kosh on 15 Mar 2017, 8:30 PM
 */
class IssueConverter : BaseConverter<Issue>() {
    override val typeClass: Class<out Issue>
        get() = Issue::class.java
}