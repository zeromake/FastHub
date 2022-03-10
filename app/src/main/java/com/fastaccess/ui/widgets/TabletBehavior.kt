package com.fastaccess.ui.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.Toolbar
import it.sephiroth.android.library.bottomnavigation.VerticalScrollingBehavior
import it.sephiroth.android.library.bottomnavigation.BottomNavigation
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.appbar.AppBarLayout

class TabletBehavior(context: Context, attrs: AttributeSet) :
    VerticalScrollingBehavior<BottomNavigation>(context, attrs) {
    fun setLayoutValues(bottomNavWidth: Int, topInset: Int, translucentStatus: Boolean) {}
    override fun layoutDependsOn(
        parent: CoordinatorLayout,
        child: BottomNavigation,
        dependency: View
    ): Boolean {
        return AppBarLayout::class.java.isInstance(dependency) || Toolbar::class.java.isInstance(
            dependency
        )
    }

    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: BottomNavigation,
        dependency: View
    ): Boolean {
        return true
    }

    override fun onDependentViewRemoved(
        parent: CoordinatorLayout,
        child: BottomNavigation,
        dependency: View
    ) {
        super.onDependentViewRemoved(parent, child, dependency)
    }

    override fun onLayoutChild(
        parent: CoordinatorLayout,
        child: BottomNavigation,
        layoutDirection: Int
    ): Boolean {
        return super.onLayoutChild(parent, child, layoutDirection)
    }

    override fun onNestedVerticalOverScroll(
        coordinatorLayout: CoordinatorLayout,
        child: BottomNavigation,
        direction: Int,
        currentOverScroll: Int,
        totalOverScroll: Int
    ) {
    }

    override fun onDirectionNestedPreScroll(
        coordinatorLayout: CoordinatorLayout,
        child: BottomNavigation,
        target: View,
        dx: Int,
        dy: Int,
        consumed: IntArray,
        scrollDirection: Int
    ) {
    }

    override fun onNestedDirectionFling(
        coordinatorLayout: CoordinatorLayout,
        child: BottomNavigation,
        target: View,
        velocityX: Float,
        velocityY: Float,
        scrollDirection: Int
    ): Boolean {
        return false
    }
}