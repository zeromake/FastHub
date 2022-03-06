package com.fastaccess.data.dao.types

import androidx.annotation.StringRes
import androidx.annotation.DrawableRes
import com.fastaccess.R
import java.util.*

/**
 * Created by Kosh on 10 Apr 2017, 4:27 PM
 */
enum class ReviewStateType(
    @get:StringRes @param:StringRes val stringRes: Int = R.string.reviewed,
    @get:DrawableRes @param:DrawableRes val drawableRes: Int = R.drawable.ic_eye
) {
    COMMENTED(R.string.reviewed, R.drawable.ic_eye), CHANGES_REQUESTED(
        R.string.request_changes,
        R.drawable.ic_clear
    ),
    REQUEST_CHANGES(R.string.reviewed, R.drawable.ic_eye), DISMISSED(
        R.string.dismissed_review,
        R.drawable.ic_clear
    ),
    APPROVED(
        R.string.approved_these_changes,
        R.drawable.ic_done
    ),
    APPROVE(R.string.approved_these_changes, R.drawable.ic_done);


    companion object {
        fun getType(state: String): ReviewStateType? {
            return values().asSequence()
                .filter { value: ReviewStateType? ->
                    value != null && value.name.lowercase(Locale.getDefault())
                        .equals(state.lowercase(Locale.getDefault()), ignoreCase = true)
                }
                .firstOrNull()
        }
    }
}