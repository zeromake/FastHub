package com.fastaccess.data.dao.converters

import com.fastaccess.data.dao.converters.BaseConverter
import com.fastaccess.data.dao.GithubState
import com.fastaccess.data.dao.LicenseModel

/**
 * Created by Kosh on 15 Mar 2017, 8:41 PM
 */
class GitHubStateConverter : BaseConverter<GithubState>() {
    override val typeClass: Class<out GithubState>
        get() = GithubState::class.java
}