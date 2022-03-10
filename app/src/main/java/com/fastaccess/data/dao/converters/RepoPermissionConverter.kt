package com.fastaccess.data.dao.converters

import com.fastaccess.data.dao.RepoPermissionsModel

/**
 * Created by Kosh on 15 Mar 2017, 8:33 PM
 */
class RepoPermissionConverter : BaseConverter<RepoPermissionsModel>() {
    override val typeClass: Class<out RepoPermissionsModel>
        get() = RepoPermissionsModel::class.java
}