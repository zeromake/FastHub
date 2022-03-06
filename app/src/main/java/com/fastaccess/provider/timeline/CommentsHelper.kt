package com.fastaccess.provider.timeline

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.annotation.IdRes
import com.fastaccess.R
import com.fastaccess.data.dao.ReactionsModel
import com.fastaccess.data.dao.TimelineModel
import com.fastaccess.data.dao.model.Comment
import com.fastaccess.data.dao.types.ReactionTypes
import com.fastaccess.provider.tasks.git.ReactionService
import com.fastaccess.ui.widgets.SpannableBuilder.Companion.builder

/**
 * Created by Kosh on 30 Mar 2017, 6:44 PM
 */
object CommentsHelper {
    private const val LAUGH = 0x1F601
    private const val SAD = 0x1F615
    private const val THUMBS_UP = 0x1f44d
    private const val THUMBS_DOWN = 0x1f44e
    private const val HOORAY = 0x1f389
    private const val HEART = 0x2764
    @JvmStatic
    fun isOwner(currentLogin: String, repoOwner: String, commentUser: String): Boolean {
        return currentLogin.equals(repoOwner, ignoreCase = true) || currentLogin.equals(
            commentUser,
            ignoreCase = true
        )
    }

    fun handleReactions(
        context: Context, login: String, repoId: String,
        @IdRes id: Int, commentId: Long, commit: Boolean, isDelete: Boolean,
        isEnterprise: Boolean
    ) {
        var type: ReactionTypes? = null
        when (id) {
            R.id.heart -> type = ReactionTypes.HEART
            R.id.sad -> type = ReactionTypes.CONFUSED
            R.id.thumbsDown -> type = ReactionTypes.MINUS_ONE
            R.id.thumbsUp -> type = ReactionTypes.PLUS_ONE
            R.id.laugh -> type = ReactionTypes.LAUGH
            R.id.hurray -> type = ReactionTypes.HOORAY
        }
        if (type != null) {
            ReactionService.start(
                context,
                login,
                repoId,
                commentId,
                type,
                commit,
                isDelete,
                isEnterprise
            )
        }
    }

    private fun getEmojiByUnicode(unicode: Int): String {
        return String(Character.toChars(unicode))
    }

    @JvmStatic
    fun getEmoji(reactionTypes: ReactionTypes): String {
        return when (reactionTypes) {
            ReactionTypes.HEART -> heart
            ReactionTypes.HOORAY -> hooray
            ReactionTypes.PLUS_ONE -> thumbsUp
            ReactionTypes.MINUS_ONE -> thumbsDown
            ReactionTypes.CONFUSED -> sad
            ReactionTypes.LAUGH -> laugh
            else -> thumbsUp
        }
    }

    @JvmStatic
    val laugh: String
        get() = getEmojiByUnicode(LAUGH)
    @JvmStatic
    val sad: String
        get() = getEmojiByUnicode(SAD)
    @JvmStatic
    val thumbsUp: String
        get() = getEmojiByUnicode(THUMBS_UP)
    @JvmStatic
    val thumbsDown: String
        get() = getEmojiByUnicode(THUMBS_DOWN)
    @JvmStatic
    val hooray: String
        get() = getEmojiByUnicode(HOORAY)
    @JvmStatic
    val heart: String
        get() = getEmojiByUnicode(HEART)

    @JvmStatic
    fun getUsers(comments: List<Comment>): ArrayList<String> {
        return comments
            .map { comment: Comment -> comment.user.login }
            .distinct()
            .toMutableList() as ArrayList<String>
    }

    @JvmStatic
    fun getUsersByTimeline(comments: List<TimelineModel>): ArrayList<String> {
        return comments
            .filter { timelineModel: TimelineModel -> timelineModel.comment != null && timelineModel.comment!!.user != null }
            .map { comment: TimelineModel -> comment.comment!!.user.login }
            .distinct()
            .toMutableList() as ArrayList<String>
    }

    @JvmStatic
    fun appendEmojies(
        reaction: ReactionsModel, thumbsUp: TextView,
        thumbsUpReaction: TextView, thumbsDown: TextView,
        thumbsDownReaction: TextView, hurray: TextView,
        hurrayReaction: TextView, sad: TextView,
        sadReaction: TextView, laugh: TextView,
        laughReaction: TextView, heart: TextView,
        heartReaction: TextView, reactionsList: View
    ) {
        var spannableBuilder = builder()
            .append(CommentsHelper.thumbsUp).append(" ")
            .append(java.lang.String.valueOf(reaction.plusOne))
            .append("   ")
        thumbsUp.text = spannableBuilder
        thumbsUpReaction.text = spannableBuilder
        thumbsUpReaction.visibility =
            if (reaction.plusOne > 0) View.VISIBLE else View.GONE
        spannableBuilder = builder()
            .append(CommentsHelper.thumbsDown).append(" ")
            .append(java.lang.String.valueOf(reaction.minusOne))
            .append("   ")
        thumbsDown.text = spannableBuilder
        thumbsDownReaction.text = spannableBuilder
        thumbsDownReaction.visibility =
            if (reaction.minusOne > 0) View.VISIBLE else View.GONE
        spannableBuilder = builder()
            .append(hooray).append(" ")
            .append(java.lang.String.valueOf(reaction.hooray))
            .append("   ")
        hurray.text = spannableBuilder
        hurrayReaction.text = spannableBuilder
        hurrayReaction.visibility =
            if (reaction.hooray > 0) View.VISIBLE else View.GONE
        spannableBuilder = builder()
            .append(CommentsHelper.sad).append(" ")
            .append(java.lang.String.valueOf(reaction.confused))
            .append("   ")
        sad.text = spannableBuilder
        sadReaction.text = spannableBuilder
        sadReaction.visibility =
            if (reaction.confused > 0) View.VISIBLE else View.GONE
        spannableBuilder = builder()
            .append(CommentsHelper.laugh).append(" ")
            .append(java.lang.String.valueOf(reaction.laugh))
            .append("   ")
        laugh.text = spannableBuilder
        laughReaction.text = spannableBuilder
        laughReaction.visibility = if (reaction.laugh > 0) View.VISIBLE else View.GONE
        spannableBuilder = builder()
            .append(CommentsHelper.heart).append(" ")
            .append(java.lang.String.valueOf(reaction.heart))
        heart.text = spannableBuilder
        heartReaction.text = spannableBuilder
        heartReaction.visibility = if (reaction.heart > 0) View.VISIBLE else View.GONE
        if (reaction.plusOne > 0 || reaction.minusOne > 0 || reaction.laugh > 0 || reaction.hooray > 0 || reaction.confused > 0 || reaction.heart > 0) {
            reactionsList.visibility = View.VISIBLE
            reactionsList.tag = true
        } else {
            reactionsList.visibility = View.GONE
            reactionsList.tag = false
        }
    }
}