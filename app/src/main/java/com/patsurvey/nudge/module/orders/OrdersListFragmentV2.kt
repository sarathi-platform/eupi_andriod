package com.patsurvey.nudge.module.orders

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import com.patsurvey.nudge.R
import com.patsurvey.nudge.base.BaseFragment
import com.patsurvey.nudge.databinding.FragmentOrdersListBinding
import com.patsurvey.nudge.databinding.LoadingShimmerListLayoutBinding
import com.patsurvey.nudge.model.response.OrdersListResponse
import com.patsurvey.nudge.utils.gone
import com.patsurvey.nudge.utils.visible
import dagger.hilt.android.AndroidEntryPoint


/**
 * created by anil
 */

@AndroidEntryPoint
class OrdersListFragmentV2 : BaseFragment<FragmentOrdersListBinding, OrderViewModel>() {
    private val orderViewModel : OrderViewModel by viewModels()

    override fun getLayoutId(): Int {
        return R.layout.fragment_orders_list
    }

    override fun getViewModel(): OrderViewModel {
        return  orderViewModel

    }

    private val orderListAdapter = OrderListAdapter()
    private val loading by lazy {
        LoadingShimmerListLayoutBinding.inflate(
            LayoutInflater.from(viewDataBinding.root.context),
            viewDataBinding.root as ViewGroup
        )
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewDataBinding.recyclerViewOrdersList.adapter = orderListAdapter

        fetchOrders()


    }

    /**
     * api call to fetch orders
     */
    private fun fetchOrders() {
        startLoading()
        mViewModel.fetchOrders().observe(viewLifecycleOwner) {
            stopLoading()
            val data = it.data
            if (data is OrdersListResponse) {
                orderListAdapter.submitList(mViewModel.getOrdersList(data.data?.items))


            } else {
                //TODO handle cases like something went wrong and network error
            }
        }
    }

    private fun startLoading() {
        viewDataBinding.recyclerViewOrdersList.gone()

        (loading.shimmerSources).let {
            it.startShimmer()
            it.visible()
        }
    }

    private fun stopLoading() {
        viewDataBinding.recyclerViewOrdersList.visible()
        (loading.shimmerSources).let {
            it.gone()
            it.stopShimmer()
        }
    }
}