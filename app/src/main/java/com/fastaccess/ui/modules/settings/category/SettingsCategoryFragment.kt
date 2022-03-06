package com.fastaccess.ui.modules.settings.category

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.bumptech.glide.Glide
import com.fastaccess.App
import com.fastaccess.R
import com.fastaccess.data.dao.SettingsModel
import com.fastaccess.data.dao.SettingsModel.SettingsType
import com.fastaccess.data.dao.model.SearchHistory
import com.fastaccess.helper.*
import com.fastaccess.provider.tasks.notification.NotificationSchedulerJobTask
import com.fastaccess.ui.base.mvp.BaseMvp.FAView
import com.fastaccess.ui.modules.settings.sound.NotificationSoundBottomSheet.Companion.newInstance
import com.fastaccess.ui.modules.settings.sound.NotificationSoundMvp.NotificationSoundListener
import com.fastaccess.ui.widgets.SpannableBuilder
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import es.dmoral.toasty.Toasty
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class SettingsCategoryFragment : PreferenceFragmentCompat(), Preference.OnPreferenceChangeListener,
    NotificationSoundListener {
    interface SettingsCallback {
        @get:SettingsType
        val settingsType: Int
    }

    private var callback: FAView? = null
    private var appColor: String? = null
    private var appLanguage: String? = null
    private var notificationTime: Preference? = null
    private var notificationRead: Preference? = null
    private var notificationSound: Preference? = null
    private var notificationSoundPath: Preference? = null
    private var settingsCallback: SettingsCallback? = null
    private val disposable = CompositeDisposable()
    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = context as FAView
        settingsCallback = context as SettingsCallback
        appColor = PrefHelper.getString("appColor")
        appLanguage = PrefHelper.getString("app_language")
    }

    override fun onDetach() {
        callback = null
        settingsCallback = null
        super.onDetach()
    }

    override fun onCreatePreferences(savedInstanceState: Bundle, rootKey: String) {
        when (settingsCallback!!.settingsType) {
            SettingsModel.BACKUP -> addBackup()
            SettingsModel.BEHAVIOR -> addBehaviour()
            SettingsModel.CUSTOMIZATION -> addCustomization()
            SettingsModel.NOTIFICATION -> addNotifications()
            else -> Toast.makeText(
                App.getInstance(),
                "You reached the impossible :'(",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onPreferenceChange(preference: Preference, newValue: Any): Boolean {
        when {
            preference.key.equals("notificationEnabled", ignoreCase = true) -> {
                if (newValue as Boolean) {
                    preferenceScreen.addPreference(notificationTime)
                    preferenceScreen.addPreference(notificationRead)
                    preferenceScreen.addPreference(notificationSound)
                    preferenceScreen.addPreference(notificationSoundPath)
                    NotificationSchedulerJobTask.scheduleJob(
                        App.getInstance(),
                        PrefGetter.getNotificationTaskDuration(), true
                    )
                } else {
                    preferenceScreen.removePreference(notificationTime)
                    preferenceScreen.removePreference(notificationRead)
                    preferenceScreen.removePreference(notificationSound)
                    preferenceScreen.removePreference(notificationSoundPath)
                    NotificationSchedulerJobTask.scheduleJob(App.getInstance(), -1, true)
                }
                return true
            }
            preference.key.equals("notificationTime", ignoreCase = true) -> {
                NotificationSchedulerJobTask.scheduleJob(
                    App.getInstance(),
                    PrefGetter.notificationDurationMillis((newValue as String)), true
                )
                return true
            }
            preference.key.equals("recylerViewAnimation", ignoreCase = true) -> {
                callback!!.onThemeChanged()
                return true
            }
            preference.key.equals("rect_avatar", ignoreCase = true) -> {
                callback!!.onThemeChanged()
                return true
            }
            preference.key.equals("appColor", ignoreCase = true) -> {
                if (newValue.toString().equals(appColor, ignoreCase = true)) return true
                Toasty.warning(
                    App.getInstance(),
                    getString(R.string.change_theme_warning),
                    Toast.LENGTH_LONG
                ).show()
                callback!!.onThemeChanged()
                return true
            }
            preference.key.equals("app_language", ignoreCase = true) -> {
                if (newValue.toString().equals(appLanguage, ignoreCase = true)) return true
                callback!!.onThemeChanged()
                return true
            }
            else -> return false
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (permissions[0] == Manifest.permission.WRITE_EXTERNAL_STORAGE) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    val settings = PrefHelper.getAll()
                    settings.remove("token")
                    val json = Gson().toJson(settings)
                    val path = Environment.getExternalStorageDirectory()
                        .toString() + File.separator + "FastHub"
                    val folder = File(path)
                    folder.mkdirs()
                    val backup = File(folder, "backup.json")
                    try {
                        backup.createNewFile()
                        val outputStream = FileOutputStream(backup)
                        val myOutWriter = OutputStreamWriter(outputStream)
                        myOutWriter.append(json)
                        myOutWriter.close()
                        outputStream.flush()
                        outputStream.close()
                    } catch (e: IOException) {
                        Log.e(tag, "Couldn't backup: $e")
                    }
                    PrefHelper.set(
                        "backed_up", SimpleDateFormat("MM/dd", Locale.ENGLISH).format(
                            Date()
                        )
                    )
                    findPreference("backup").summary =
                        getString(R.string.backup_summary, getString(R.string.now))
                    Toasty.success(App.getInstance(), getString(R.string.backed_up)).show()
                } else {
                    Toasty.error(App.getInstance(), getString(R.string.permission_failed)).show()
                }
            } else if (permissions[0] == Manifest.permission.READ_EXTERNAL_STORAGE) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showFileChooser()
                } else {
                    Toasty.error(App.getInstance(), getString(R.string.permission_failed)).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == RESTORE_REQUEST_CODE) {
                restoreData(data)
            } else if (requestCode == SOUND_REQUEST_CODE) {
                val ringtone =
                    data!!.getParcelableExtra<Uri>(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
                if (notificationSoundPath != null && notificationSoundPath!!.isVisible) {
                    notificationSoundPath!!.setDefaultValue(ringtone.toString())
                }
            }
        }
    }

    override fun onSoundSelected(uri: Uri?) {
        PrefGetter.setNotificationSound(uri!!)
        if (notificationSoundPath != null && notificationSoundPath!!.isVisible) notificationSoundPath!!.summary =
            FileHelper.getRingtoneName(requireContext(), uri)
    }

    override fun onDestroyView() {
        disposable.clear()
        super.onDestroyView()
    }

    override fun onDestroy() {
        disposable.clear()
        super.onDestroy()
    }

    private fun showFileChooser() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "application/json"
        startActivityForResult(
            Intent.createChooser(intent, getString(R.string.select_backup)),
            RESTORE_REQUEST_CODE
        )
    }

    private fun addBackup() {
        addPreferencesFromResource(R.xml.backup_settings)
        findPreference("backup").onPreferenceClickListener =
            Preference.OnPreferenceClickListener {
                if (ContextCompat.checkSelfPermission(
                        requireActivity(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    val preferences = PrefHelper.getAll()
                    preferences.remove("token")
                    val json = Gson().toJson(preferences)
                    val path = FileHelper.PATH
                    val folder = File(path)
                    folder.mkdirs()
                    val backup = File(folder, "backup.json")
                    try {
                        backup.createNewFile()
                        FileOutputStream(backup).use { outputStream ->
                            OutputStreamWriter(
                                outputStream
                            ).use { myOutWriter -> myOutWriter.append(json) }
                        }
                    } catch (e: IOException) {
                        Log.e(tag, "Couldn't backup: $e")
                    }
                    PrefHelper.set(
                        "backed_up",
                        SimpleDateFormat("MM/dd", Locale.ENGLISH).format(Date())
                    )
                    Toasty.success(App.getInstance(), getString(R.string.backed_up)).show()
                } else {
                    requestPermissions(
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        PERMISSION_REQUEST_CODE
                    )
                }
                true
            }
        if (PrefHelper.getString("backed_up") != null) {
            findPreference("backup").summary = SpannableBuilder.builder()
                .append(getString(R.string.backup_summary, PrefHelper.getString("backed_up")))
                .append("\n")
                .append(FileHelper.PATH)
        } else {
            findPreference("backup").summary = ""
        }
        findPreference("restore").onPreferenceClickListener =
            Preference.OnPreferenceClickListener {
                if (ContextCompat.checkSelfPermission(
                        requireActivity(),
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    showFileChooser()
                } else {
                    requestPermissions(
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        PERMISSION_REQUEST_CODE
                    )
                }
                true
            }
    }

    private fun addCustomization() {
        addPreferencesFromResource(R.xml.customization_settings)
        findPreference("enable_ads").isVisible = false
        findPreference("recylerViewAnimation").onPreferenceChangeListener = this
        findPreference("rect_avatar").onPreferenceChangeListener = this
        findPreference("appColor").onPreferenceChangeListener = this
    }

    private fun addBehaviour() {
        addPreferencesFromResource(R.xml.behaviour_settings)
        findPreference("clear_search").onPreferenceClickListener =
            Preference.OnPreferenceClickListener {
                callback!!.showMessage(R.string.success, R.string.deleted)
                SearchHistory.deleteAll()
                true
            }
        findPreference("clear_image_cache").onPreferenceClickListener =
            Preference.OnPreferenceClickListener {
                val glide = Glide.get(App.getInstance())
                disposable.add(RxHelper.getObservable(Observable.fromCallable {
                    glide.clearDiskCache()
                    true
                }).subscribe {
                    glide.clearMemory()
                    Toasty.info(
                        App.getInstance(),
                        getString(R.string.restart_app_message),
                        Toast.LENGTH_LONG
                    ).show()
                })
                true
            }
    }

    private fun addNotifications() {
        addPreferencesFromResource(R.xml.notification_settings)
        notificationTime = findPreference("notificationTime")
        notificationRead = findPreference("markNotificationAsRead")
        notificationSound = findPreference("notificationSound")
        notificationTime?.onPreferenceChangeListener = this
        findPreference("notificationEnabled").onPreferenceChangeListener = this
        notificationSoundPath = findPreference("notification_sound_path")
        notificationSoundPath?.summary = FileHelper.getRingtoneName(
            requireContext(),
            PrefGetter.getNotificationSound()
        )
        notificationSoundPath?.setOnPreferenceClickListener {
            newInstance(
                    FileHelper.getRingtoneName(
                        requireContext(),
                        PrefGetter.getNotificationSound()
                    )
                )
                    .show(childFragmentManager, "NotificationSoundBottomSheet")
                true
            }
        if (!PrefHelper.getBoolean("notificationEnabled")) {
            preferenceScreen.removePreference(notificationTime)
            preferenceScreen.removePreference(notificationRead)
            preferenceScreen.removePreference(notificationSound)
            preferenceScreen.removePreference(notificationSoundPath)
        }
    }

    private fun restoreData(data: Intent?) {
        val json = StringBuilder()
        try {
            requireContext().contentResolver.openInputStream(data!!.data!!).use { inputStream ->
                if (inputStream != null) {
                    BufferedReader(InputStreamReader(inputStream)).use { reader ->
                        var line: String?
                        while (reader.readLine().also { line = it } != null) {
                            json.append(line)
                        }
                    }
                }
            }
        } catch (e: IOException) {
            Toasty.error(App.getInstance(), getString(R.string.error)).show()
        }
        if (!InputHelper.isEmpty(json)) {
            try {
                val gson = Gson()
                val typeOfHashMap = object : TypeToken<Map<String?, *>?>() {}.type
                val savedPref = gson.fromJson<Map<String, *>>(json.toString(), typeOfHashMap)
                if (savedPref != null && savedPref.isNotEmpty()) {
                    for ((key, value) in savedPref) {
                        PrefHelper.set(key, value)
                    }
                }
                callback!!.onThemeChanged()
            } catch (ignored: Exception) {
                Toasty.error(App.getInstance(), getString(R.string.error), Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    companion object {
        val TAG: String = SettingsCategoryFragment::class.java.simpleName
        private const val PERMISSION_REQUEST_CODE = 128
        private const val RESTORE_REQUEST_CODE = 256
        private const val SOUND_REQUEST_CODE = 257
    }
}