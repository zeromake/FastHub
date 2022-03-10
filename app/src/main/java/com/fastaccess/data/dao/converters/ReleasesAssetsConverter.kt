package com.fastaccess.data.dao.converters

import com.fastaccess.data.dao.ReleasesAssetsListModel

/**
 * Created by Kosh on 11 Feb 2017, 11:43 PM
 */
class ReleasesAssetsConverter : BaseConverter<ReleasesAssetsListModel>() {
    override val typeClass: Class<out ReleasesAssetsListModel>
        get() = ReleasesAssetsListModel::class.java
}