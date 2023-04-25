package com.patsurvey.nudge.utils

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable

fun showToast(
    context: Context?,
    msg: String,){
    Toast.makeText(context,msg,Toast.LENGTH_LONG).show()
}