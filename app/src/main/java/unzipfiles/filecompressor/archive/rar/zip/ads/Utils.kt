package unzipfiles.filecompressor.archive.rar.zip.ads

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import com.codemybrainsout.ratingdialog.MaybeLaterCallback
import com.codemybrainsout.ratingdialog.RateButtonCallback
import com.codemybrainsout.ratingdialog.RatingDialog
import com.google.firebase.analytics.FirebaseAnalytics
import com.vapp.admoblibrary.ads.AppOpenManager
import unzipfiles.filecompressor.archive.rar.zip.BuildConfig
import unzipfiles.filecompressor.archive.rar.zip.R
import unzipfiles.filecompressor.archive.rar.zip.utils.RemoteConfig

object Utils {

    fun setFirstOpen(context: Context, open: Boolean) {
        val preferences =
            context.getSharedPreferences(context.packageName, Context.MODE_MULTI_PROCESS)
        preferences.edit().putBoolean("KEY_OPEN", open).apply()
    }

    fun getFirstOpen(mContext: Context): Boolean {
        val preferences =
            mContext.getSharedPreferences(mContext.packageName, Context.MODE_MULTI_PROCESS)
        return preferences.getBoolean("KEY_OPEN", true)
    }

    fun logEventFirebase(context: Context, eventName: String?) {
        var verson = "0"
        if(RemoteConfig.version == "loading_theme"){
            verson = "base"
        }else if(RemoteConfig.version == "loading_language"){
            verson = "A"
        }
        Log.d("===Event", eventName!!+"_"+ BuildConfig.VERSION_CODE+"_"+verson)
        val firebaseAnalytics = FirebaseAnalytics.getInstance(context)
        val bundle = Bundle()
        bundle.putString("onEvent", context.javaClass.simpleName)
        firebaseAnalytics.logEvent(eventName +"_"+ BuildConfig.VERSION_CODE+"_"+verson, bundle)
    }

    fun setThemeForActivity(activity: Activity) {
        when (getTheme(activity)) {
            1 -> {
                activity.setTheme(R.style.Theme_AppTheme1)
            }
            2 -> {
                activity.setTheme(R.style.Theme_AppTheme2)
            }
        }
    }

    fun setTheme(context: Context, open: Int) {
        val preferences = context.getSharedPreferences(context.packageName, Context.MODE_MULTI_PROCESS)
        preferences.edit().putInt("KEY_THEME", open).apply()
    }

    fun getTheme(context: Context): Int {
        val preferences = context.getSharedPreferences(context.packageName, Context.MODE_MULTI_PROCESS)
        return preferences.getInt("KEY_THEME", 1)
    }

    fun setShowRateFirstOpen(context: Context, open: Boolean) {
        val preferences = context.getSharedPreferences(context.packageName, Context.MODE_MULTI_PROCESS)
        preferences.edit().putBoolean("RATE_FIRST_OPEN", open).apply()
    }

    fun getShowRateFirstOpen(context: Context): Boolean {
        val preferences = context.getSharedPreferences(context.packageName, Context.MODE_MULTI_PROCESS)
        return preferences.getBoolean("RATE_FIRST_OPEN", true)
    }

    fun setShowRateMain(context: Context, open: Boolean) {
        val preferences = context.getSharedPreferences(context.packageName, Context.MODE_MULTI_PROCESS)
        preferences.edit().putBoolean("RATE_MAIN", open).apply()
    }

    fun getShowRateMain(context: Context): Boolean {
        val preferences = context.getSharedPreferences(context.packageName, Context.MODE_MULTI_PROCESS)
        return preferences.getBoolean("RATE_MAIN", true)
    }

    fun setCountAccessApp(context: Context, open: Int) {
        val preferences =
            context.getSharedPreferences(context.packageName, Context.MODE_MULTI_PROCESS)
        preferences.edit().putInt("KEY_COUNT_BACK_APP", open).apply()
    }

    fun getCountAccessApp(mContext: Context): Int {
        val preferences =
            mContext.getSharedPreferences(mContext.packageName, Context.MODE_MULTI_PROCESS)
        return preferences.getInt("KEY_COUNT_BACK_APP", 1)
    }

    fun showRate(context: Context, exitApp: Boolean) {
        val ratingDialog: RatingDialog = RatingDialog.Builder(context as Activity)
            .session(1)
            .date(1)
            .setNameApp(context.getString(R.string.app_name))
            .setIcon(R.mipmap.ic_launcher)
            .setEmail("3xiappsp@gmail.com")
            .setOnlickRate(RateButtonCallback { rate ->
                AppOpenManager.getInstance().disableAppResumeWithActivity(context.javaClass)
            })
            .ignoreRated(false)
            .isShowButtonLater(true)
            .isClickLaterDismiss(true)
            .setTextButtonLater("Maybe Later")
            .setOnlickMaybeLate(MaybeLaterCallback {
                if (exitApp) context.finish()
            })
            .ratingButtonColor(R.color.colorPrimary)
            .build()
        ratingDialog.setCanceledOnTouchOutside(false)
        ratingDialog.show()
    }
}