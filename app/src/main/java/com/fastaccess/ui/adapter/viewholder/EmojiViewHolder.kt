package com.fastaccess.ui.adapter.viewholder

import android.view.View
import android.view.ViewGroup
import com.fastaccess.R
import com.fastaccess.provider.emoji.Emoji
import com.fastaccess.ui.widgets.FontTextView
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder

/**
 * Created by kosh on 17/08/2017.
 */
class EmojiViewHolder private constructor(
    view: View,
    adapter: BaseRecyclerAdapter<Emoji, EmojiViewHolder, OnItemClickListener<Emoji>>
) : BaseViewHolder<Emoji>(view, adapter) {
    val emojiTextView: FontTextView = view.findViewById(R.id.emoji)

    override fun bind(t: Emoji) {
        emojiTextView.text = t.unicode
    }

    companion object {
        fun newInstance(
            parent: ViewGroup,
            adapter: BaseRecyclerAdapter<Emoji, EmojiViewHolder, OnItemClickListener<Emoji>>
        ): EmojiViewHolder {
            return EmojiViewHolder(getView(parent, R.layout.emoji_row_item), adapter)
        }
    }
}