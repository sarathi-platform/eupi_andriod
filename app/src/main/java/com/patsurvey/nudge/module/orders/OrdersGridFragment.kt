package com.patsurvey.nudge.module.orders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.patsurvey.nudge.R
import com.patsurvey.nudge.base.BaseFragment
import com.patsurvey.nudge.databinding.FragmentOrdersGridBinding
import com.patsurvey.nudge.databinding.LoadingShimmerGridLayoutBinding
import com.patsurvey.nudge.model.response.OrdersListResponse
import com.patsurvey.nudge.utils.GridSpacingItemDecoration
import com.patsurvey.nudge.utils.gone
import com.patsurvey.nudge.utils.visible
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class OrdersGridFragment : BaseFragment<FragmentOrdersGridBinding, OrderViewModel>() {
  private val SPAN_COUNT = 3
  private val INCLUDE_EDGE = true
  private val orderViewModel: OrderViewModel by viewModels()
  override fun getLayoutId(): Int {
    return R.layout.fragment_orders_grid
  }

  override fun getViewModel(): OrderViewModel {
    return orderViewModel

  }

  private val loading by lazy {
    LoadingShimmerGridLayoutBinding.inflate(
      LayoutInflater.from(viewDataBinding.root.context),
      viewDataBinding.root as ViewGroup
    )
  }

  private val orderListAdapter = OrderGridAdapter()

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)


    val marginItem = resources.getDimension(R.dimen.margin_parent_grid_item).toInt()

    viewDataBinding.recyclerViewOrdersGrid.addItemDecoration(
      GridSpacingItemDecoration(
        SPAN_COUNT,
        marginItem,
        INCLUDE_EDGE
      )
    )

    viewDataBinding.recyclerViewOrdersGrid.adapter = orderListAdapter


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
    viewDataBinding.recyclerViewOrdersGrid.gone()
    (loading.shimmerSources).let {
      it.startShimmer()
      it.visible()
    }
  }

  private fun stopLoading() {
    viewDataBinding.recyclerViewOrdersGrid.visible()
    (loading.shimmerSources).let {
      it.gone()
      it.stopShimmer()
    }
  }
}