package unzipfiles.filecompressor.archive.rar.zip.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import com.jakewharton.rxbinding4.view.clicks
import unzipfiles.filecompressor.archive.rar.zip.R
import unzipfiles.filecompressor.archive.rar.zip.activity.LanguageChangeActivity
import unzipfiles.filecompressor.archive.rar.zip.ads.AdsManager
import unzipfiles.filecompressor.archive.rar.zip.databinding.FragmentHistoryBinding
import unzipfiles.filecompressor.archive.rar.zip.model.SelectLanguageModel
import unzipfiles.filecompressor.archive.rar.zip.utils.Common
import unzipfiles.filecompressor.archive.rar.zip.utils.Type
import unzipfiles.filecompressor.archive.rar.zip.utils.logEvents
import java.util.concurrent.TimeUnit

class HistoryFragment : Fragment() {
    private lateinit var binding: FragmentHistoryBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoryBinding.inflate(layoutInflater)
        val listLang: ArrayList<SelectLanguageModel> = Common.getListLocation(requireContext())
        val position = Common.getLocationPosition(requireContext())
        Glide.with(this).load(listLang[position].getFlag()).into(binding.ivLanguage)

        AdsManager.loadNative(requireActivity(), AdsManager.nativeLanguage)

        binding.ivLanguage.clicks().throttleFirst(1, TimeUnit.SECONDS).subscribe { v: Unit ->
            val intent = Intent(requireActivity(), LanguageChangeActivity::class.java)
            startActivity(intent)
        }
        setupViewpager()
        context?.logEvents("history_screen")
        return binding.root
    }

    private fun setupViewpager() {
        binding.viewpager.adapter = ViewPager2Adapter(requireActivity())
        TabLayoutMediator(binding.tabLayout, binding.viewpager) { tab, position ->
            val text = if (position == 0) getString(R.string.compressed) else getString(R.string.extracted)
            tab.text = text
            context?.logEvents("${text.lowercase()}_history_tab")
        }.attach()
    }

    open class ViewPager2Adapter(activity: FragmentActivity) :
        FragmentStateAdapter(activity) {

        override fun createFragment(position: Int): Fragment {
            val type = if (position == 0) Type.COMPRESSED else Type.EXTRACTED
            return CompressExtractFragment.newInstance(type.name)
        }

        override fun getItemCount(): Int {
            return 2
        }
    }
}