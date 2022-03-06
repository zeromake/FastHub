package com.fastaccess.data.dao.converters

import com.fastaccess.data.dao.GithubStatusComponentsModel

class GithubStatusComponentsConverter: BaseConverter<GithubStatusComponentsModel>() {
    override fun getTypeClass(): Class<out GithubStatusComponentsModel?> {
        return GithubStatusComponentsModel::class.java
    }
}
