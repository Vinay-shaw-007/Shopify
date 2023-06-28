package com.example.shopify

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.shopify.adapter.SwatchAdapter
import com.example.shopify.adapter.ViewPagerAdapter
import com.example.shopify.databinding.FragmentFirstBinding
import com.example.shopify.model.ConfigurableOption
import com.example.shopify.model.Data
import com.example.shopify.utils.NetworkResult
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
@AndroidEntryPoint
class FirstFragment : Fragment() {

    private lateinit var viewPager: ViewPager2
    private lateinit var recyclerView: RecyclerView

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val productViewModel by viewModels<ProductViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewPager = binding.imageSlider

        recyclerView = binding.swatchRecyclerView

        productViewModel.getProductDetails()

        binding.textView11.setOnClickListener {
            hideAndShowProductDetailSection()
        }

        binding.productDescription.setOnClickListener {
            hideAndShowProductDetailSection()
        }

        observer()

    }

    private fun hideAndShowProductDetailSection() {
        if (binding.productDescription.isVisible) {
            val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.arrow_down)

            val fadeOutAnimator = ObjectAnimator.ofFloat(binding.productDescription, "alpha", 1f, 0f)
            fadeOutAnimator.duration = 300
            fadeOutAnimator.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    binding.productDescription.visibility = View.GONE
                    binding.view.visibility = View.GONE

                }
            })

            fadeOutAnimator.start()

            binding.textView11.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)
        } else {
            val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.arrow_up)

            binding.productDescription.alpha = 0f
            binding.productDescription.visibility = View.VISIBLE
            binding.view.visibility = View.VISIBLE
            val fadeInAnimator = ObjectAnimator.ofFloat(binding.productDescription, "alpha", 0f, 1f)
            fadeInAnimator.duration = 300
            fadeInAnimator.start()

            binding.textView11.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)
        }
    }

    private fun observer() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                productViewModel.productResponseLiveData.collectLatest {
                    binding.progressBar.visibility = View.GONE
                    when (it) {
                        is NetworkResult.Success -> {
                            val apiResponse = it.data
                            apiResponse?.let {
                                setViewPagerImages(apiResponse.data.images)
                                setSwatchesImages(apiResponse.data.configurable_option)
                                setOtherUIDetails(apiResponse.data)
                            }
                            binding.nestedScrollView.visibility = View.VISIBLE
                        }

                        is NetworkResult.Error -> {

                        }

                        is NetworkResult.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.nestedScrollView.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }

    private fun setOtherUIDetails(data: Data) {
        binding.apply {
            brandName.text = data.brand_name
            name.text = data.name
            skuName.text = getString(R.string.sku, data.sku)
            price.text = getString(R.string.price, data.price.substring(0, 4))
            val rawDescription = data.description
            val stringBuilder = StringBuilder()
            val extractedValues = extractTextFromTags(rawDescription)
            for (value in extractedValues) {
                stringBuilder.append(value)
                stringBuilder.append("\n")
            }
            productDescription.text = stringBuilder
        }
    }

    private fun extractTextFromTags(htmlString: String): List<String> {
        val document: Document = Jsoup.parse(htmlString)

        val extractedValues = mutableListOf<String>()

        fun extractTextFromElement(element: Element) {
            when (element.tag().normalName()) {
                "p", "ul" -> extractedValues.add(element.ownText())
                "li" -> extractedValues.add("• ${element.ownText()}")
            }

            for (child in element.children()) {
                extractTextFromElement(child)
            }
        }

        extractTextFromElement(document.body())

        return extractedValues
    }

    private fun setSwatchesImages(configurableOption: List<ConfigurableOption>) {
        val swatchImages = ArrayList<String>()
        configurableOption.forEach { item ->
            item.attributes.forEach {
                swatchImages.add(it.swatch_url)
            }

        }
        val swatchAdapter = SwatchAdapter(swatchImages)
        recyclerView.adapter = swatchAdapter
    }

    private fun setViewPagerImages(images: List<String>) {
        val adapter = ViewPagerAdapter(images)
        viewPager.adapter = adapter
        binding.springDotsIndicator.attachTo(viewPager)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}