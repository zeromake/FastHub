package com.fastaccess.data.entity.converters

import com.fastaccess.data.dao.MilestoneModel
import java.lang.reflect.Type

class MilestoneConverter:BaseConverter<MilestoneModel>() {
    override val genericType: Type = genericType<MilestoneModel>()
}