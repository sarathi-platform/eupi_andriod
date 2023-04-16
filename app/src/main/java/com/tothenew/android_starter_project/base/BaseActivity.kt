package com.tothenew.android_starter_project.base

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel

abstract class BaseActivity<T : ViewDataBinding, V : ViewModel> : AppCompatActivity() {

    protected lateinit var viewDataBinding: T

    protected lateinit var mViewModel: V

    /**
     * Return layout resource id
     */
    @get:LayoutRes
    abstract val layoutId: Int

    /**
     * Override for set view model
     *
     * @return view model instance
     */
    abstract fun getViewModel(): V


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mViewModel = getViewModel()
        viewDataBinding = DataBindingUtil.setContentView(this, layoutId)
        viewDataBinding.apply {
            setVariable(1, mViewModel)
            lifecycleOwner = this@BaseActivity
            executePendingBindings()
        }

    }
}