package com.fastaccess.ui.modules.repos.code.prettifier

import android.net.Uri
import android.os.Bundle
import android.webkit.MimeTypeMap
import com.fastaccess.R
import com.fastaccess.data.dao.MarkdownModel
import com.fastaccess.data.dao.model.ViewerFile
import com.fastaccess.helper.BundleConstant
import com.fastaccess.helper.InputHelper.isEmpty
import com.fastaccess.helper.RxHelper.getObservable
import com.fastaccess.provider.markdown.MarkDownProvider.isArchive
import com.fastaccess.provider.markdown.MarkDownProvider.isImage
import com.fastaccess.provider.markdown.MarkDownProvider.isMarkdown
import com.fastaccess.provider.rest.RestProvider.getErrorCode
import com.fastaccess.provider.rest.RestProvider.getRepoService
import com.fastaccess.ui.base.mvp.presenter.BasePresenter

/**
 * Created by Kosh on 27 Nov 2016, 3:43 PM
 */
class ViewerPresenter : BasePresenter<ViewerMvp.View>(), ViewerMvp.Presenter {
    private var downloadedStream: String? = null

    @com.evernote.android.state.State
    override var isMarkDown = false

    @com.evernote.android.state.State
    override var isRepo = false

    @com.evernote.android.state.State
    override var isImage = false

    @com.evernote.android.state.State
    var url: String? = null

    @JvmField
    @com.evernote.android.state.State
    var htmlUrl: String? = null

    @JvmField
    @com.evernote.android.state.State
    var defaultBranch: String? = null
    override fun onError(throwable: Throwable) {
        throwable.printStackTrace()
        val code = getErrorCode(throwable)
        if (code == 404) {
            if (!isRepo) {
                sendToView { view -> view.onShowError(R.string.no_file_found) }
            }
            sendToView { it.hideProgress() }
        } else {
            if (code == 406) {
                sendToView { view ->
                    view.hideProgress()
                    view.openUrl(url!!)
                }
                return
            }
            onWorkOffline()
            super.onError(throwable)
        }
    }

    override fun onHandleIntent(intent: Bundle?) {
        if (intent == null) return
        isRepo = intent.getBoolean(BundleConstant.EXTRA)
        url = intent.getString(BundleConstant.ITEM)
        htmlUrl = intent.getString(BundleConstant.EXTRA_TWO)
        defaultBranch = intent.getString(BundleConstant.EXTRA_THREE)
        if (!isEmpty(url)) {
            if (isArchive(url)) {
                sendToView { view -> view.onShowError(R.string.archive_file_detected_error) }
                return
            }
            if (isRepo) {
                url = if (url!!.endsWith("/")) url + "readme" else "$url/readme"
            }
            onWorkOnline()
        }
    }

    override fun onLoadContentAsStream() {
        val isImage = isImage(url) && !"svg".equals(
            MimeTypeMap.getFileExtensionFromUrl(url),
            ignoreCase = true
        )
        if (isImage || isArchive(url)) {
            return
        }
        makeRestCall(
            getRepoService(isEnterprise).getFileAsStream(url!!)
        ) { body ->
            downloadedStream = body
            sendToView { view: ViewerMvp.View ->
                view.onSetCode(
                    body
                )
            }
        }
    }

    override fun downloadedStream(): String? {
        return downloadedStream
    }

    override fun onWorkOffline() {
        if (downloadedStream == null) {
            manageDisposable(
                getObservable(ViewerFile.get(url!!))
                    .subscribe({ fileModel: ViewerFile? ->
                        if (fileModel != null) {
                            isImage = isImage(fileModel.fullUrl)
                            if (isImage) {
                                sendToView { view ->
                                    view.onSetImageUrl(
                                        fileModel.fullUrl,
                                        false
                                    )
                                }
                            } else {
                                downloadedStream = fileModel.content
                                isRepo = fileModel.isRepo
                                isMarkDown = fileModel.isMarkdown
                                sendToView { view ->
                                    if (isRepo || isMarkDown) {
                                        view.onSetMdText(
                                            downloadedStream!!,
                                            fileModel.fullUrl,
                                            false,
                                            defaultBranch
                                        )
                                    } else {
                                        view.onSetCode(downloadedStream!!)
                                    }
                                }
                            }
                        }
                    }) { throwable ->
                        sendToView { view ->
                            view.showErrorMessage(
                                throwable.message!!
                            )
                        }
                    })
        }
    }

    override fun onWorkOnline() {
        isImage = isImage(url)
        if (isImage) {
            if ("svg".equals(MimeTypeMap.getFileExtensionFromUrl(url), ignoreCase = true)) {
                makeRestCall(
                    getRepoService(isEnterprise).getFileAsStream(
                        url!!
                    )
                ) { s ->
                    sendToView { view ->
                        view.onSetImageUrl(
                            s, true
                        )
                    }
                }
                return
            }
            sendToView { view ->
                view.onSetImageUrl(
                    url!!, false
                )
            }
            return
        }
        val streamObservable =
            if (isMarkdown(url)) getRepoService(isEnterprise).getFileAsHtmlStream(
                url!!
            ) else getRepoService(isEnterprise).getFileAsStream(url!!)
        val observable = if (isRepo) getRepoService(isEnterprise).getReadmeHtml(
            url!!
        ) else streamObservable
        makeRestCall(observable) { content: String? ->
            downloadedStream = content
            val fileModel = ViewerFile()
            fileModel.content = downloadedStream
            fileModel.fullUrl = url
            fileModel.isRepo = isRepo
            if (isRepo) {
                fileModel.isMarkdown = true
                isMarkDown = true
                isRepo = true
                sendToView { view ->
                    view.onSetMdText(
                        downloadedStream!!,
                        if (htmlUrl == null) url else htmlUrl,
                        false,
                        defaultBranch
                    )
                }
            } else {
                isMarkDown = isMarkdown(url)
                if (isMarkDown) {
                    val model = MarkdownModel()
                    model.text = downloadedStream
                    val uri = Uri.parse(url)
                    val baseUrl = StringBuilder()
                    for (s in uri.pathSegments) {
                        if (!s.equals(uri.lastPathSegment, ignoreCase = true)) {
                            baseUrl.append("/").append(s)
                        }
                    }
                    model.context = baseUrl.toString()
                    val obs = getRepoService(isEnterprise).convertReadmeToHtml(model)
                    makeRestCall(obs) { string: String? ->
                        isMarkDown = true
                        downloadedStream = string
                        fileModel.isMarkdown = true
                        fileModel.content = downloadedStream
                        manageObservable(fileModel.save(fileModel).toObservable())
                        sendToView { view ->
                            view.onSetMdText(
                                downloadedStream!!,
                                if (htmlUrl == null) url else htmlUrl,
                                true,
                                defaultBranch
                            )
                        }
                    }
                    return@makeRestCall
                }
                fileModel.isMarkdown = false
                sendToView { view ->
                    view.onSetCode(
                        downloadedStream!!
                    )
                }
            }
            manageObservable(fileModel.save(fileModel).toObservable())
        }
    }

    override fun url(): String {
        return url!!
    }
}