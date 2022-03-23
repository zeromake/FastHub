package com.fastaccess.provider.rest

import android.app.DownloadManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import com.fastaccess.R
import com.fastaccess.helper.ActivityHelper
import com.fastaccess.helper.InputHelper
import java.io.File

// by https://viayoo.com decompile code
object DownloadProvider {
    private const val systemDownloader = "System Downloader"
    private const val defaultBrowser = "Default Browser"
    val intents: Array<Array<String>> = arrayOf(
        arrayOf("", "", "", defaultBrowser, "default_browser"),
        arrayOf("", "", "", systemDownloader, "system_downloader"),
        arrayOf(
            "com.dv.adm.pay",
            "com.dv.get.Main",
            "com.dv.adm.pay.AEditor",
            "ADM+",
            "adm+"
        ),
        arrayOf(
            "com.dv.adm",
            "com.dv.get.Main",
            "com.dv.adm.AEditor",
            "ADM",
            "adm"
        ),
        arrayOf(
            "idm.internet.download.manager.plus",
            "idm.internet.download.manager.MainActivity",
            "idm.internet.download.manager.Downloader",
            "IDM+",
            "idm+"
        ),
        arrayOf(
            "idm.internet.download.manager",
            "idm.internet.download.manager.MainActivity",
            "idm.internet.download.manager.Downloader",
            "IDM",
            "idm"
        ),
        arrayOf(
            "com.vanda_adm.vanda",
            "com.vanda_adm.vanda.MainActivity",
            "com.vanda_adm.vanda.ClipActivity",
            "QKADM",
            "qkadm"
        ),
        arrayOf(
            "org.freedownloadmanager.fdm",
            "org.freedownloadmanager.fdm.MyActivity",
            "org.freedownloadmanager.fdm.SendActivity",
            "FDM",
            "fdm"
        ),
        arrayOf(
            "com.dv.get",
            "com.dv.get.Main",
            "com.dv.get.AEditor",
            "DVGet",
            "dvget"
        ),
        arrayOf(
            "com.tachibana.downloader",
            "com.tachibana.downloader.ui.main.MainActivity",
            "com.tachibana.downloader.ui.adddownload.AddDownloadActivity",
            "Download Navi",
            "navi"
        ),
    )

    private fun thirdPartyDownload(context: Context, url: String, intentPosition: Int): Boolean {
        if (intentPosition >= 2 && intentPosition < intents.size && !InputHelper.isEmpty(url)) {
            val intent = Intent()
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.type = "text/plain"
            intent.action = "android.intent.action.SEND"
            intent.putExtra("android.intent.extra.TEXT", url)
            val arr = intents[intentPosition]
            intent.component = ComponentName(
                arr[0],
                arr[2],
            )
            try {
                context.startActivity(intent)
                return true
            } catch (unused: Exception) {
            }
        }
        return false
    }

    fun download(context: Context, url: String, intentPosition: Int): Boolean {
        return when (intentPosition) {
            0 -> ActivityHelper.startCustomTab(context, url)
            1 -> systemDownload(context, url)
            else -> thirdPartyDownload(context, url, intentPosition)
        }
    }

    private fun systemDownload(context: Context, url: String): Boolean {
        if (InputHelper.isEmpty(url)) return false
        val uri = Uri.parse(url)
        val downloadManager = context.getSystemService(
            Context.DOWNLOAD_SERVICE
        ) as DownloadManager
        val request = DownloadManager.Request(uri)
        val fileName = File(url).name
        request.setDestinationInExternalPublicDir(
            Environment.DIRECTORY_DOWNLOADS,
            context.getString(R.string.app_name) + "/" + fileName
        )
        request.setAllowedNetworkTypes(
            DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI
        )
        request.setTitle(fileName)
        request.setDescription(context.getString(R.string.downloading_file))
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        downloadManager.enqueue(request)
        return true
    }

    fun downloadByName(context: Context, url: String, name: String): Boolean {
        var intentPosition = 0
        for (i in intents.indices) {
            val arr = intents[i]
            if (arr[3] == name) {
                intentPosition = i
                break
            }
        }
        return download(context, url, intentPosition)
    }

    fun getDownloads(context: Context): Array<Array<String>> {
        val packageManager = context.packageManager
        val downloads = mutableListOf<String>()
        val downloadsValues = mutableListOf<String>()
        for (i in intents.indices) {
            val arr = intents[i]
            if (arr[0] == "") {
                downloads.add(arr[3])
                downloadsValues.add(arr[4])
                continue
            }
            if (packageManager == null) {
                break
            }
            var packageInfo: PackageInfo? = null
            try {
                packageInfo = packageManager.getPackageInfo(arr[0], PackageManager.GET_ACTIVITIES)
            } catch (e: Exception) {

            }
            if (packageInfo != null && packageInfo.applicationInfo.enabled) {
                downloads.add(arr[3])
                downloadsValues.add(arr[4])
            }
        }
        return arrayOf(downloads.toTypedArray(), downloadsValues.toTypedArray())
    }
}