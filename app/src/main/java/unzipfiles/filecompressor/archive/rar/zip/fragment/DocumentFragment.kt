package unzipfiles.filecompressor.archive.rar.zip.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import unzipfiles.filecompressor.archive.rar.zip.databinding.FragmentDocumentBinding
import unzipfiles.filecompressor.archive.rar.zip.utils.Type

class DocumentFragment : Fragment() {
    private lateinit var binding: FragmentDocumentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDocumentBinding.inflate(layoutInflater)
        setupViewpager()
        return binding.root
    }

    private fun setupViewpager() {
        binding.viewpager.adapter = DocumentPagerAdapter(requireActivity())
        TabLayoutMediator(binding.tabLayout, binding.viewpager) { tab, position ->
            val name = when (position) {
                0 -> "All"
                1 -> Type.PDF.name
                2 -> Type.EXCEL.name
                3 -> Type.PPT.name
                4 -> Type.WORD.name
                else -> Type.TXT.name
            }
            tab.text = name.lowercase().capitalize()
        }.attach()
//        binding.viewpager.isUserInputEnabled = false
//        binding.viewpager.offscreenPageLimit = 6
    }

    open class DocumentPagerAdapter(activity: FragmentActivity) :
        FragmentStateAdapter(activity) {

        override fun createFragment(position: Int): Fragment {
            val type = when (position) {
                0 -> Type.DOCUMENTS.name
                1 -> Type.PDF.name
                2 -> Type.EXCEL.name
                3 -> Type.PPT.name
                4 -> Type.WORD.name
                else -> Type.TXT.name
            }
            return CategoryFragment.newInstance(type)
        }

        override fun getItemCount(): Int {
            return 6
        }
    }
}