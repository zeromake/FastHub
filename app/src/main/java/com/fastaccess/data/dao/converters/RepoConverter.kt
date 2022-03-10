package com.fastaccess.data.dao.converters

import com.fastaccess.data.dao.model.Repo

/**
 * Created by Kosh on 15 Mar 2017, 7:58 PM
 */
class RepoConverter : BaseConverter<Repo>() {
    override val typeClass: Class<out Repo>
        get() = Repo::class.java
}