package unzipfiles.filecompressor.archive.rar.zip.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import unzipfiles.filecompressor.archive.rar.zip.R
import unzipfiles.filecompressor.archive.rar.zip.ads.AdsManager
import unzipfiles.filecompressor.archive.rar.zip.ads.Utils
import unzipfiles.filecompressor.archive.rar.zip.databinding.ActivityCategoriesBinding
import unzipfiles.filecompressor.archive.rar.zip.fragment.CategoryFragment
import unzipfiles.filecompressor.archive.rar.zip.fragment.DocumentFragment
import unzipfiles.filecompressor.archive.rar.zip.utils.Constants
import unzipfiles.filecompressor.archive.rar.zip.utils.Type

class CategoriesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCategoriesBinding
    private var type: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.setThemeForActivity(this)
        binding = ActivityCategoriesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AdsManager.loadNative(this,AdsManager.nativeId)
        AdsManager.loadNative(this,AdsManager.nativeDirectory)

        type = intent?.extras?.getString(Constants.EXTRA_TYPE) ?: ""
        val title = when (type) {
            Type.ARCHIVER.name -> getString(R.string.archiver)
            Type.AUDIOS.name -> getString(R.string.audios)
            Type.IMAGES.name -> getString(R.string.images)
            Type.VIDEOS.name -> getString(R.string.videos)
            Type.DOCUMENTS.name -> getString(R.string.document)
            else -> getString(R.string.download)
        }
        binding.tvTitle.text = title.lowercase().capitalize()
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        if (type == Type.DOCUMENTS.name) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.frContainer, DocumentFragment()).commit()
        } else {
            supportFragmentManager.beginTransaction()
                .replace(R.id.frContainer, CategoryFragment.newInstance(type)).commit()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
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