package com.fastaccess.data.dao.converters

import com.fastaccess.data.dao.GithubStatusComponentsModel

class GithubStatusComponentsConverter :
    BaseConverter<GithubStatusComponentsModel>() {
    override val typeClass: Class<out GithubStatusComponentsModel>
        get() = GithubStatusComponentsModel::class.java
}
