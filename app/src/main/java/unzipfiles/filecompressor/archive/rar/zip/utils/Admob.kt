package unzipfiles.filecompressor.archive.rar.zip.utils

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.google.firebase.analytics.FirebaseAnalytics
import com.vapp.admoblibrary.ads.AdmodUtils
import com.vapp.admoblibrary.ads.AppOpenManager
import com.vapp.admoblibrary.ads.NativeAdCallback
import com.vapp.admoblibrary.ads.admobnative.enumclass.GoogleENative
import unzipfiles.filecompressor.archive.rar.zip.BuildConfig
import unzipfiles.filecompressor.archive.rar.zip.R

enum class Ads(val adName: String = "", val adId: String) {

    INTER_SPLASH_1("inter_splash_1", "ca-app-pub-8475252859305547/6568229073"),
    INTER_SPLASH_2("inter_splash_2", "ca-app-pub-8475252859305547/1375364729"),
    INTER_SPLASH_3("inter_splash_3", "ca-app-pub-8475252859305547/5123038042"),

    INTER_ARCHIVER_1("inter_archiver_1", "ca-app-pub-8475252859305547/8331655313"),
    INTER_ARCHIVER_2("inter_archiver_2", "ca-app-pub-8475252859305547/1998143263"),
    INTER_ARCHIVER_3("inter_archiver_3", "ca-app-pub-8475252859305547/5745816586"),

    INTER_IMAGES_1("inter_images_1", "ca-app-pub-8475252859305547/6244548026"),
    INTER_IMAGES_2("inter_images_2", "ca-app-pub-8475252859305547/5705491973"),
    INTER_IMAGES_3("inter_images_3", "ca-app-pub-8475252859305547/7366058009"),

    INTER_VIDEOS_1("inter_videos_1", "ca-app-pub-8475252859305547/2113731328"),
    INTER_VIDEOS_2("inter_videos_2", "ca-app-pub-8475252859305547/4983399253"),
    INTER_VIDEOS_3("inter_videos_3", "ca-app-pub-8475252859305547/6001005448"),

    INTER_AUDIOS_1("inter_audios_1", "ca-app-pub-8475252859305547/1970508517"),
    INTER_AUDIOS_2("inter_audios_2", "ca-app-pub-8475252859305547/9657426841"),
    INTER_AUDIOS_3("inter_audios_3", "ca-app-pub-8475252859305547/6847430675"),

    INTER_DOCUMENTS_1("inter_document_1", "ca-app-pub-8475252859305547/7988836549"),
    INTER_DOCUMENTS_2("inter_document_2", "ca-app-pub-8475252859305547/2628984060"),
    INTER_DOCUMENTS_3("inter_document_3", "ca-app-pub-8475252859305547/1918537625"),

    INTER_DOWNLOAD_1("inter_download_1", "ca-app-pub-8475252859305547/6675754870"),
    INTER_DOWNLOAD_2("inter_download_2", "ca-app-pub-8475252859305547/8292374280"),
    INTER_DOWNLOAD_3("inter_download_3", "ca-app-pub-8475252859305547/2908185660"),

    INTER_PROCESS_1("inter_process_1", "ca-app-pub-8475252859305547/6979292618"),
    INTER_PROCESS_2("inter_process_2", "ca-app-pub-8475252859305547/1315902398"),
    INTER_PROCESS_3("inter_process_3", "ca-app-pub-8475252859305547/9002820726"),

    INTER_DIRECTORY_1("inter_directory_1", "ca-app-pub-8475252859305547/4120947566"),
    INTER_DIRECTORY_2("inter_directory_2", "ca-app-pub-8475252859305547/6376371973"),
    INTER_DIRECTORY_3("inter_directory_3", "ca-app-pub-8475252859305547/2807865896"),

    NATIVE_HOME_1("native_home_1", "ca-app-pub-8475252859305547/9383748381"),
    NATIVE_HOME_2("native_home_2", "ca-app-pub-8475252859305547/4357474724"),
    NATIVE_HOME_3("native_home_3", "ca-app-pub-8475252859305547/4352881853"),

    NATIVE_PROCESS_1("native_process_1", "ca-app-pub-8475252859305547/5274801735"),
    NATIVE_PROCESS_2("native_process_2", "ca-app-pub-8475252859305547/1731311382"),
    NATIVE_PROCESS_3("native_process_3", "ca-app-pub-8475252859305547/7912852917"),

    NATIVE_LANGUAGE_1("native_language_1", "ca-app-pub-8475252859305547/3961720066"),
    NATIVE_LANGUAGE_2("native_language_2", "ca-app-pub-8475252859305547/4569227719"),
    NATIVE_LANGUAGE_3("native_language_3", "ca-app-pub-8475252859305547/3495343179"),

    NATIVE_ID_1("native_id_1", "ca-app-pub-8475252859305547/1989041654"),
    NATIVE_ID_2("native_id_2", "ca-app-pub-8475252859305547/7899843888"),
    NATIVE_ID_3("native_id_3", "ca-app-pub-8475252859305547/6623783989"),

    NATIVE_DIRECTORY_1("native_directory_1", "ca-app-pub-8475252859305547/5286689570"),
    NATIVE_DIRECTORY_2("native_directory_2", "ca-app-pub-8475252859305547/9034362897"),
    NATIVE_DIRECTORY_3("native_directory_3", "ca-app-pub-8475252859305547/1371457308"),

    ON_RESUME("on_resume_id", "ca-app-pub-8475252859305547/6757585044")
}

fun Context.logEvents(eventName: String) {
    var verson = "0"
    if(RemoteConfig.version == "loading_theme"){
        verson = "base"
    }else if(RemoteConfig.version == "loading_language"){
        verson = "A"
    }
    Log.d("===Event", eventName!!+"_"+ BuildConfig.VERSION_CODE+"_"+verson)
    val firebaseAnalytics = FirebaseAnalytics.getInstance(this)
    val bundle = Bundle()
    bundle.putString("event", javaClass.simpleName)
    firebaseAnalytics.logEvent(eventName+"_"+ BuildConfig.VERSION_CODE+"_"+verson, bundle)
}

fun disableOnResume(activity: Class<*>) {
    AppOpenManager.getInstance().disableAppResumeWithActivity(activity)
}
