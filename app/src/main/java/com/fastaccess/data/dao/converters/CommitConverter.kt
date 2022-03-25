package com.fastaccess.data.dao.converters

import com.fastaccess.data.dao.model.Commit

/**
 * Created by Kosh on 15 Mar 2017, 7:58 PM
 */
class CommitConverter : BaseConverter<Commit>() {
    override val typeClass: Class<out Commit>
        get() = Commit::class.java
}