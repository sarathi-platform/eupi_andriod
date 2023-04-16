package com.tothenew.android_starter_project.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.tothenew.android_starter_project.R
import com.tothenew.android_starter_project.base.BaseActivity
import com.tothenew.android_starter_project.databinding.ActivityMainBinding
import com.tothenew.android_starter_project.utils.SORTING_CLEAR
import com.tothenew.android_starter_project.module.orders.OrderViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding, OrderViewModel>() {
    private val orderViewModel : OrderViewModel by viewModels()
    override val layoutId: Int
        get() = R.layout.activity_main

    override fun getViewModel(): OrderViewModel {
        return orderViewModel
    }

    private val singleItems: Array<String> by lazy {
        resources.getStringArray(R.array.sorting_options)
    }

    private var sortingOrder = SORTING_CLEAR

    private fun filterDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.sort_by))
            .setSingleChoiceItems(singleItems, sortingOrder)
            { dialog, which ->
                sortingOrder = which
                Toast.makeText(
                    this,
                    getString(R.string.sorting_by_message) + singleItems[which],
                    Toast.LENGTH_SHORT
                ).show()

                dialog.dismiss()
            }
            .show()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewDataBinding.navView.setupWithNavController(findNavController(R.id.nav_host_fragment_activity_main))

        viewDataBinding.topAppBar.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.filter -> {
                    filterDialog()
                    true
                }
                R.id.login -> {
                    startActivity(Intent(this, LoginActivity::class.java))
                    true
                }
                else -> {
                    true
                }
            }

        }
    }


}