package com.fastaccess.helper

import android.R
import android.animation.Animator
import androidx.interpolator.view.animation.FastOutLinearInInterpolator
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import android.view.animation.LinearInterpolator
import androidx.annotation.UiThread
import androidx.core.view.ViewCompat
import android.view.ViewTreeObserver
import android.view.animation.AccelerateInterpolator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.app.Dialog
import android.view.View
import android.widget.PopupWindow
import android.view.ViewAnimationUtils
import android.view.animation.Interpolator
import androidx.fragment.app.DialogFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton.OnVisibilityChangedListener
import kotlin.math.hypot
import kotlin.math.sqrt

/**
 * Created by Kosh on 27 May 2016, 9:04 PM
 */
object AnimHelper {
    private val FAST_OUT_LINEAR_IN_INTERPOLATOR: Interpolator = FastOutLinearInInterpolator()
    private val LINEAR_OUT_SLOW_IN_INTERPOLATOR: Interpolator = LinearOutSlowInInterpolator()
    private val interpolator: Interpolator = LinearInterpolator()


    @JvmStatic
    @UiThread
    fun animateVisibility(view: View?, show: Boolean) {
        if (view == null) {
            return
        }
        if (!ViewCompat.isAttachedToWindow(view)) {
            view.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    view.viewTreeObserver.removeOnPreDrawListener(this)
                    animateSafeVisibility(show, view)
                    return true
                }
            })
        } else {
            animateSafeVisibility(show, view)
        }
    }

    @UiThread
    fun animateSafeVisibility(show: Boolean, view: View) {
        val visibility = View.GONE
        view.animate().cancel()
        val animator = view.animate().setDuration(200).alpha(if (show) 1f else 0f)
            .setInterpolator(AccelerateInterpolator())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator) {
                    super.onAnimationStart(animation)
                    if (show) {
                        view.scaleX = 1f
                        view.scaleY = 1f
                        view.visibility = View.VISIBLE
                    }
                }

                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    if (!show) {
                        view.visibility = visibility
                        view.scaleX = 0f
                        view.scaleY = 0f
                    }
                    animation.removeListener(this)
                    view.clearAnimation()
                }
            })
        val x: Float = if (show) 1F else 0F
        animator.scaleX(x).scaleY(x)
    }

    @UiThread
    private fun getBeats(view: View): List<ObjectAnimator> {
        return listOf(
            ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.1f, 1f),
            ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.1f, 1f)
        )
    }

    @JvmStatic
    @UiThread
    fun startBeatsAnimation(view: View) {
        view.clearAnimation()
        if (view.animation != null) {
            view.animation.cancel()
        }
        val animators = getBeats(view)
        for (anim in animators) {
            anim.setDuration(300).start()
            anim.interpolator = interpolator
        }
    }

    @JvmStatic
    @UiThread
    fun revealPopupWindow(popupWindow: PopupWindow, from: View) {
        val rect = ViewHelper.getLayoutPosition(from)
        val x = rect.exactCenterX().toInt()
        val y = rect.exactCenterY().toInt()
        if (popupWindow.contentView != null) {
            val view = popupWindow.contentView
            if (view != null) {
                popupWindow.showAsDropDown(from)
                view.post {
                    if (ViewCompat.isAttachedToWindow(view)) {
                        val animator = ViewAnimationUtils.createCircularReveal(
                            view, x, y, 0f,
                            hypot(rect.width().toDouble(), rect.height().toDouble()).toFloat()
                        )
                        animator.duration =
                            view.resources.getInteger(R.integer.config_shortAnimTime).toLong()
                        animator.start()
                    }
                }
            }
        }
    }

    @JvmStatic
    @UiThread
    fun revealDialog(dialog: Dialog, animDuration: Int) {
        if (dialog.window != null) {
            val view = dialog.window!!.decorView
            view.post {
                if (ViewCompat.isAttachedToWindow(view)) {
                    val centerX = view.width / 2
                    val centerY = view.height / 2
                    val animator = ViewAnimationUtils.createCircularReveal(
                        view,
                        centerX,
                        centerY,
                        20f,
                        view.height.toFloat()
                    )
                    animator.duration = animDuration.toLong()
                    animator.start()
                }
            }
        }
    }

    @JvmStatic
    @UiThread
    fun dismissDialog(
        dialogFragment: DialogFragment,
        duration: Int,
        listenerAdapter: AnimatorListenerAdapter
    ) {
        val dialog = dialogFragment.dialog
        if (dialog != null) {
            if (dialog.window != null) {
                val view = dialog.window!!.decorView
                val centerX = view.width / 2
                val centerY = view.height / 2
                val radius =
                    sqrt((view.width * view.width / 4 + view.height * view.height / 4).toDouble())
                        .toFloat()
                view.post {
                    if (ViewCompat.isAttachedToWindow(view)) {
                        val animator = ViewAnimationUtils.createCircularReveal(
                            view,
                            centerX,
                            centerY,
                            radius,
                            0f
                        )
                        animator.duration = duration.toLong()
                        animator.addListener(listenerAdapter)
                        animator.start()
                    } else {
                        listenerAdapter.onAnimationEnd(null)
                    }
                }
            }
        } else {
            listenerAdapter.onAnimationEnd(null)
        }
    }

    @JvmStatic
    @UiThread
    fun mimicFabVisibility(
        show: Boolean, view: View,
        listener: OnVisibilityChangedListener?
    ) {
        if (show) {
            view.animate().cancel()
            if (ViewCompat.isLaidOut(view)) {
                if (view.visibility != View.VISIBLE) {
                    view.alpha = 0f
                    view.scaleY = 0f
                    view.scaleX = 0f
                }
                view.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .alpha(1f)
                    .setDuration(200)
                    .setInterpolator(LINEAR_OUT_SLOW_IN_INTERPOLATOR)
                    .withStartAction {
                        view.visibility = View.VISIBLE
                        listener?.onShown(null)
                    }
            } else {
                view.visibility = View.VISIBLE
                view.alpha = 1f
                view.scaleY = 1f
                view.scaleX = 1f
                listener?.onShown(null)
            }
        } else {
            view.animate()
                .scaleX(0f)
                .scaleY(0f)
                .alpha(0f)
                .setDuration(40).interpolator = FAST_OUT_LINEAR_IN_INTERPOLATOR
            view.visibility = View.GONE
            listener?.onHidden(null)
        }
    }
}