package unzipfiles.filecompressor.archive.rar.zip.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.jakewharton.rxbinding4.view.clicks
import unzipfiles.filecompressor.archive.rar.zip.adapter.SwitchLanguageAdapter
import unzipfiles.filecompressor.archive.rar.zip.ads.AdsManager
import unzipfiles.filecompressor.archive.rar.zip.ads.Utils
import unzipfiles.filecompressor.archive.rar.zip.databinding.ActivityLanguageBinding
import unzipfiles.filecompressor.archive.rar.zip.model.SelectLanguageModel
import unzipfiles.filecompressor.archive.rar.zip.utils.Common
import unzipfiles.filecompressor.archive.rar.zip.utils.RemoteConfig
import unzipfiles.filecompressor.archive.rar.zip.utils.logEvents
import unzipfiles.filecompressor.archive.rar.zip.utils.replaceActivity
import java.util.concurrent.TimeUnit


class LanguageChangeActivity : AppCompatActivity() {

    lateinit var binding: ActivityLanguageBinding
    var listLanguage: ArrayList<SelectLanguageModel>? = null
    var languageAdapter: SwitchLanguageAdapter? = null
    var open = false

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.setThemeForActivity(this)
        binding = ActivityLanguageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        logEvents("language_screen")
        open = intent.getBooleanExtra("open",false)

        if(open){
            binding.ivBack.visibility = View.GONE
        }else{
            binding.ivBack.visibility = View.VISIBLE
        }

        AdsManager.loadInter(this,AdsManager.interLanguage)
        AdsManager.loadNative(this,AdsManager.nativeIntro)

        listLanguage = Common.getListLocation(this)
        var position = Common.getLocationPosition(this)

        for (i in 0 until listLanguage!!.size) {
            listLanguage!![i].selected = false
        }
        listLanguage!![position].selected = true

        languageAdapter =
            SwitchLanguageAdapter(this, listLanguage, SwitchLanguageAdapter.OnItemClickListener {
                position = it
            })

        binding.rcvLanguage.layoutManager = GridLayoutManager(this, 2)
        binding.rcvLanguage.adapter = languageAdapter

        binding.ivBack.clicks().throttleFirst(1, TimeUnit.SECONDS).subscribe() {
            onBackPressed()
        }

        binding.ivDone.clicks().throttleFirst(1, TimeUnit.SECONDS).subscribe() {
                logEvents("btn_done_language")
                Common.setFirstOpen(false, applicationContext)
                Common.setLocationPosition(this@LanguageChangeActivity, position)
                if(open){
                    checkAbTest()
                }else{
                    val intent = Intent(this@LanguageChangeActivity, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
        }
        AdsManager.showNativeTopButton(this@LanguageChangeActivity,AdsManager.nativeLanguage,binding.frNative)
    }

    private fun checkAbTest() {
        when(RemoteConfig.version){
            "loading_theme" -> {
                nextActivity()
            }
            else -> {
                AdsManager.showAdInter(this,AdsManager.interLanguage,object : AdsManager.AdListenerNew{
                    override fun onAdClosed() {
                        nextActivity()
                    }

                    override fun onFailed() {
                        nextActivity()
                    }

                },"inter_save_language")
            }
        }
    }
    private fun nextActivity(){
        val intent = Intent(this@LanguageChangeActivity, IntroActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
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