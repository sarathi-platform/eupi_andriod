package com.patsurvey.nudge.utils

import android.content.Context
import android.view.Gravity
import android.widget.Toast
import androidx.compose.runtime.Composable

fun showToast(
    context: Context?,
    msg: String){
    Toast.makeText(context,msg,Toast.LENGTH_LONG).show()
}
fun showCustomToast(
    context: Context?,
    msg: String){
     val toast= Toast.makeText(context,msg,Toast.LENGTH_LONG)
    context?.let {
        toast.setGravity(Gravity.TOP, dpToPx(it, 10), dpToPx(it, 20))
    }
    toast.show()
    }
