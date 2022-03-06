package com.fastaccess.data.dao.types

import androidx.annotation.DrawableRes
import com.fastaccess.R
import java.util.*

enum class IssueEventType(@get:DrawableRes @param:DrawableRes val iconResId: Int = R.drawable.ic_label) {
    assigned(R.drawable.ic_profile), closed(R.drawable.ic_issue_closed), commented(R.drawable.ic_comment), committed(
        R.drawable.ic_push
    ),
    demilestoned(R.drawable.ic_milestone), head_ref_deleted(R.drawable.ic_trash), head_ref_restored(
        R.drawable.ic_redo
    ),
    labeled(R.drawable.ic_label), locked(R.drawable.ic_lock), mentioned(R.drawable.ic_at), merged(R.drawable.ic_fork), milestoned(
        R.drawable.ic_milestone
    ),
    referenced(R.drawable.ic_format_quote), renamed(R.drawable.ic_edit), reopened(R.drawable.ic_issue_opened), subscribed(
        R.drawable.ic_subscribe
    ),
    unassigned(R.drawable.ic_profile), unlabeled(R.drawable.ic_label), unlocked(R.drawable.ic_unlock), unsubscribed(
        R.drawable.ic_eye_off
    ),
    review_requested(R.drawable.ic_eye), review_dismissed(R.drawable.ic_eye_off), review_request_removed(
        R.drawable.ic_eye_off
    ),
    cross_referenced(R.drawable.ic_format_quote), line_commented(R.drawable.ic_comment), commit_commented(
        R.drawable.ic_comment
    ),
    reviewed(R.drawable.ic_eye), changes_requested(R.drawable.ic_eye), added_to_project(R.drawable.ic_add), GROUPED(
        R.drawable.ic_eye
    ),
    deployed(R.drawable.ic_rocket), unknown(R.drawable.ic_bug);


    companion object {
        fun getType(type: String): IssueEventType {
            return values().asSequence()
                .filter { value: IssueEventType ->
                    value.name.lowercase(Locale.getDefault()).equals(
                        type.lowercase(Locale.getDefault())
                            .replace("-".toRegex(), "_"), ignoreCase = true
                    )
                }
                .firstOrNull() ?: unknown
        }
    }
}