package unzipfiles.filecompressor.archive.rar.zip

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.vapp.admoblibrary.ads.AppOpenManager
import unzipfiles.filecompressor.archive.rar.zip.activity.SplashActivity

object ActivityLifecycleListener : Application.ActivityLifecycleCallbacks {
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {

    }

    override fun onActivityStarted(activity: Activity) {

    }

    override fun onActivityResumed(activity: Activity) {
        if (activity.javaClass != SplashActivity::class.java) {
            AppOpenManager.getInstance().enableAppResumeWithActivity(activity.javaClass)
        }
    }

    override fun onActivityPaused(activity: Activity) {

    }

    override fun onActivityStopped(activity: Activity) {

    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

    }

    override fun onActivityDestroyed(activity: Activity) {

    }

}
