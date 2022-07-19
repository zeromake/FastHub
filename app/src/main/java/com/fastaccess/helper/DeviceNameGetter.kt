package com.fastaccess.helper

import android.os.Build
import com.fastaccess.App
import com.jaredrummler.android.device.DeviceDatabase
import com.jaredrummler.android.device.DeviceName.DeviceInfo
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe

/**
 * Created by Front Desk on 8/7/2017.
 */
class DeviceNameGetter private constructor() {
    private var deviceInfo: DeviceInfo? = null
    val deviceName: String
        get() = this.deviceInfo?.marketName ?: blockingDeviceName()

    fun loadDevice() {
        queryToDevice()
    }

    private fun queryToDevice() {
        // Todo use local save
        val codename = Build.DEVICE
        val model = Build.MODEL
        val db = DeviceDatabase(App.getInstance())
        deviceInfo = db.queryToDevice(codename, model)
    }

    private fun blockingDeviceName(): String {
        return Observable.create(ObservableOnSubscribe<String> { emitter ->
            queryToDevice()
            emitter.onNext(deviceInfo!!.marketName)
        }).blockingFirst(Build.MODEL) as String
    }

    companion object {
        @JvmField
        val instance = DeviceNameGetter()
    }
}