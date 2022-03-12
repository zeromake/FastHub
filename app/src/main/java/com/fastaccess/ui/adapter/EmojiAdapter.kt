package com.fastaccess.ui.adapter

import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import com.fastaccess.provider.emoji.Emoji
import com.fastaccess.ui.adapter.viewholder.EmojiViewHolder
import com.fastaccess.ui.base.adapter.BaseRecyclerAdapter
import com.fastaccess.ui.base.adapter.BaseViewHolder
import java.util.*

/**
 * Created by kosh on 17/08/2017.
 */
class EmojiAdapter(listener: BaseViewHolder.OnItemClickListener<Emoji>)
    : BaseRecyclerAdapter<Emoji, EmojiViewHolder, BaseViewHolder.OnItemClickListener<Emoji>>(listener), Filterable {

    var copiedList = mutableListOf<Emoji>()

    override fun viewHolder(parent: ViewGroup, viewType: Int): EmojiViewHolder {
        return EmojiViewHolder.newInstance(parent, this)
    }

    override fun onBindView(holder: EmojiViewHolder, position: Int) {
        holder.bind(data[position]!!)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence): FilterResults {
                if (copiedList.isEmpty()) {
                    copiedList.addAll(data.filterNotNull())
                }
                val text = constraint.toString().lowercase(Locale.getDefault())
                val filteredResults: List<Emoji> = if (text.isNotBlank()) {
                    val data = data.filterNotNull().filter {
                        text in it.tags || it.description.contains(text) ||
                                it.unicode.contains(text) || text in it.aliases
                    }
                    data.ifEmpty { copiedList }
                } else {
                    copiedList
                }
                val results = FilterResults()
                results.values = filteredResults
                results.count = filteredResults.size
                return results
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(var1: CharSequence, results: FilterResults) {
                results.values?.let {
                    insertItems(it as List<Emoji>)
                }
            }
        }
    }
}