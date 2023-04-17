package com.patsurvey.nudge.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel

abstract class BaseFragment <T : ViewDataBinding, V : ViewModel> :
    androidx.fragment.app.Fragment() {

    protected lateinit var viewDataBinding: T
    protected lateinit var mViewModel: V

    protected lateinit var mContext: Context

    /**
     * Returns layout resource id
     */
    @LayoutRes
    abstract fun getLayoutId(): Int

    /**
     * Override for set view model
     *
     * @return view model instance
     */
    abstract fun getViewModel(): V

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.let {
            mContext = it
        }
        mViewModel = getViewModel()

        viewDataBinding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false)
        viewDataBinding.setVariable(1, mViewModel)
        viewDataBinding.lifecycleOwner = this
        viewDataBinding.executePendingBindings()

        return viewDataBinding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

}
