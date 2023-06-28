package com.example.shopify.ui.adapter

// MyPagerAdapter.kt
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shopify.R
import com.example.shopify.databinding.SingleImageItemBinding

class ViewPagerAdapter(private val items: List<String>) : RecyclerView.Adapter<ViewPagerAdapter.MyViewHolder>() {

//    private val items: List<String> = listOf("Page 1", "Page 2", "Page 3")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = SingleImageItemBinding.inflate(inflater, parent, false)
        return MyViewHolder(binding)
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class MyViewHolder(private val binding: SingleImageItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: String) {
            Glide
                .with(binding.root)
                .load(item)
                .placeholder(R.drawable.placeholder)
                .into(binding.singleImage)
        }
    }
}
