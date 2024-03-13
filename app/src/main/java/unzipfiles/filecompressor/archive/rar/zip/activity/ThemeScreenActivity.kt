package unzipfiles.filecompressor.archive.rar.zip.activity;

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.jakewharton.rxbinding4.view.clicks
import unzipfiles.filecompressor.archive.rar.zip.R
import unzipfiles.filecompressor.archive.rar.zip.ads.AdsManager
import unzipfiles.filecompressor.archive.rar.zip.ads.Utils
import unzipfiles.filecompressor.archive.rar.zip.databinding.ActivityThemeScreenBinding
import unzipfiles.filecompressor.archive.rar.zip.utils.logEvents
import java.util.concurrent.TimeUnit

class ThemeScreenActivity : AppCompatActivity() {
    lateinit var binding: ActivityThemeScreenBinding
    var isLightMode = true
    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.setThemeForActivity(this)
        binding = ActivityThemeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        logEvents("theme_screen")
//        isLightMode = Common.getLightMode(this)

        AdsManager.loadNative(this,AdsManager.nativeIntro)
        AdsManager.loadInter(this,AdsManager.interTheme)

        if (Utils.getTheme(this) == 2) {
            isLightMode = false
            binding.selectDark.setImageResource(R.drawable.ic_select)
            binding.selectLight.setImageResource(R.drawable.ic_no_select)

            binding.txtDark.setTextColor(getColor(R.color.white))
            binding.txtLight.setTextColor(getColor(R.color.white))
            binding.txtname.setTextColor(getColor(R.color.white))
            binding.bgMain.setBackgroundColor(getColor(R.color.black))

        } else {
            isLightMode = true
            binding.selectLight.setImageResource(R.drawable.ic_select)
            binding.selectDark.setImageResource(R.drawable.ic_no_select)

            binding.txtDark.setTextColor(getColor(R.color.black))
            binding.txtLight.setTextColor(getColor(R.color.black))
            binding.txtname.setTextColor(getColor(R.color.black))
            binding.bgMain.setBackgroundColor(getColor(R.color.white))
        }

        binding.lnLight.setOnClickListener {
            logEvents("light_theme")
            isLightMode = true
            binding.txtLight.setTextColor(getColor(R.color.black))
            binding.txtname.setTextColor(getColor(R.color.black))
            binding.bgMain.setBackgroundColor(getColor(R.color.white))
            binding.txtDark.setTextColor(getColor(R.color.black))
            binding.selectLight.setImageResource(R.drawable.ic_select)
            binding.selectDark.setImageResource(R.drawable.ic_no_select)
        }

        binding.lnDark.setOnClickListener {
            logEvents("dark_theme")
            isLightMode = false
            binding.txtDark.setTextColor(getColor(R.color.white))
            binding.txtLight.setTextColor(getColor(R.color.white))
            binding.txtname.setTextColor(getColor(R.color.white))
            binding.bgMain.setBackgroundColor(getColor(R.color.black))
            binding.selectDark.setImageResource(R.drawable.ic_select)
            binding.selectLight.setImageResource(R.drawable.ic_no_select)
        }
        binding.btnApply.clicks().throttleFirst(1,TimeUnit.SECONDS).subscribe {
            logEvents("btn_apply_theme")
            if (isLightMode) {
                Utils.setTheme(this@ThemeScreenActivity, 1)
                logEvents("apply_light_theme")
            } else {
                Utils.setTheme(this@ThemeScreenActivity, 2)
                logEvents("apply_dark_theme")
            }
            nextActivity()
        }
    }
    private fun nextActivity(){
         AdsManager.showAdInter(this,AdsManager.interTheme,object : AdsManager.AdListenerNew{
             override fun onAdClosed() {
                 var intent = Intent(this@ThemeScreenActivity,LanguageChangeActivity::class.java)
                 intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                 intent.putExtra("open",true)
                 startActivity(intent)
             }

             override fun onFailed() {
                 var intent = Intent(this@ThemeScreenActivity,LanguageChangeActivity::class.java)
                 intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                 intent.putExtra("open",true)
                 startActivity(intent)
             }

         },"inter_apply_theme")
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