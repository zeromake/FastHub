package com.fastaccess.ui.modules.editor.emoji

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import com.fastaccess.R
import com.fastaccess.provider.emoji.Emoji
import com.fastaccess.ui.adapter.EmojiAdapter
import com.fastaccess.ui.base.BaseMvpBottomSheetDialogFragment
import com.fastaccess.ui.delegate.viewFind
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView
import com.fastaccess.ui.widgets.recyclerview.layout_manager.GridManager
import com.fastaccess.ui.widgets.recyclerview.scroll.RecyclerViewFastScroller

/**
 * Created by kosh on 17/08/2017.
 */
class EmojiBottomSheet : BaseMvpBottomSheetDialogFragment<EmojiMvp.View, EmojiPresenter>(),
    EmojiMvp.View {
    val recycler: DynamicRecyclerView by viewFind(R.id.recycler)
    val fastScroller: RecyclerViewFastScroller by viewFind(R.id.fastScroller)

    val adapter: EmojiAdapter by lazy { EmojiAdapter(this) }
    private var emojiCallback: EmojiMvp.EmojiCallback? = null

    fun onTextChange(text: Editable) {
        adapter.filter.filter(text)
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        emojiCallback = when {
            parentFragment is EmojiMvp.EmojiCallback -> parentFragment as EmojiMvp.EmojiCallback
            context is EmojiMvp.EmojiCallback -> context
            else -> throw IllegalArgumentException("${context.javaClass.simpleName} must implement EmojiMvp.EmojiCallback")
        }
    }

    override fun onDetach() {
        emojiCallback = null
        super.onDetach()
    }

    override fun fragmentLayout(): Int = R.layout.emoji_popup_layout

    override fun providePresenter(): EmojiPresenter = EmojiPresenter()

    override fun clearAdapter() {
        adapter.clear()
    }

    override fun onAddEmoji(emoji: Emoji) {
        adapter.addItem(emoji)
    }

    override fun onItemClick(position: Int, v: View?, item: Emoji) {
        emojiCallback?.onEmojiAdded(item)
        dismiss()
    }

    override fun onItemLongClick(position: Int, v: View?, item: Emoji) {}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<TextView>(R.id.editText).addTextChangedListener(
            { _, _, _, _ -> },
            { _, _, _, _ -> }
        ) {
            onTextChange(it!!)
        }
        recycler.adapter = adapter
        fastScroller.attachRecyclerView(recycler)
        presenter.onLoadEmoji()
        val gridManager = recycler.layoutManager as GridManager
        gridManager.iconSize = resources.getDimensionPixelSize(R.dimen.header_icon_zie)
    }
}