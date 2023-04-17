package com.patsurvey.nudge.module.orders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.patsurvey.nudge.databinding.LayoutOrderItemGridBinding
import com.patsurvey.nudge.model_ui.OrderedItem
import com.patsurvey.nudge.model_ui.OrderedItemDiffCallBack
import com.patsurvey.nudge.network.BaseNetworkConstants.RANDOM_IMAGE_URL
import com.patsurvey.nudge.utils.loadImage

class OrderGridAdapter(private val onClick: (OrderedItem, Int) -> Unit = { _, _ -> run {} }) :
    RecyclerView.Adapter<OrderGridAdapter.OrderGridViewHolder>() {

    private val differ = AsyncListDiffer(this, OrderedItemDiffCallBack)

    fun submitList(list: List<OrderedItem>) {
        differ.submitList(list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = OrderGridViewHolder(
        LayoutOrderItemGridBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: OrderGridViewHolder, position: Int) {
        holder.bind(differ.currentList[position], onClick)
    }


    inner class OrderGridViewHolder(private val binding: LayoutOrderItemGridBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(orderedItem: OrderedItem?, onClick: (OrderedItem, Int) -> Unit) {
            with(binding) {
                orderedItem?.let {
                    imageViewOrderItemGrid.loadImage("$RANDOM_IMAGE_URL${it.imageId}")
                    textViewTitleOrderItemGrid.text = it.name
                    textViewPriceOrderItemGrid.text = it.price
                }
            }
        }
    }
}