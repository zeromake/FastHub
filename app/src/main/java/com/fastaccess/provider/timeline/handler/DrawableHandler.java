package com.fastaccess.provider.timeline.handler;

import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.widget.TextView;

import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.PrefGetter;
import com.fastaccess.provider.scheme.SchemeParser;
import com.fastaccess.provider.timeline.handler.drawable.DrawableGetter;
import com.fastaccess.ui.widgets.SpannableBuilder;

import net.nightwhistler.htmlspanner.TagNodeHandler;
import net.nightwhistler.htmlspanner.spans.CenterSpan;

import org.htmlcleaner.TagNode;


import static android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;

/**
 * Created by Kosh on 22 Apr 2017, 1:09 PM
 */

public class DrawableHandler extends TagNodeHandler {

    private final TextView textView;
    private final int width;
    public DrawableHandler(TextView textView, int width) {
        this.textView = textView;
        this.width = width;
    }

    private boolean isNull() {
        return textView == null;
    }

    @Override public void handleTagNode(TagNode node, SpannableStringBuilder builder, int start, int end) {
        String src = node.getAttributeByName("src");
        if (!InputHelper.isEmpty(src)) {
            if (!PrefGetter.isAutoImageDisabled()) {
                builder.append("￼");
                if (isNull()) return;
                builder.append("\n");
                DrawableGetter imageGetter = new DrawableGetter(textView, width);
                builder.setSpan(new ImageSpan(imageGetter.getDrawable(src)), start, builder.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
                builder.setSpan(new CenterSpan(), start, builder.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
                builder.append("\n");
            } else {
                builder.append(SpannableBuilder.builder().clickable("Image", v -> SchemeParser.launchUri(v.getContext(), src)));
                builder.append("\n");
            }
        }
    }
}
