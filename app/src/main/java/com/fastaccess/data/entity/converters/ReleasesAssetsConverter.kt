package com.fastaccess.data.entity.converters

import com.fastaccess.data.dao.ReleasesAssetsListModel
import java.lang.reflect.Type

class ReleasesAssetsConverter : BaseConverter<ReleasesAssetsListModel>() {
    override val genericType: Type = genericType<ReleasesAssetsListModel>()
}