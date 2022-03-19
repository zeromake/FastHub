package com.fastaccess

import android.app.Application
import android.content.pm.ShortcutManager
import android.os.Build
import androidx.annotation.RequiresApi
import com.fastaccess.data.dao.model.Models
import com.fastaccess.helper.DeviceNameGetter
import com.fastaccess.helper.PrefHelper.init
import com.fastaccess.helper.SettingsDataStore
import com.fastaccess.helper.TypeFaceHelper.generateTypeface
import com.fastaccess.provider.colors.ColorsProvider
import com.fastaccess.provider.crash.Report
import com.fastaccess.provider.emoji.EmojiManager
import com.google.firebase.messaging.FirebaseMessaging
import com.miguelbcr.io.rx_billing_service.RxBillingService
import es.dmoral.toasty.Toasty
import io.requery.Persistable
import io.requery.android.sqlite.DatabaseSource
import io.requery.reactivex.ReactiveEntityStore
import io.requery.reactivex.ReactiveSupport
import io.requery.sql.EntityDataStore
import io.requery.sql.TableCreationMode

/**
 * Created by Kosh on 03 Feb 2017, 12:07 AM
 */
class App : Application() {
    val dataStore: ReactiveEntityStore<Persistable> by lazy {
        val model = Models.DEFAULT
        val source = DatabaseSource(this, model, "FastHub-DB", 18)
        val configuration = source.configuration
        if (BuildConfig.DEBUG) {
            source.setTableCreationMode(TableCreationMode.CREATE_NOT_EXISTS)
        }
        ReactiveSupport.toReactiveStore(EntityDataStore(configuration))
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        init()
    }

    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    private fun initShortcut() {
        val shortcutManager = this.applicationContext.getSystemService(
            ShortcutManager::class.java
        )
        shortcutManager.removeAllDynamicShortcuts()
    }

    private fun init() {
        Report.init(applicationContext)
        RxBillingService.register(this)
        deleteDatabase("database.db")
        dataStore
        setupPreference()
        generateTypeface(this)
        //        NotificationSchedulerJobTask.scheduleJob(this);
        if (BuildConfig.DEBUG) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                initShortcut()
            }
        }
        EmojiManager.load()
        ColorsProvider.load()
        DeviceNameGetter.instance.loadDevice()
        try {
            FirebaseMessaging.getInstance().subscribeToTopic("FastHub")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        Toasty.Config.getInstance().allowQueue(true).apply()
    }

    private fun setupPreference() {
        init(this.applicationContext)
        SettingsDataStore.init()
    }

    companion object {
        private lateinit var instance: App

        @JvmStatic
        fun getInstance(): App {
            return instance
        }
    }
}
