package unzipfiles.filecompressor.archive.rar.zip.utils

import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import unzipfiles.filecompressor.archive.rar.zip.R

class RemoteConfig {

    companion object {
        var version = "loading_language"
    }
    //loading_theme
    //loading_language

    fun initRemoteConfig(listener: OnCompleteListener<Boolean>) {
        val mFirebaseRemoteConfig: FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings: FirebaseRemoteConfigSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(3600)
            .build()
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings)
        mFirebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
        mFirebaseRemoteConfig.fetchAndActivate().addOnCompleteListener(listener)
    }

    fun getRemoteVersion(key: String): String {
        val mFirebaseRemoteConfig: FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        return mFirebaseRemoteConfig.getString(key)
    }
}