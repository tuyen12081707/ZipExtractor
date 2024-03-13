package unzipfiles.filecompressor.archive.rar.zip.activity

import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.bumptech.glide.Glide
import com.tenjin.android.TenjinSDK
import com.vapp.admoblibrary.ads.AdmodUtils
import unzipfiles.filecompressor.archive.rar.zip.BuildConfig
import unzipfiles.filecompressor.archive.rar.zip.R
import unzipfiles.filecompressor.archive.rar.zip.ads.AdsManager
import unzipfiles.filecompressor.archive.rar.zip.utils.*
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import unzipfiles.filecompressor.archive.rar.zip.ads.Utils


class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.setThemeForActivity(this)
        setContentView(R.layout.activity_splash)
        if (!isTaskRoot && intent.hasCategory(Intent.CATEGORY_LAUNCHER) &&
            intent.action != null && intent.action == Intent.ACTION_MAIN
        ) {
            finish()
            return
        }

        val remoteConfig = RemoteConfig()
        if (AdmodUtils.isNetworkConnected(this)) {
            remoteConfig.initRemoteConfig {
                RemoteConfig.version = remoteConfig.getRemoteVersion("test_loading_splash")
                AdsManager.checkABIdAds()
                if (Utils.getFirstOpen(this)) {
                    logEvents("first_open")
                    Utils.setFirstOpen(this,false)
                } else {
                    logEvents("open_app")
                }
                nextActivity()
            }
        } else {
            if (Utils.getFirstOpen(this)) {
                logEvents("first_open")
                Utils.setFirstOpen(this,false)
            } else {
                logEvents("open_app")
            }
            nextActivity()
        }

        setupRate()
        Utils.setShowRateFirstOpen(this,true)
        Utils.setShowRateMain(this,true)

        getMeteData()
    }

    private fun nextActivity() {
        AdsManager.loadNative(this,AdsManager.nativeLanguage)
        Handler().postDelayed(Runnable {
            checkAbTestNextActivity()
        }, 5000)
    }

    private fun checkAbTestNextActivity() {
        when(RemoteConfig.version){
            "loading_theme" -> {
                replaceActivity(ThemeScreenActivity::class.java)
            }
            else -> {
                var intent = Intent(this,LanguageChangeActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.putExtra("open",true)
                startActivity(intent)
            }
        }
    }

    private fun setupRate() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val isShowRate = prefs.getBoolean(Constants.IS_SHOW_RATE, true)
        prefs.edit().putBoolean(Constants.IS_SHOW_RATE, !isShowRate).apply()
    }

    private fun getMeteData() {
        val info: PackageInfo
        try {
            info = packageManager.getPackageInfo(
                BuildConfig.APPLICATION_ID,
                PackageManager.GET_SIGNATURES
            )
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val something = String(Base64.encode(md.digest(), 0))
                //                String something = new String(Base64.encodeBytes(md.digest()));
                Log.e("hash_key", something)
            }
        } catch (e1: PackageManager.NameNotFoundException) {
            Log.e("name not found", e1.toString())
        } catch (e: NoSuchAlgorithmException) {
            Log.e("no such an algorithm", e.toString())
        } catch (e: Exception) {
            Log.e("exception", e.toString())
        }
    }
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }
}