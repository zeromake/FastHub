package com.fastaccess.ui.modules.repos.code.prettifier

import android.os.Bundle
import android.view.*
import android.widget.ProgressBar
import androidx.annotation.StringRes
import butterknife.BindView
import com.evernote.android.state.State
import com.fastaccess.R
import com.fastaccess.helper.ActivityHelper.startCustomTab
import com.fastaccess.helper.AppHelper.isDeviceAnimationEnabled
import com.fastaccess.helper.BundleConstant
import com.fastaccess.helper.Bundler.Companion.start
import com.fastaccess.helper.InputHelper.isEmpty
import com.fastaccess.helper.PrefGetter.isWrapCode
import com.fastaccess.ui.base.BaseFragment
import com.fastaccess.ui.widgets.StateLayout
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import com.prettifier.pretty.PrettifyWebView
import it.sephiroth.android.library.bottomnavigation.BottomNavigation
import kotlin.math.abs

/**
 * Created by Kosh on 28 Nov 2016, 9:27 PM
 */
class ViewerFragment : BaseFragment<ViewerMvp.View, ViewerPresenter>(), ViewerMvp.View,
    OnOffsetChangedListener {
    @JvmField
    @BindView(R.id.readmeLoader)
    var loader: ProgressBar? = null

    @JvmField
    @BindView(R.id.webView)
    var webView: PrettifyWebView? = null

    @JvmField
    @BindView(R.id.stateLayout)
    var stateLayout: StateLayout? = null
    private var appBarLayout: AppBarLayout? = null
    private var bottomNavigation: BottomNavigation? = null
    private var isAppBarMoving = false
    private var isAppBarExpanded = true
    private var isAppBarListener = false

    @JvmField
    @State
    var isWrap = isWrapCode

    @JvmField
    @State
    var defaultBranch: String? = null
    override fun onSetImageUrl(url: String, isSvg: Boolean) {
        webView!!.loadImage(url, isSvg)
        webView!!.setOnContentChangedListener(this)
        webView!!.visibility = View.VISIBLE
        requireActivity().invalidateOptionsMenu()
    }

    override fun onSetMdText(text: String, baseUrl: String?, replace: Boolean, branch: String?) {
        webView!!.visibility = View.VISIBLE
        loader!!.isIndeterminate = false
        webView!!.setGithubContentWithReplace(text, baseUrl, replace, branch)
        webView!!.setOnContentChangedListener(this)
        requireActivity().invalidateOptionsMenu()
    }

    override fun onSetCode(text: String) {
        webView!!.visibility = View.VISIBLE
        loader!!.isIndeterminate = false
        webView!!.setSource(text, isWrap)
        webView!!.setOnContentChangedListener(this)
        requireActivity().invalidateOptionsMenu()
    }

    override fun onShowError(msg: String) {
        hideProgress()
        showErrorMessage(msg)
    }

    override fun onShowError(@StringRes msg: Int) {
        hideProgress()
        onShowError(getString(msg))
    }

    override fun onShowMdProgress() {
        loader!!.isIndeterminate = true
        loader!!.visibility = View.VISIBLE
        stateLayout!!.showProgress()
    }

    override fun openUrl(url: String) {
        startCustomTab(requireActivity(), url)
    }

    override fun onViewAsCode() {
        presenter!!.onLoadContentAsStream()
    }

    override fun showProgress(@StringRes resId: Int) {
        onShowMdProgress()
    }

    override fun hideProgress() {
        loader!!.visibility = View.GONE
        stateLayout!!.hideProgress()
        if (!presenter!!.isImage) stateLayout!!.showReload(if (presenter!!.downloadedStream() == null) 0 else 1)
    }

    override fun showErrorMessage(msgRes: String) {
        hideProgress()
        super.showErrorMessage(msgRes)
    }

    override fun showMessage(titleRes: Int, msgRes: Int) {
        hideProgress()
        super.showMessage(titleRes, msgRes)
    }

    override fun showMessage(titleRes: String, msgRes: String) {
        hideProgress()
        super.showMessage(titleRes, msgRes)
    }

    override fun fragmentLayout(): Int {
        return R.layout.general_viewer_layout
    }

    override fun providePresenter(): ViewerPresenter {
        return ViewerPresenter()
    }

    override fun onContentChanged(progress: Int) {
        if (loader != null) {
            loader!!.progress = progress
            if (progress == 100) {
                hideProgress()
                if (!presenter!!.isMarkDown && !presenter!!.isImage) {
                    webView!!.scrollToLine(presenter!!.url())
                }
            }
        }
    }

    override fun onScrollChanged(reachedTop: Boolean, scroll: Int) {
        if (isDeviceAnimationEnabled(requireActivity())) {
            if (presenter!!.isRepo && appBarLayout != null && bottomNavigation != null && webView != null) {
                val shouldExpand = webView!!.scrollY == 0
                if (!isAppBarMoving && shouldExpand != isAppBarExpanded) {
                    isAppBarMoving = true
                    isAppBarExpanded = shouldExpand
                    bottomNavigation!!.setExpanded(shouldExpand, true)
                    appBarLayout!!.setExpanded(shouldExpand, true)
                    webView!!.isNestedScrollingEnabled = shouldExpand
                    if (shouldExpand) webView!!.onTouchEvent(
                        MotionEvent.obtain(
                            0,
                            0,
                            MotionEvent.ACTION_UP,
                            0f,
                            0f,
                            0
                        )
                    )
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        defaultBranch = requireArguments().getString(BundleConstant.EXTRA_THREE)
        setHasOptionsMenu(true)
    }

    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        if (isEmpty(presenter!!.downloadedStream())) {
            presenter!!.onHandleIntent(arguments)
        } else {
            if (presenter!!.isMarkDown) {
                onSetMdText(
                    presenter!!.downloadedStream()!!,
                    presenter!!.url(),
                    false,
                    defaultBranch
                )
            } else {
                onSetCode(presenter!!.downloadedStream()!!)
            }
        }
        requireActivity().invalidateOptionsMenu()
        stateLayout!!.setEmptyText(R.string.no_data)
        if (savedInstanceState == null) {
            stateLayout!!.showReload(0)
        }
        stateLayout!!.setOnReloadListener {
            presenter!!.onHandleIntent(
                arguments
            )
        }
        if (presenter!!.isRepo) {
            appBarLayout = requireActivity().findViewById(R.id.appbar)
            bottomNavigation = requireActivity().findViewById(R.id.bottomNavigation)
            if (appBarLayout != null && !isAppBarListener) {
                appBarLayout!!.addOnOffsetChangedListener(this)
                isAppBarListener = true
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (isDeviceAnimationEnabled(requireContext())) {
            if (appBarLayout != null && !isAppBarListener) {
                appBarLayout!!.addOnOffsetChangedListener(this)
                isAppBarListener = true
            }
        }
    }

    override fun onStop() {
        if (isDeviceAnimationEnabled(requireContext())) {
            if (appBarLayout != null && isAppBarListener) {
                appBarLayout!!.removeOnOffsetChangedListener(this)
                isAppBarListener = false
            }
        }
        super.onStop()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.wrap_menu_option, menu)
        menu.findItem(R.id.wrap).isVisible = false
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val menuItem = menu.findItem(R.id.wrap)
        if (menuItem != null) {
            if (presenter!!.isMarkDown || presenter!!.isRepo || presenter!!.isImage) {
                menuItem.isVisible = false
            } else {
                menuItem.setVisible(true).setCheckable(true).isChecked = isWrap
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.wrap) {
            item.isChecked = !item.isChecked
            isWrap = item.isChecked
            showProgress(0)
            onSetCode(presenter!!.downloadedStream()!!)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onScrollTop(index: Int) {
        super.onScrollTop(index)
        if (webView != null) webView!!.scrollTo(0, 0)
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (!isVisibleToUser && appBarLayout != null) {
            appBarLayout!!.visibility = View.VISIBLE
        }
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
        var offset = verticalOffset
        offset = abs(offset)
        if (offset == 0 || offset == appBarLayout.totalScrollRange) isAppBarMoving =
            false
    }

    override fun onDestroy() {
        super.onDestroy()
        if (webView != null) {
            webView!!.destroy()
        }
    }

    companion object {
        val TAG: String = ViewerFragment::class.java.simpleName
        fun newInstance(url: String, isRepo: Boolean): ViewerFragment {
            return newInstance(url, null, isRepo, "")
        }

        @JvmOverloads
        fun newInstance(
            url: String,
            htmlUrl: String?,
            isRepo: Boolean = false,
            defaultBranch: String? = ""
        ): ViewerFragment {
            return newInstance(
                start()
                    .put(BundleConstant.ITEM, url)
                    .put(BundleConstant.EXTRA, isRepo)
                    .put(BundleConstant.EXTRA_TWO, htmlUrl)
                    .put(BundleConstant.EXTRA_THREE, defaultBranch)
                    .end()
            )
        }

        private fun newInstance(bundle: Bundle): ViewerFragment {
            val fragmentView = ViewerFragment()
            fragmentView.arguments = bundle
            return fragmentView
        }
    }
}