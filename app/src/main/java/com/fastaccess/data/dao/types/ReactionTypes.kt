package com.fastaccess.data.dao.types

import androidx.annotation.DrawableRes
import com.fastaccess.R
import androidx.annotation.IdRes

/**
 * Created by Kosh on 29 Mar 2017, 10:11 PM
 */
enum class ReactionTypes(
    val content: String,
    @get:IdRes @param:IdRes val vId: Int,
    @param:IdRes private val secondaryViewId: Int
) {
    HEART("heart", R.id.heart, R.id.heartReaction), HOORAY(
        "hooray",
        R.id.hurray,
        R.id.hurrayReaction
    ),
    PLUS_ONE("+1", R.id.thumbsUp, R.id.thumbsUpReaction), MINUS_ONE(
        "-1",
        R.id.thumbsDown,
        R.id.thumbsDownReaction
    ),
    CONFUSED("confused", R.id.sad, R.id.sadReaction), LAUGH(
        "laugh",
        R.id.laugh,
        R.id.laughReaction
    );


    companion object {
        @JvmStatic
        operator fun get(@IdRes vId: Int): ReactionTypes? {
            return values().asSequence()
                .filter { value: ReactionTypes? -> value != null && (value.vId == vId || value.secondaryViewId == vId) }
                .firstOrNull()
        }
    }
}