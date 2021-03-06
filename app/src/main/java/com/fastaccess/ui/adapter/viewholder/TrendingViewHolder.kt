package com.fastaccess.ui.adapter.viewholder

import android.view.View
import com.fastaccess.R
import com.fastaccess.data.dao.TrendingModel
import com.fastaccess.helper.Logger
import com.fastaccess.provider.colors.ColorsProvider
import com.fastaccess.provider.emoji.EmojiParser
import com.fastaccess.ui.base.adapter.BaseRecyclerAdapter
import com.fastaccess.ui.base.adapter.BaseViewHolder
import com.fastaccess.ui.widgets.FontTextView

/**
 * Created: FontTextView by Kosh on 02 Jun 2017, 1:27 PM
 */

open class TrendingViewHolder(
    itemView: View,
    adapter: BaseRecyclerAdapter<TrendingModel, TrendingViewHolder, OnItemClickListener<TrendingModel>>
) : BaseViewHolder<TrendingModel>(itemView, adapter) {

    val title: FontTextView = itemView.findViewById(R.id.title)
    val description: FontTextView = itemView.findViewById(R.id.description)
    private val todayStars: FontTextView = itemView.findViewById(R.id.todayStars)
    val stars: FontTextView = itemView.findViewById(R.id.stars)
    val fork: FontTextView = itemView.findViewById(R.id.forks)
    val lang: FontTextView = itemView.findViewById(R.id.language)

    override fun bind(t: TrendingModel) {
        title.text = t.title
        if (t.description.isNullOrBlank()) {
            description.visibility = View.GONE
        } else {
            val descriptionValue: String = EmojiParser.parseToUnicode(t.description)
            description.text = descriptionValue
            description.visibility = View.VISIBLE
        }
        todayStars.text = t.todayStars
        stars.text = t.stars
        fork.text = t.forks
        if (t.language.isNullOrBlank()) {
            lang.visibility = View.GONE
            lang.text = ""
        } else {
            val color = ColorsProvider.getColorAsColor(t.language, itemView.context)
            Logger.e(color, t.language)
            lang.tintDrawables(color)
            lang.setTextColor(color)
            lang.text = t.language
            lang.visibility = View.VISIBLE
        }
    }

}