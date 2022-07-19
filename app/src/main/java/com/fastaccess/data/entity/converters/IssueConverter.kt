package com.fastaccess.data.entity.converters

import com.fastaccess.data.entity.Issue
import java.lang.reflect.Type

class IssueConverter : BaseConverter<Issue>() {
    override val genericType: Type = genericType<Issue>()
}