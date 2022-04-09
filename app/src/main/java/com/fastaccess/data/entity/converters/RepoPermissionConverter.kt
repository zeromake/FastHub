package com.fastaccess.data.entity.converters

import com.fastaccess.data.dao.RepoPermissionsModel
import java.lang.reflect.Type

class RepoPermissionConverter : BaseConverter<RepoPermissionsModel>() {
    override val genericType: Type = genericType<RepoPermissionsModel>()
}