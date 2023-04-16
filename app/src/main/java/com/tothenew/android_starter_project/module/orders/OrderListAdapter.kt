package com.tothenew.android_starter_project.module.orders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.tothenew.android_starter_project.databinding.LayoutOrderItemListBinding
import com.tothenew.android_starter_project.model_ui.OrderedItem
import com.tothenew.android_starter_project.model_ui.OrderedItemDiffCallBack
import com.tothenew.android_starter_project.network.BaseNetworkConstants.RANDOM_IMAGE_URL
import com.tothenew.android_starter_project.utils.gone
import com.tothenew.android_starter_project.utils.loadImage
import com.tothenew.android_starter_project.utils.visible

class OrderListAdapter(private val onClick: (OrderedItem, Int) -> Unit = { _, _ -> run {} }) :
    RecyclerView.Adapter<OrderListAdapter.OrderListViewHolder>() {

    private val differ = AsyncListDiffer(this, OrderedItemDiffCallBack)

    fun submitList(list: List<OrderedItem>) {
        differ.submitList(list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = OrderListViewHolder(
        LayoutOrderItemListBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: OrderListViewHolder, position: Int) {
        holder.bind(differ.currentList[position], onClick)
    }

    inner class OrderListViewHolder(private val binding: LayoutOrderItemListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(orderedItem: OrderedItem?, onClick: (OrderedItem, Int) -> Unit) {
            with(binding) {
                orderedItem?.let {
                    imageViewOrderItemList.loadImage("$RANDOM_IMAGE_URL${it.imageId}")
                    textViewTitleOrderItemList.text = it.name
                    textViewPriceOrderItemList.text = it.price
                    if (it.extra?.isNotBlank() == true) {
                        textViewExtraOrderItemList.text = it.extra
                        textViewExtraOrderItemList.visible()
                    } else {
                        textViewExtraOrderItemList.gone()
                    }
                }
            }
        }
    }
}