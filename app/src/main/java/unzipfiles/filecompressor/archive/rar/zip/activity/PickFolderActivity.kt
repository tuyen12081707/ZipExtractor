package unzipfiles.filecompressor.archive.rar.zip.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import unzipfiles.filecompressor.archive.rar.zip.MyApplication
import unzipfiles.filecompressor.archive.rar.zip.R
import unzipfiles.filecompressor.archive.rar.zip.ads.Utils
import unzipfiles.filecompressor.archive.rar.zip.databinding.ActivityPickFolderBinding
import unzipfiles.filecompressor.archive.rar.zip.fragment.PickFolderFragment
import unzipfiles.filecompressor.archive.rar.zip.utils.Constants
import unzipfiles.filecompressor.archive.rar.zip.utils.log

class PickFolderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPickFolderBinding
    private val fragments = mutableListOf<PickFolderFragment>()
    private var path = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.setThemeForActivity(this)
        binding = ActivityPickFolderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }
        binding.btnSelect.setOnClickListener {
            MyApplication.instance.folder.postValue(path)
            finish()
        }

        val frag = PickFolderFragment.newInstance(Constants.EXTERNAL_DIRECTORY.path)
        supportFragmentManager.beginTransaction().replace(R.id.frContainer, frag).commit()
        fragments.add(frag)
    }

    fun replaceFragment(frag: PickFolderFragment, path: String) {
        this.path = path
        path.log()
        fragments.add(frag)
        supportFragmentManager
            .beginTransaction()
            .addToBackStack(frag.id.toString())
//            .add(R.id.frContainer, frag)
            .replace(R.id.frContainer, frag)
            .commit()
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