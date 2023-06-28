package com.example.shopify.ui.productDetail

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.shopify.R
import com.example.shopify.ui.adapter.SwatchAdapter
import com.example.shopify.ui.adapter.ViewPagerAdapter
import com.example.shopify.databinding.FragmentProductDetailBinding
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
class ProductDetailFragment : Fragment() {

    private lateinit var viewPager: ViewPager2
    private lateinit var recyclerView: RecyclerView

    private var _binding: FragmentProductDetailBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val productViewModel by activityViewModels<ProductViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentProductDetailBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views
        viewPager = binding.imageSlider
        recyclerView = binding.swatchRecyclerView

        // Request product details from the ViewModel
        productViewModel.getProductDetails()

        // Set click listeners for hiding/showing the product detail section
        binding.productDetailLabel.setOnClickListener {
            hideAndShowProductDetailSection()
        }
        binding.productDescription.setOnClickListener {
            hideAndShowProductDetailSection()
        }

        // Set click listeners for increasing and reducing the product quantity
        binding.btnPlus.setOnClickListener {
            var quantity = Integer.parseInt(binding.productQuantity.text.toString())
            quantity += 1
            binding.productQuantity.text = quantity.toString()
        }
        binding.btnMinus.setOnClickListener {
            var quantity = Integer.parseInt(binding.productQuantity.text.toString())
            if (quantity > 1) {
                quantity -= 1
                binding.productQuantity.text = quantity.toString()
            }
        }

        // Observe changes in the product response from the ViewModel
        observer()

    }

    // Function to hide/show the product detail section
    private fun hideAndShowProductDetailSection() {
        if (binding.productDescription.isVisible) {
            // If the description is visible, fade it out and hide it
            val fadeOutAnimator =
                ObjectAnimator.ofFloat(binding.productDescription, "alpha", 1f, 0f)
            fadeOutAnimator.duration = 300
            fadeOutAnimator.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    binding.productDescription.visibility = View.GONE
                    binding.view.visibility = View.GONE
                }
            })
            fadeOutAnimator.start()

            // Change the label's drawable to indicate the section is hidden
            val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.arrow_down)
            binding.productDetailLabel.setCompoundDrawablesWithIntrinsicBounds(
                null,
                null,
                drawable,
                null
            )
        } else {
            // If the description is hidden, make it visible and fade it in
            binding.productDescription.alpha = 0f
            binding.productDescription.visibility = View.VISIBLE
            binding.view.visibility = View.VISIBLE
            val fadeInAnimator = ObjectAnimator.ofFloat(binding.productDescription, "alpha", 0f, 1f)
            fadeInAnimator.duration = 300
            fadeInAnimator.start()

            // Change the label's drawable to indicate the section is visible
            val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.arrow_up)
            binding.productDetailLabel.setCompoundDrawablesWithIntrinsicBounds(
                null,
                null,
                drawable,
                null
            )
        }
    }

    // Function to observe changes in the product response
    private fun observer() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Collect the product response state flow from the ViewModel
                productViewModel.productResponseStateFlow.collectLatest {
                    binding.progressBar.visibility = View.GONE
                    when (it) {
                        is NetworkResult.Success -> {
                            // Handling response data
                            val apiResponse = it.data
                            apiResponse?.let {
                                setViewPagerImages(apiResponse.data.images)
                                setSwatchesImages(apiResponse.data.configurable_option)
                                setOtherUIDetails(apiResponse.data)
                            }
                            // Showing the content when available view
                            binding.nestedScrollView.visibility = View.VISIBLE
                        }

                        is NetworkResult.Error -> {
                            // Handling Error state
                            Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_LONG)
                                .show()
                        }

                        is NetworkResult.Loading -> {
                            // Handling loading state
                            binding.progressBar.visibility = View.VISIBLE
                            binding.nestedScrollView.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }

    // Function to set other UI details based on the product data
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

    // Function to extract text from HTML tags in the product description
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

    // Function to set the swatches images in the recycler view
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

    // Function to set the view pager images
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