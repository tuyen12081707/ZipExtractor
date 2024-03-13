package unzipfiles.filecompressor.archive.rar.zip.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.codemybrainsout.ratingdialog.RatingDialog
import com.jakewharton.rxbinding4.view.clicks
import com.vapp.admoblibrary.ads.AppOpenManager
import unzipfiles.filecompressor.archive.rar.zip.R
import unzipfiles.filecompressor.archive.rar.zip.activity.InternalStorageActivity
import unzipfiles.filecompressor.archive.rar.zip.activity.LanguageChangeActivity
import unzipfiles.filecompressor.archive.rar.zip.activity.MainActivity
import unzipfiles.filecompressor.archive.rar.zip.activity.PolicyAppActivity
import unzipfiles.filecompressor.archive.rar.zip.ads.Utils
import unzipfiles.filecompressor.archive.rar.zip.databinding.FragmentSettingBinding
import unzipfiles.filecompressor.archive.rar.zip.model.SelectLanguageModel
import unzipfiles.filecompressor.archive.rar.zip.utils.*
import java.util.concurrent.TimeUnit

class SettingFragment : Fragment() {
    private lateinit var binding: FragmentSettingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingBinding.inflate(layoutInflater)
        val listLang: ArrayList<SelectLanguageModel> = Common.getListLocation(requireContext())
        val position = Common.getLocationPosition(requireContext())
        Glide.with(this).load(listLang[position].getFlag()).into(binding.ivLanguage)
        binding.ivLanguage.clicks().throttleFirst(1, TimeUnit.SECONDS).subscribe { v: Unit ->
            val intent = Intent(requireActivity(), LanguageChangeActivity::class.java)
            startActivity(intent)
        }
        binding.tvShare.clicks().throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                disableOnResume(requireActivity().javaClass)
                context?.shareApp()
            }
        binding.tvFeedback.clicks().throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                disableOnResume(requireActivity().javaClass)
                context?.sendFeedback()
            }
        binding.tvPrivacy.clicks().throttleFirst(1, TimeUnit.SECONDS)
            .subscribe { context?.addActivity(PolicyAppActivity::class.java) }
        binding.tvRate.clicks().throttleFirst(1, TimeUnit.SECONDS).subscribe {
            disableOnResume(requireActivity().javaClass)
            Utils.showRate(requireContext(),false)
        }
        return binding.root
    }

    private fun showRate() {
        AppOpenManager.getInstance().disableAppResumeWithActivity(MainActivity::class.java)
        RatingDialog.Builder(requireActivity())
            .session(1)
            .date(1)
            .setNameApp(getString(R.string.app_name))
            .setIcon(R.mipmap.ic_launcher)
            .setEmail("3xiappsp@gmail.com")
            .isShowButtonLater(true)
            .isClickLaterDismiss(true)
            .setTextButtonLater("Maybe Later")
            .setOnlickMaybeLate { }
            .setOnlickRate {
                AppOpenManager.getInstance().disableAppResumeWithActivity(MainActivity::class.java)
            }
            .ratingButtonColor(R.color.colorPrimary)
            .build()
            .show()
    }
}