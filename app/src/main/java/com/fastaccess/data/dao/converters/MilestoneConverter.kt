package com.fastaccess.data.dao.converters

import com.fastaccess.data.dao.MilestoneModel

/**
 * Created by Kosh on 11 Feb 2017, 11:43 PM
 */
class MilestoneConverter : BaseConverter<MilestoneModel>() {
    override val typeClass: Class<out MilestoneModel>
        get() = MilestoneModel::class.java
}