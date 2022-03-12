package com.fastaccess.ui.modules.feeds

import android.net.Uri
import android.os.Bundle
import android.view.View
import com.fastaccess.data.dao.NameParser
import com.fastaccess.data.dao.Pageable
import com.fastaccess.data.dao.SimpleUrlsModel
import com.fastaccess.data.dao.model.Event
import com.fastaccess.data.dao.model.Login
import com.fastaccess.data.dao.types.EventsType
import com.fastaccess.helper.*
import com.fastaccess.provider.rest.RestProvider
import com.fastaccess.provider.scheme.LinkParserHelper.isEnterprise
import com.fastaccess.provider.scheme.SchemeParser.launchUri
import com.fastaccess.ui.base.mvp.presenter.BasePresenter
import com.fastaccess.ui.modules.repos.RepoPagerActivity
import com.fastaccess.ui.modules.repos.code.commit.details.CommitPagerActivity
import com.fastaccess.ui.modules.repos.code.releases.ReleasesListActivity
import com.fastaccess.ui.modules.repos.wiki.WikiActivity.Companion.getWiki
import io.reactivex.Observable

/**
 * Created by Kosh on 11 Nov 2016, 12:36 PM
 */
class FeedsPresenter : BasePresenter<FeedsMvp.View>(), FeedsMvp.Presenter {
    override val events: MutableList<Event> = mutableListOf()
    override var currentPage = 0
    override var previousTotal = 0
    private var lastPage = Int.MAX_VALUE

    @com.evernote.android.state.State
    var user: String? = null

    @com.evernote.android.state.State
    var isOrg = false
    override fun onFragmentCreated(argument: Bundle) {
        user = argument.getString(BundleConstant.EXTRA)
        isOrg = argument.getBoolean(BundleConstant.EXTRA_TWO)
        if (events.isEmpty()) {
            onCallApi(1)
        }
    }

    override fun onCallApi(page: Int): Boolean {
        if (page == 1) {
            lastPage = Int.MAX_VALUE
            sendToView { view -> view?.loadMore?.reset() }
        }
        if (page > lastPage || lastPage == 0) {
            sendToView { it?.hideProgress() }
            return false
        }
        currentPage = page
        val login = Login.getUser() ?: return false
        // I can't understand how this could possibly be reached lol.
        Logger.e(isOrg)
        val observable: Observable<Pageable<Event>> = if (user != null) {
            if (isOrg) {
                RestProvider.getOrgService(isEnterprise)
                    .getReceivedEvents(login.login, user!!, page)
            } else {
                RestProvider.getUserService(
                    if (login.login.equals(
                            user,
                            ignoreCase = true
                        )
                    ) PrefGetter.isEnterprise else isEnterprise
                ).getUserEvents(
                    user!!, page
                )
            }
        } else {
            RestProvider.getUserService(PrefGetter.isEnterprise)
                .getReceivedEvents(login.login, page)
        }
        makeRestCall(observable) { response ->
            lastPage = response.last
            val items = response.items ?: listOf()
            if (currentPage == 1) {
//                events.clear()
//                events.addAll(items)
                manageDisposable(Event.save(items, user))
            }
            sendToView { view ->
                view?.onNotifyAdapter(
                    items,
                    page
                )
            }
        }
        return true
    }

    override fun onCallApi(page: Int, parameter: String?): Boolean {
        return onCallApi(page)
    }

    override fun onSubscribed(cancelable: Boolean) {
        sendToView { view -> view!!.showProgress(0) }
    }

    override fun onError(throwable: Throwable) {
        onWorkOffline()
        super.onError(throwable)
    }

    override fun onWorkOffline() {
        if (events.isEmpty() && InputHelper.isEmpty(user)) {
            manageDisposable(RxHelper.getObservable(
                Event.getEvents(Login.getUser().login).toObservable()
            )
                .subscribe({ modelList ->
                    if (modelList != null) {
//                        events.addAll(modelList.filterNotNull())
                        sendToView { view ->
                            view?.onNotifyAdapter(
                                modelList.filterNotNull(),
                                1
                            )
                        }
                    }
                }) { obj: Throwable -> obj.printStackTrace() })
        } else {
            sendToView { it!!.hideProgress() }
        }
    }

    override fun onItemClick(position: Int, v: View?, item: Event) {
        v ?: return
        if (item.type === EventsType.ForkEvent) {
            val parser = NameParser(
                item.repo.url,
//                item.payload.forkee?.htmlUrl
            )
            RepoPagerActivity.startRepoPager(v.context, parser)
        } else {
            val payloadModel = item.payload
            if (payloadModel != null) {
                if (payloadModel.head != null) {
                    if (payloadModel.commits != null && payloadModel.commits!!.size > 1) {
                        sendToView { view ->
                            view!!.onOpenCommitChooser(
                                payloadModel.commits!!
                            )
                        }
                    } else {
                        val repoModel = item.repo
                        val nameParser = NameParser(repoModel.url)
                        val intent = CommitPagerActivity.createIntent(
                            v.context, nameParser.name!!,
                            nameParser.username!!, payloadModel.head!!, true,
                            isEnterprise(repoModel.url)
                        )
                        v.context.startActivity(intent)
                    }
                } else if (payloadModel.issue != null) {
                    launchUri(v.context, Uri.parse(payloadModel.issue!!.htmlUrl), true)
                } else if (payloadModel.pullRequest != null) {
                    launchUri(v.context, Uri.parse(payloadModel.pullRequest!!.htmlUrl), true)
                } else if (payloadModel.comment != null) {
                    launchUri(v.context, Uri.parse(payloadModel.comment!!.htmlUrl), true)
                } else if (item.type === EventsType.ReleaseEvent && payloadModel.release != null) {
                    val nameParser = NameParser(
                        payloadModel.release!!.htmlUrl
                    )
                    v.context.startActivity(
                        ReleasesListActivity.getIntent(
                            v.context, nameParser.username!!, nameParser.name!!,
                            payloadModel.release!!.id, isEnterprise(payloadModel.release!!.htmlUrl)
                        )
                    )
                } else if (item.type === EventsType.CreateEvent && "tag".equals(
                        payloadModel.refType,
                        ignoreCase = true
                    )
                ) {
                    val repoModel = item.repo
                    val nameParser = NameParser(repoModel.url)
                    v.context.startActivity(
                        ReleasesListActivity.getIntent(
                            v.context, nameParser.username!!, nameParser.name!!,
                            payloadModel.ref!!, isEnterprise(repoModel.url)
                        )
                    )
                } else if (item.type === EventsType.GollumEvent) {
                    val repoModel = item.repo
                    val parser = NameParser(repoModel.url)
                    v.context.startActivity(getWiki(v.context, parser.name, parser.username))
                } else {
                    val repoModel = item.repo
                    val parser = NameParser(repoModel.url)
                    RepoPagerActivity.startRepoPager(v.context, parser)
                }
            }
        }
    }

    override fun onItemLongClick(position: Int, v: View?, item: Event) {
        v ?: return
        if (item.type === EventsType.ForkEvent) {
            if (view != null) {
                view!!.onOpenRepoChooser(
                    listOf(
                        SimpleUrlsModel(
                            item.payload.forkee!!.fullName,
                            item.payload.forkee!!.htmlUrl
                        ),
                        SimpleUrlsModel(item.repo.name, item.repo.url),
                    )
                )
            }
        } else {
            val repo = item.repo
            if (repo != null) {
                val parser = NameParser(repo.url)
                RepoPagerActivity.startRepoPager(v.context, parser)
            }
        }
    }
}