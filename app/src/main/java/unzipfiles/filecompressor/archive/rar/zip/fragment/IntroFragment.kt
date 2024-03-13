package unzipfiles.filecompressor.archive.rar.zip.fragment

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import unzipfiles.filecompressor.archive.rar.zip.R
import unzipfiles.filecompressor.archive.rar.zip.databinding.FragmentIntroBinding
import unzipfiles.filecompressor.archive.rar.zip.utils.logEvents


class IntroFragment : Fragment() {
    private lateinit var binding: FragmentIntroBinding
    private var mPosition = 0

    companion object {
        fun newInstance(position: Int) = IntroFragment().apply {
            arguments = Bundle(1).apply { putInt("position", position) }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentIntroBinding.inflate(layoutInflater)

        arguments?.getInt("position")?.let { mPosition = it }

        binding.tvContent.movementMethod = ScrollingMovementMethod()
        when (mPosition) {
            0 -> {
                binding.ivIntro.setImageResource(R.drawable.im_intro1)
                binding.tvTitle.text = getString(R.string.support_extracting)
                binding.tvContent.text = getString(R.string.content_1)
            }
            1 -> {
                binding.ivIntro.setImageResource(R.drawable.im_intro2)
                binding.tvTitle.text = getString(R.string.title_intro_2)
                binding.tvContent.text = getString(R.string.content_2)
            }
            2 -> {
                binding.ivIntro.setImageResource(R.drawable.im_intro3)
                binding.tvTitle.text = getString(R.string.title_intro_3)
                binding.tvContent.text = getString(R.string.content_3)
            }
        }
        return binding.root
    }
}
