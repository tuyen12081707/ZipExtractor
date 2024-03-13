package unzipfiles.filecompressor.archive.rar.zip.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.codemybrainsout.ratingdialog.RatingDialog
import com.vapp.admoblibrary.ads.AppOpenManager
import unzipfiles.filecompressor.archive.rar.zip.MyApplication
import unzipfiles.filecompressor.archive.rar.zip.R
import unzipfiles.filecompressor.archive.rar.zip.ads.AdsManager
import unzipfiles.filecompressor.archive.rar.zip.ads.Utils
import unzipfiles.filecompressor.archive.rar.zip.command.Command
import unzipfiles.filecompressor.archive.rar.zip.databinding.ActivityInternalStorageBinding
import unzipfiles.filecompressor.archive.rar.zip.fragment.InternalStorageFragment
import unzipfiles.filecompressor.archive.rar.zip.utils.*
import unzipfiles.filecompressor.archive.rar.zip.utils.Constants.EXTRA_PATH
import java.io.File


class InternalStorageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInternalStorageBinding
    private val fragments = mutableListOf<InternalStorageFragment>()
    private var path = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.setThemeForActivity(this)
        binding = ActivityInternalStorageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }

        AdsManager.loadNative(this, AdsManager.nativeProcess)
        AdsManager.loadNative(this, AdsManager.nativeDirectory)

//        if (MyApplication.showRate == 1) {
//            showRate()
//            MyApplication.showRate = 2
//        }

        path = intent?.extras?.getString(EXTRA_PATH) ?: Constants.EXTERNAL_DIRECTORY.path

        intent?.data?.let { uri ->
            runTryCatch {
                val util = IntentFilterUtils(this)
                val file = File(util.getFilePathForN(uri))
                if (file.exists() && FileUtility.isArchiver(file)) {
                    Command.showExtractDialog(this, this, listOf(file), this)
                } else {
                    toast(getString(R.string.file_not_supported))
                    finish()
                }
            }
        }

        val frag = InternalStorageFragment.newInstance(path)
        supportFragmentManager.beginTransaction().replace(R.id.frContainer, frag).commit()
        fragments.add(frag)
    }

    private fun toastAndOpenDestination() {
        toast(getString(R.string.success))
        val destinationFolder = MyApplication.instance.folder.value ?: return
        val i = Intent(this, InternalStorageActivity::class.java)
        i.putExtra(EXTRA_PATH, destinationFolder)
        startActivity(i)
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            fragments.removeLastOrNull()
            supportFragmentManager.popBackStackImmediate()
            fragments[fragments.size - 1].scanForFiles()
        } else {
            super.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        AppOpenManager.getInstance().enableAppResumeWithActivity(InternalStorageActivity::class.java)

    }

    fun addFragment(frag: InternalStorageFragment) {
        fragments.add(frag)
        supportFragmentManager
            .beginTransaction()
            .addToBackStack(frag.id.toString())
            .add(R.id.frContainer, frag)
//            .replace(R.id.frContainer, frag)
            .commit()
    }

    private fun showRate() {
        AppOpenManager.getInstance().disableAppResumeWithActivity(InternalStorageActivity::class.java)
        RatingDialog.Builder(this)
            .session(1)
            .date(1)
            .setNameApp(getString(R.string.app_name))
            .setIcon(R.mipmap.ic_launcher)
            .setEmail("3xiappsp@gmail.com")
            .isShowButtonLater(true)
            .isClickLaterDismiss(true)
            .setOnlickRate {
                AppOpenManager.getInstance().disableAppResumeWithActivity(InternalStorageActivity::class.java)
            }
            .setOnlickMaybeLate {
                AppOpenManager.getInstance().enableAppResumeWithActivity(javaClass)
            }
            .ratingButtonColor(R.color.colorPrimary)
            .build()
            .show()
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