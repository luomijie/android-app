package com.kyberswap.android.presentation.main.limitorder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import com.kyberswap.android.AppExecutors
import com.kyberswap.android.R
import com.kyberswap.android.databinding.ItemFilterBinding
import com.kyberswap.android.domain.model.FilterItem
import com.kyberswap.android.presentation.base.DataBoundListAdapter

class FilterStatusAdapter(
    appExecutors: AppExecutors,
    private val onCancelClick: ((FilterItem) -> Unit)?

) : DataBoundListAdapter<FilterItem, ItemFilterBinding>(
    appExecutors,
    diffCallback = object : DiffUtil.ItemCallback<FilterItem>() {
        override fun areItemsTheSame(oldItem: FilterItem, newItem: FilterItem): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: FilterItem, newItem: FilterItem): Boolean {
            return oldItem == newItem
        }
    }
) {
    override fun bind(binding: ItemFilterBinding, item: FilterItem) {
        binding.name = item.name
        binding.isSelected = item.isSelected
        binding.executePendingBindings()
    }


    override fun createBinding(parent: ViewGroup, viewType: Int): ItemFilterBinding =
        DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_filter,
            parent,
            false
        )
}