package com.fastaccess.data.entity.converters

import com.fastaccess.data.dao.LabelListModel
import java.lang.reflect.Type

class LabelsListConverter : BaseConverter<LabelListModel>() {
    override val genericType: Type = genericType<LabelListModel>()
}