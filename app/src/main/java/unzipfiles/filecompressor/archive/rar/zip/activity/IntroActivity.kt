package unzipfiles.filecompressor.archive.rar.zip.activity

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.jakewharton.rxbinding4.view.clicks
import com.vapp.admoblibrary.ads.AppOpenManager
import unzipfiles.filecompressor.archive.rar.zip.R
import unzipfiles.filecompressor.archive.rar.zip.ads.AdsManager
import unzipfiles.filecompressor.archive.rar.zip.ads.Utils
import unzipfiles.filecompressor.archive.rar.zip.databinding.ActivityIntroBinding
import unzipfiles.filecompressor.archive.rar.zip.databinding.DialogConnectionsBinding
import unzipfiles.filecompressor.archive.rar.zip.fragment.IntroFragment
import unzipfiles.filecompressor.archive.rar.zip.utils.logEvents
import unzipfiles.filecompressor.archive.rar.zip.utils.setVisible
import java.util.concurrent.TimeUnit


class IntroActivity : AppCompatActivity() {
    private lateinit var binding: ActivityIntroBinding
    private var position = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.setThemeForActivity(this)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AdsManager.loadNative(this, AdsManager.nativeHome)
        AdsManager.loadInter(this,AdsManager.interIntro)
        AdsManager.showNativeTopButton(this,AdsManager.nativeIntro,binding.frNative)

//        binding.tvSkip.text = getString(R.string.skip) +" "
        setupView()
    }

    override fun onBackPressed() {
        if (position > 0) {
            binding.vpContainer.currentItem = position - 1
        } else {
            super.onBackPressed()
        }
    }

    private fun setupView() {
//        binding.tvSkip.setOnClickListener {
//            AppOpenManager.getInstance().isAppResumeEnabled = true
//            it.isEnabled = false
//            logEvents("btn_skip")
//        }
        binding.vpContainer.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                this@IntroActivity.position = position
                logEvents("intro${position.plus(1)}")
//                binding.tvSkip.setVisible(position != 2)
            }
        })
        binding.vpContainer.isUserInputEnabled = false
        binding.vpContainer.adapter = ViewPager2Adapter(this)
        binding.dotsIndicator.attachTo(binding.vpContainer)
        binding.btnNext.setOnClickListener {
            if (binding.vpContainer.currentItem == 0) {
                binding.vpContainer.currentItem = position + 1
            }else if(binding.vpContainer.currentItem == 1){
                AdsManager.showAdInter(this,AdsManager.interIntro,object : AdsManager.AdListenerNew{
                    override fun onAdClosed() {
                        binding.vpContainer.currentItem = position + 1
                        binding.btnNext.text = resources.getText(R.string.startHoa)
                    }
                    override fun onFailed() {
                        binding.vpContainer.currentItem = position + 1
                        binding.btnNext.text = resources.getText(R.string.startHoa)
                    }
                },"inter_intro2")
            }else {
//                it.isEnabled = false
                val intent = Intent(this@IntroActivity, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        AppOpenManager.getInstance().enableAppResumeWithActivity(IntroActivity::class.java)
        binding.btnNext.isEnabled = true
//        binding.tvSkip.isEnabled = true
    }

    private fun showDialogConnect() {
        AppOpenManager.getInstance().disableAppResumeWithActivity(IntroActivity::class.java)
        val dialogBinding = DialogConnectionsBinding.inflate(layoutInflater)
        val alertDialog = AlertDialog.Builder(this).setView(dialogBinding.root).create()
        dialogBinding.btnConnect.clicks().throttleFirst(1, TimeUnit.SECONDS).subscribe {
            alertDialog.dismiss()
            startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
        }
        alertDialog.setCancelable(false)
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()
    }

    open class ViewPager2Adapter(activity: FragmentActivity) :
        FragmentStateAdapter(activity) {
        override fun createFragment(position: Int): Fragment {
            return IntroFragment.newInstance(position)
        }

        override fun getItemCount(): Int {
            return 3
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