package unzipfiles.filecompressor.archive.rar.zip

import android.app.Activity
import android.app.Application
import android.content.res.Resources
import android.os.Bundle
import android.os.StrictMode
import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.Utils
import com.vapp.admoblibrary.ads.AdmodUtils
import com.vapp.admoblibrary.ads.AppOpenManager
import unzipfiles.filecompressor.archive.rar.zip.activity.SplashActivity
import unzipfiles.filecompressor.archive.rar.zip.ads.AdsManager
import unzipfiles.filecompressor.archive.rar.zip.model.SelectLanguageModel
import unzipfiles.filecompressor.archive.rar.zip.utils.Common
import java.io.File
import java.util.*

class MyApplication : Application() , Application.ActivityLifecycleCallbacks{
    private val list = mutableListOf<File>()
    val selectedFiles = MutableLiveData<MutableList<File>>().apply { value = list }
    val folder = MutableLiveData<String>()

    override fun onCreate() {
        super.onCreate()
        instance = this
        AdmodUtils.initAdmob(this, 10000, true, false)
        AppOpenManager.getInstance().init(this, AdsManager.onResumeID)
        AppOpenManager.getInstance().disableAppResumeWithActivity(SplashActivity::class.java)
        Utils.init(this)
        registerActivityLifecycleCallbacks(this)
        //* For sharing files
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
    }

    companion object {
        lateinit var instance: MyApplication
            private set

        fun addFile(file: File) {
            instance.list.add(file)
            instance.selectedFiles.postValue(instance.list)
        }

        fun removeFile(file: File) {
            instance.list.remove(file)
            instance.selectedFiles.postValue(instance.list)
        }

        fun removeAll() {
            instance.list.clear()
            instance.selectedFiles.postValue(instance.list)
        }

        fun getSelectedFiles() = instance.list

//        var showRate : Int = 0
    }

    override fun onActivityCreated(activity: Activity, p1: Bundle?) {
        val listLang: ArrayList<SelectLanguageModel> = Common.getListLocation(applicationContext)
        val position: Int = Common.getLocationPosition(activity)
        val langCode: String = listLang[position].langCode
        val locale = Locale(langCode)
        Locale.setDefault(locale)
        val resource: Resources = activity.resources
        val configuration = resource.configuration
        configuration.setLocale(locale)
        resource.updateConfiguration(configuration, resource.displayMetrics)
    }

    override fun onActivityStarted(p0: Activity) {
    }

    override fun onActivityResumed(p0: Activity) {
        if (p0.javaClass != SplashActivity::class.java) {
            AppOpenManager.getInstance().enableAppResumeWithActivity(p0.javaClass)
        }
    }

    override fun onActivityPaused(p0: Activity) {
    }

    override fun onActivityStopped(p0: Activity) {
    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {
    }

    override fun onActivityDestroyed(p0: Activity) {
    }

}