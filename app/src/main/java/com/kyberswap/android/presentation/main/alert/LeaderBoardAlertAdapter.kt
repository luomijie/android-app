package com.kyberswap.android.presentation.main.alert

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import com.kyberswap.android.AppExecutors
import com.kyberswap.android.BR
import com.kyberswap.android.R
import com.kyberswap.android.databinding.ItemLeaderBoardBinding
import com.kyberswap.android.domain.model.Alert
import com.kyberswap.android.presentation.base.DataBoundListAdapter

class LeaderBoardAlertAdapter(
    appExecutors: AppExecutors

) : DataBoundListAdapter<Alert, ItemLeaderBoardBinding>(
    appExecutors,
    diffCallback = object : DiffUtil.ItemCallback<Alert>() {
        override fun areItemsTheSame(oldItem: Alert, newItem: Alert): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Alert, newItem: Alert): Boolean {
            return oldItem.areContentsTheSame(newItem)
        }
    }
) {


    fun submitAlerts(alerts: List<Alert>) {
        submitList(listOf())
        submitList(alerts)
    }

    override fun bind(binding: ItemLeaderBoardBinding, item: Alert) {
        binding.isActive = item.userName.isNotBlank()
        binding.setVariable(BR.alert, item)
        binding.setVariable(BR.isReward, item.rewardId > 0 || item.reward.isNotEmpty())
        binding.executePendingBindings()
    }


    override fun createBinding(parent: ViewGroup, viewType: Int): ItemLeaderBoardBinding =
        DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_leader_board,
            parent,
            false
        )
}