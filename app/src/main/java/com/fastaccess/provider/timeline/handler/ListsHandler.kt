package com.fastaccess.provider.timeline.handler

import android.graphics.drawable.Drawable
import android.text.SpannableStringBuilder
import com.fastaccess.helper.Logger.e
import com.fastaccess.ui.widgets.SpannableBuilder.Companion.builder
import net.nightwhistler.htmlspanner.TagNodeHandler
import org.htmlcleaner.TagNode

class ListsHandler(private val checked: Drawable?, private val unchecked: Drawable?) :
    TagNodeHandler() {
    private fun getMyIndex(node: TagNode): Int {
        if (node.parent != null) {
            var i = 1
            for (child in node.parent.children) {
                if (child === node) {
                    return i
                }
                if (child is TagNode) {
                    if ("li" == child.name) {
                        ++i
                    }
                }
            }
        }
        return -1
    }

    private fun getParentName(node: TagNode): String? {
        return if (node.parent == null) null else node.parent.name
    }

    override fun beforeChildren(node: TagNode, builder: SpannableStringBuilder) {
        var todoItem: TodoItems? = null
        if (node.childTags != null && node.childTags.size > 0) {
            for (tagNode in node.childTags) {
                e(tagNode.name, tagNode.text)
                if (tagNode.name != null && tagNode.name == "input") {
                    todoItem = TodoItems()
                    todoItem.isChecked = tagNode.getAttributeByName("checked") != null
                    break
                }
            }
        }
        if ("ol" == getParentName(node)) {
            builder.append(getMyIndex(node).toString()).append(". ")
        } else if ("ul" == getParentName(node)) {
            if (todoItem != null) {
                if (checked == null || unchecked == null) {
                    builder.append(if (todoItem.isChecked) "☑" else "☐")
                } else {
                    builder.append(
                        builder()
                            .append(if (todoItem.isChecked) checked else unchecked)
                    )
                        .append(" ")
                }
            } else {
                builder.append("\u2022 ")
            }
        }
    }

    override fun handleTagNode(
        tagNode: TagNode,
        spannableStringBuilder: SpannableStringBuilder,
        i: Int,
        i1: Int
    ) {
        appendNewLine(spannableStringBuilder)
    }

    internal class TodoItems {
        var isChecked = false
    }
}