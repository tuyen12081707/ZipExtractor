package unzipfiles.filecompressor.archive.rar.zip.fragment

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.os.StatFs
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.jakewharton.rxbinding4.view.clicks
import com.vapp.admoblibrary.ads.AdCallback
import com.vapp.admoblibrary.ads.AdmodUtils
import com.vapp.admoblibrary.ads.AppOpenManager
import com.vapp.admoblibrary.ads.model.InterHolder
import unzipfiles.filecompressor.archive.rar.zip.R
import unzipfiles.filecompressor.archive.rar.zip.activity.CategoriesActivity
import unzipfiles.filecompressor.archive.rar.zip.activity.InternalStorageActivity
import unzipfiles.filecompressor.archive.rar.zip.activity.LanguageChangeActivity
import unzipfiles.filecompressor.archive.rar.zip.ads.AdsManager
import unzipfiles.filecompressor.archive.rar.zip.databinding.FragmentHomeMainBinding
import unzipfiles.filecompressor.archive.rar.zip.model.SelectLanguageModel
import unzipfiles.filecompressor.archive.rar.zip.utils.*
import unzipfiles.filecompressor.archive.rar.zip.utils.Constants.EXTRA_PATH
import unzipfiles.filecompressor.archive.rar.zip.utils.Constants.EXTRA_TYPE
import java.io.File
import java.util.concurrent.TimeUnit

class HomeMainFragment : Fragment() {

    companion object {
        lateinit var binding: FragmentHomeMainBinding
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeMainBinding.inflate(layoutInflater)
        val listLang: ArrayList<SelectLanguageModel> = Common.getListLocation(requireContext())
        val position = Common.getLocationPosition(requireContext())
        Glide.with(this).load(listLang[position].getFlag()).into(binding.ivLanguage)
        binding.ivLanguage.clicks().throttleFirst(1, TimeUnit.SECONDS).subscribe { v: Unit ->
            val intent = Intent(requireActivity(), LanguageChangeActivity::class.java)
            startActivity(intent)
        }

        AdsManager.loadInter(requireContext(),AdsManager.interDirectory)
        AdsManager.loadNative(requireActivity(), AdsManager.nativeId)

        AdsManager.showNative(requireActivity(),AdsManager.nativeHome,binding.frNative)

        binding.tvUsedSpace.text = String.format("%s %s",getString(R.string.used) , usedMemory().convertSize())
        binding.tvFreeSpace.text = String.format("%s %s",getString(R.string.free) , freeMemory().convertSize())

        binding.skStorage.progress = (usedMemory() * 100 / totalMemory()).toInt()
        binding.skStorage.isEnabled = false
        binding.tvArchiver.clicks().throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                context?.logEvents("btn_archiver_click")
                openFileActivity(Type.ARCHIVER,"inter_archiver")
            }
        binding.tvDownload.clicks().throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                context?.logEvents("btn_download_click")
                openFileActivity(Type.DOWNLOAD,"inter_download")
            }
        binding.tvDocument.clicks().throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                context?.logEvents("btn_document_click")
                openFileActivity(Type.DOCUMENTS,"inter_document")
            }
        binding.tvAudio.clicks().throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                context?.logEvents("btn_audios_click")
                openFileActivity(Type.AUDIOS,"inter_audios")
            }
        binding.tvVideo.clicks().throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                context?.logEvents("btn_videos_click")
                openFileActivity(Type.VIDEOS,"inter_videos")
            }
        binding.tvImage.clicks().throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                context?.logEvents("btn_images_click")
                openFileActivity(Type.IMAGES,"inter_images")
            }
        binding.clInternal.clicks().throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                context?.logEvents("btn_internal_storage_click")
                openDirectory(Constants.EXTERNAL_DIRECTORY)
            }

        return binding.root
    }

    private fun openDirectory(directory: File) {
        AdsManager.showAdInter(requireContext(),AdsManager.interDirectory,object : AdsManager.AdListenerNew{
            override fun onAdClosed() {
                val i = Intent(requireContext(), InternalStorageActivity::class.java)
                i.putExtra(EXTRA_PATH, directory.path)
                startActivity(i)
            }

            override fun onFailed() {
                val i = Intent(requireContext(), InternalStorageActivity::class.java)
                i.putExtra(EXTRA_PATH, directory.path)
                startActivity(i)
            }
        },"inter_directory")
    }

    private fun openFileActivity(type: Type, event: String) {
         AdsManager.showAdInter(requireContext(),AdsManager.interHome,object : AdsManager.AdListenerNew{
             override fun onAdClosed() {
                 val i = Intent(requireContext(), CategoriesActivity::class.java)
                i.putExtra(EXTRA_TYPE, type.name)
                startActivity(i)
             }

             override fun onFailed() {
                 val i = Intent(requireContext(), CategoriesActivity::class.java)
                 i.putExtra(EXTRA_TYPE, type.name)
                 startActivity(i)
             }

         },event)
    }

    private fun totalMemory(): Long {
        val statFs = StatFs(Environment.getExternalStorageDirectory().absolutePath)
        return statFs.totalBytes
    }

    private fun freeMemory(): Long {
        val statFs = StatFs(Environment.getExternalStorageDirectory().absolutePath)
        return statFs.availableBytes
    }

    private fun usedMemory(): Long {
        return totalMemory() - freeMemory()
    }

}