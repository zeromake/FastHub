package com.fastaccess.data.dao.converters

import com.fastaccess.data.dao.LabelListModel

/**
 * Created by Kosh on 11 Feb 2017, 11:43 PM
 */
class LabelsListConverter : BaseConverter<LabelListModel>() {
    override val typeClass: Class<out LabelListModel>
        get() = LabelListModel::class.java
}