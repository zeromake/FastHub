package com.fastaccess.data.dao.types

import androidx.annotation.IdRes
import com.fastaccess.R

/**
 * Created by Kosh on 29 Mar 2017, 10:11 PM
 */
enum class ReactionTypes(
    content: String,
    @get:IdRes @param:IdRes val vId: Int,
    @param:IdRes private val secondaryViewId: Int
) {
    HEART("heart", R.id.heart, R.id.heartReaction),
    HOORAY(
        "hooray",
        R.id.hurray,
        R.id.hurrayReaction
    ),
    PLUS_ONE("thumbs_up", R.id.thumbsUp, R.id.thumbsUpReaction),
    MINUS_ONE(
        "thumbs_down",
        R.id.thumbsDown,
        R.id.thumbsDownReaction
    ),
    CONFUSED("confused", R.id.sad, R.id.sadReaction),
    LAUGH(
        "laugh",
        R.id.laugh,
        R.id.laughReaction
    ),
    ROCKET("rocket", R.id.rocket, R.id.rocketReaction),
    EYES("eyes", R.id.eyes, R.id.eyeReaction);

    var content: String = content
        set(value) {
            field = when (value) {
                "thumbs_up" -> "+1"
                "thumbs_down" -> "-1"
                else -> value
            }
        }

    fun equalsContent(c: String?): Boolean {
        c ?: return false
        if (content == c) {
            return true
        }
        val cc = when (this) {
            PLUS_ONE -> "thumbs_up"
            MINUS_ONE -> "thumbs_down"
            else -> content
        }
        return c == cc
    }

    companion object {
        @JvmStatic
        operator fun get(@IdRes vId: Int): ReactionTypes? {
            return values().asSequence()
                .filter { value: ReactionTypes? -> value != null && (value.vId == vId || value.secondaryViewId == vId) }
                .firstOrNull()
        }
    }
}