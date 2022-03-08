package com.fastaccess.ui.widgets

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.appcompat.widget.TooltipCompat
import androidx.core.content.ContextCompat
import cn.gavinliu.android.lib.shapedimageview.ShapedImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.signature.StringSignature
import com.fastaccess.R
import com.fastaccess.helper.InputHelper
import com.fastaccess.helper.PrefGetter
import com.fastaccess.ui.modules.user.UserPagerActivity.Companion.startActivity
import com.fastaccess.utils.setOnThrottleClickListener

/**
 * Created by Kosh on 14 Nov 2016, 7:59 PM
 */
class AvatarLayout : FrameLayout {
    lateinit var avatar: ShapedImageView
    private var login: String? = null
    private var isOrg = false
    private var isEnterprise = false

    override fun onFinishInflate() {
        super.onFinishInflate()
        inflate(context, R.layout.avatar_layout, this)
        if (isInEditMode) return

        this.avatar = this.findViewById(R.id.avatar)
        this.avatar.setOnThrottleClickListener { view ->
            if (InputHelper.isEmpty(login)) return@setOnThrottleClickListener
            startActivity(view.context, login!!, isOrg, isEnterprise, -1)
        }
        setBackground()
        if (PrefGetter.isRectAvatar) {
            avatar.setShape(ShapedImageView.SHAPE_MODE_ROUND_RECT, 15f)
        }
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, @AttrRes defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    constructor(
        context: Context,
        attrs: AttributeSet?,
        @AttrRes defStyleAttr: Int,
        @StyleRes defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    fun setUrl(url: String?, login: String?, isOrg: Boolean, isEnterprise: Boolean) {
        setUrl(url, login, isOrg, isEnterprise, false)
    }

    fun setUrl(
        url: String?,
        login: String?,
        isOrg: Boolean,
        isEnterprise: Boolean,
        reload: Boolean
    ) {
        this.login = login
        this.isOrg = isOrg
        this.isEnterprise = isEnterprise
        avatar.contentDescription = login
        if (login != null) {
            TooltipCompat.setTooltipText(avatar, login)
        } else {
            avatar.setOnClickListener(null)
            avatar.setOnLongClickListener(null)
        }
        Glide.with(context)
            .load(url)
            .fallback(ContextCompat.getDrawable(context, R.drawable.ic_fasthub_mascot))
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .signature(StringSignature(if (reload) System.currentTimeMillis().toString() else "0"))
            .dontAnimate()
            .into(avatar)
    }

    private fun setBackground() {
        if (PrefGetter.isRectAvatar) {
            setBackgroundResource(R.drawable.rect_shape)
        } else {
            setBackgroundResource(R.drawable.circle_shape)
        }
    }
}