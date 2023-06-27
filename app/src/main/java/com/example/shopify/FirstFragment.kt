package com.example.shopify

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.shopify.adapter.ViewPagerAdapter
import com.example.shopify.databinding.FragmentFirstBinding
import com.example.shopify.utils.NetworkResult
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
@AndroidEntryPoint
class FirstFragment : Fragment() {

    private lateinit var viewPager: ViewPager2

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

        val adapter = ViewPagerAdapter(listOf(
            "https://klinq.com/media/catalog/product/cache/6ba02f1c1feeeb8ea72e23b04b2a55ca/8/8/8809579838296-1_mj8bpalcovgwf41a.jpg",
            "https://klinq.com/media/catalog/product/cache/6ba02f1c1feeeb8ea72e23b04b2a55ca/8/8/8809579838296-1_mj8bpalcovgwf41a.jpg",
            "https://klinq.com/media/catalog/product/cache/6ba02f1c1feeeb8ea72e23b04b2a55ca/8/8/8809579838296-1_mj8bpalcovgwf41a.jpg",
            "https://klinq.com/media/catalog/product/cache/6ba02f1c1feeeb8ea72e23b04b2a55ca/8/8/8809579838296-1_mj8bpalcovgwf41a.jpg",
            "https://klinq.com/media/catalog/product/cache/6ba02f1c1feeeb8ea72e23b04b2a55ca/8/8/8809579838296-1_mj8bpalcovgwf41a.jpg",
            "https://klinq.com/media/catalog/product/cache/6ba02f1c1feeeb8ea72e23b04b2a55ca/8/8/8809579838296-1_mj8bpalcovgwf41a.jpg",
            "https://klinq.com/media/catalog/product/cache/6ba02f1c1feeeb8ea72e23b04b2a55ca/8/8/8809579838296-1_mj8bpalcovgwf41a.jpg",
            "https://klinq.com/media/catalog/product/cache/6ba02f1c1feeeb8ea72e23b04b2a55ca/8/8/8809579838296-1_mj8bpalcovgwf41a.jpg",
            "https://klinq.com/media/catalog/product/cache/6ba02f1c1feeeb8ea72e23b04b2a55ca/8/8/8809579838296-1_mj8bpalcovgwf41a.jpg",
            "https://klinq.com/media/catalog/product/cache/6ba02f1c1feeeb8ea72e23b04b2a55ca/8/8/8809579838296-1_mj8bpalcovgwf41a.jpg",
            "https://klinq.com/media/catalog/product/cache/6ba02f1c1feeeb8ea72e23b04b2a55ca/8/8/8809579838296-1_mj8bpalcovgwf41a.jpg",
            "https://klinq.com/media/catalog/product/cache/6ba02f1c1feeeb8ea72e23b04b2a55ca/8/8/8809579838296-1_mj8bpalcovgwf41a.jpg",
            "https://klinq.com/media/catalog/product/cache/6ba02f1c1feeeb8ea72e23b04b2a55ca/8/8/8809579838296-1_mj8bpalcovgwf41a.jpg",
            "https://klinq.com/media/catalog/product/cache/6ba02f1c1feeeb8ea72e23b04b2a55ca/8/8/8809579838296-1_mj8bpalcovgwf41a.jpg",
            "https://klinq.com/media/catalog/product/cache/6ba02f1c1feeeb8ea72e23b04b2a55ca/8/8/8809579838296-1_mj8bpalcovgwf41a.jpg",
            "https://klinq.com/media/catalog/product/cache/6ba02f1c1feeeb8ea72e23b04b2a55ca/8/8/8809579838296-1_mj8bpalcovgwf41a.jpg",
            "https://klinq.com/media/catalog/product/cache/6ba02f1c1feeeb8ea72e23b04b2a55ca/8/8/8809579838296-1_mj8bpalcovgwf41a.jpg",
        ))

        viewPager.adapter = adapter

        binding.springDotsIndicator.attachTo(viewPager)

//        productViewModel.getProductDetails()
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                productViewModel.productResponseLiveData.collectLatest {
                    when (it) {
                        is NetworkResult.Success -> {
                            val data = it.data
                            Log.d("Shopify", data.toString())
                        }
                        is NetworkResult.Error -> {

                        }
                        is NetworkResult.Loading -> {}
                    }
                }
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}