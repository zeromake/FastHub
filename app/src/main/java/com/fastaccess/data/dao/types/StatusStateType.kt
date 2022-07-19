package com.fastaccess.data.dao.types

import androidx.annotation.DrawableRes
import com.fastaccess.R
import java.util.*

/**
 * Created by Kosh on 10 Apr 2017, 3:41 AM
 */
enum class StatusStateType(
    @field:DrawableRes @get:DrawableRes
    @param:DrawableRes val drawableRes: Int
) {
    failure(R.drawable.ic_issues_small), pending(R.drawable.ic_time_small), success(R.drawable.ic_check_small), error(
        R.drawable.ic_issues_small
    );

    companion object {
        fun getState(status: String?): StatusStateType {
            return values().asSequence()
                .filter { value: StatusStateType ->
                    value.name.lowercase(Locale.getDefault()).equals(status, ignoreCase = true)
                }
                .firstOrNull() ?: pending
        }
    }
}