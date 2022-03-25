package com.fastaccess.ui.widgets

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar.SnackbarLayout
import it.sephiroth.android.library.bottomnavigation.BottomNavigation
import it.sephiroth.android.library.bottomnavigation.MiscUtils

class FloatingActionButtonBehavior : CoordinatorLayout.Behavior<FloatingActionButton> {
    private var navigationBarHeight = 0

    constructor() : super() {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}

    override fun onAttachedToLayoutParams(lp: CoordinatorLayout.LayoutParams) {
         super.onAttachedToLayoutParams(lp);
    }

    override fun layoutDependsOn(
        parent: CoordinatorLayout,
        child: FloatingActionButton,
        dependency: View
    ): Boolean {
        if (dependency is BottomNavigation) {
            return true
        } else if (dependency is SnackbarLayout) {
            return true
        }
        return super.layoutDependsOn(parent, child, dependency)
    }

    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: FloatingActionButton,
        dependency: View
    ): Boolean {
        MiscUtils.log(Log.INFO, TAG, "onDependentViewChanged: $dependency")
        val list = parent.getDependencies(child)
        val params = child.layoutParams as MarginLayoutParams
        val bottomMargin =
            params.bottomMargin + params.rightMargin - (params.topMargin + params.leftMargin)
        var t = 0f
        var t2 = 0f
        var result = false
        for (dep in list) {
            if (dep is SnackbarLayout) {
                t += dep.getTranslationY() - dep.getHeight()
                result = true
            } else if (dep is BottomNavigation) {
                t2 = dep.translationY - dep.height + bottomMargin
                t += t2
                result = true
                if (navigationBarHeight > 0) {
                    if (!dep.isExpanded) {
                        child.hide()
                    } else {
                        child.show()
                    }
                }
            }
        }
        if (navigationBarHeight > 0 && t2 < 0) {
            t = t2.coerceAtMost(t + navigationBarHeight)
        }
        child.translationY = t
        return result
    }

    fun setNavigationBarHeight(height: Int) {
        navigationBarHeight = height
    }

    companion object {
        private val TAG = FloatingActionButtonBehavior::class.java.simpleName
    }
}