package unzipfiles.filecompressor.archive.rar.zip.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.jakewharton.rxbinding4.view.clicks
import com.vapp.admoblibrary.ads.AppOpenManager
import unzipfiles.filecompressor.archive.rar.zip.R
import unzipfiles.filecompressor.archive.rar.zip.ads.AdsManager
import unzipfiles.filecompressor.archive.rar.zip.ads.Utils
import unzipfiles.filecompressor.archive.rar.zip.databinding.ActivityMainAppBinding
import unzipfiles.filecompressor.archive.rar.zip.databinding.DialogDeleteBinding
import unzipfiles.filecompressor.archive.rar.zip.databinding.DialogRequestPermissionBinding
import unzipfiles.filecompressor.archive.rar.zip.fragment.HomeMainFragment
import unzipfiles.filecompressor.archive.rar.zip.utils.disableOnResume
import unzipfiles.filecompressor.archive.rar.zip.utils.gone
import unzipfiles.filecompressor.archive.rar.zip.utils.hasPermission
import unzipfiles.filecompressor.archive.rar.zip.utils.logEvents
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainAppBinding
    private var permissionDialog: AlertDialog? = null
    private var goToSettingDialog: AlertDialog? = null
    private var isRateShowed = false
    private val REQUEST_CODE_PERMISSION = 1212
    private var countAccessApp = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.setThemeForActivity(this)
        binding = ActivityMainAppBinding.inflate(layoutInflater)
        setContentView(binding.root)
        logEvents("home_screen")

        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        setupWithNavController(binding.bottomNav, navController)

        AdsManager.loadNative(this,AdsManager.nativeHome)
        AdsManager.loadNative(this,AdsManager.nativeLanguage)
        AdsManager.loadNative(this,AdsManager.nativeDirectory)
        AdsManager.loadInter(this,AdsManager.interHome)

        // Rate 2,4,6
        if(Utils.getShowRateMain(this)){
            countAccessApp = Utils.getCountAccessApp(this)
            val countBackAppNew: Int = countAccessApp + 1
            Utils.setCountAccessApp(this, countBackAppNew)
            if (countAccessApp % 2 == 0) {
                Utils.showRate(this, false)
                Utils.setShowRateFirstOpen(this,false)
            }
            Utils.setShowRateMain(this,false)
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                HomeMainFragment.binding.frNative.visibility = View.GONE
                showRequestManageStorageDialog(
                    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                        if (Environment.isExternalStorageManager()) {
                            permissionDialog?.dismiss()
//                            AppOpenManager.getInstance().isAppResumeEnabled = true
                            HomeMainFragment.binding.frNative.visibility = View.VISIBLE
                        }
                    })
            }
        } else {
            if (!checkPermission()) {
                HomeMainFragment.binding.frNative.visibility = View.GONE
//                AppOpenManager.getInstance()
//                    .disableAppResumeWithActivity(MainActivity::class.java)
                val binding: DialogRequestPermissionBinding =
                    DialogRequestPermissionBinding.inflate(
                        layoutInflater
                    )
                val builder = AlertDialog.Builder(this)
                builder.setView(binding.root)
                val dialogPermission = builder.create()
                dialogPermission.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                binding.btnAllow.clicks().throttleFirst(1, TimeUnit.SECONDS).subscribe() {
                    dialogPermission.dismiss()
                    requestPermission()
                }
                dialogPermission.setCancelable(false)
                dialogPermission.show()
            }
        }
    }

    private fun requestPermission() {
        AppOpenManager.getInstance().disableAppResumeWithActivity(MainActivity::class.java)
//        AppOpenManager.getInstance().isAppResumeEnabled = false
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            REQUEST_CODE_PERMISSION
        )
    }

    private fun checkPermission(): Boolean {
        val result =
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        val result1 =
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (checkPermission()) {
                AppOpenManager.getInstance().isAppResumeEnabled = true
                HomeMainFragment.binding.frNative.visibility = View.VISIBLE
            } else {
                showGotoSetting()
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    private fun showRequestManageStorageDialog(resultLauncher: ActivityResultLauncher<Intent>) {
        val dialogBinding = DialogDeleteBinding.inflate(layoutInflater)
        permissionDialog = AlertDialog.Builder(this).create()
        permissionDialog!!.setView(dialogBinding.root)
        permissionDialog!!.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialogBinding.title.gone()
        dialogBinding.msg.text = getString(R.string.request_all_files_access)
        dialogBinding.btnCancel.gone()
        dialogBinding.btnDelete.text = getString(R.string.Go_to_settings)
        dialogBinding.btnDelete.clicks().throttleFirst(2, TimeUnit.SECONDS).subscribe {
            AppOpenManager.getInstance().isAppResumeEnabled = false
            try {
                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                intent.addCategory(Intent.CATEGORY_DEFAULT)
                intent.data = Uri.parse("package:$packageName")
                resultLauncher.launch(intent)
            } catch (e: Exception) {
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                resultLauncher.launch(intent)
            }
        }
        permissionDialog!!.setCancelable(false)
        permissionDialog!!.show()
    }

    private fun showGotoSetting() {
        AppOpenManager.getInstance().disableAppResumeWithActivity(MainActivity::class.java)
        val dialogBinding = DialogDeleteBinding.inflate(layoutInflater)
        goToSettingDialog = AlertDialog.Builder(this).create()
        goToSettingDialog!!.setView(dialogBinding.root)
        goToSettingDialog!!.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialogBinding.title.gone()
        dialogBinding.msg.text = getString(R.string.please_enable_permission)
        dialogBinding.btnCancel.gone()
        dialogBinding.btnDelete.text = getString(R.string.Go_to_settings)
        dialogBinding.btnDelete.clicks().throttleFirst(2, TimeUnit.SECONDS).subscribe {
            disableOnResume(MainActivity::class.java)
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", packageName, null)
            intent.data = uri
            startActivityForResult(intent, 222)
        }
        goToSettingDialog!!.setCancelable(false)
        goToSettingDialog!!.show()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 222) {
            if (hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE) &&
                hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            ) {

                if(goToSettingDialog?.isShowing == true)
                goToSettingDialog?.dismiss()
                HomeMainFragment.binding.frNative.visibility = View.VISIBLE
                AppOpenManager.getInstance().isAppResumeEnabled = false
                AppOpenManager.getInstance().disableAppResumeWithActivity(MainActivity::class.java)

            }
        }

    }

    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R) {
            if (hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE) &&
                hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            ) {
                AppOpenManager.getInstance().isAppResumeEnabled = true
                AppOpenManager.getInstance().enableAppResumeWithActivity(MainActivity::class.java)
            }
        }
        AppOpenManager.getInstance().enableAppResumeWithActivity(MainActivity::class.java)
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