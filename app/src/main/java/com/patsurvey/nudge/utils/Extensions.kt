package com.patsurvey.nudge.utils

import android.view.View
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.findViewTreeLifecycleOwner
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.patsurvey.nudge.R
import kotlinx.coroutines.*

fun ImageView.loadImage(url: String?) {
    Glide.with(this)
        .load(url ?: "")
        .diskCacheStrategy(DiskCacheStrategy.NONE)
        .fitCenter()
        .placeholder(R.drawable.ic_android_device)
        .error(R.drawable.ic_android_device)
        .into(this)
}

fun ImageView.load(@DrawableRes drawableRes: Int) {
    Glide.with(this).load(drawableRes).fitCenter().into(this)
}

fun String?.default(default: String? = null): String {
    if (this.isNullOrEmpty()) {
        return default ?: ""
    }
    return this
}

inline fun <T : Any> T?.act(f: (it: T) -> Unit) {
    if (this != null) f(this)
}

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun View.delayOnLifeCycle(
    durationInMillis: Long,
    dispatcher: CoroutineDispatcher = Dispatchers.Main,
    block: () -> Unit
): Job? = findViewTreeLifecycleOwner()?.let { lifecycleOwner ->
    lifecycleOwner.lifecycle.coroutineScope.launch(dispatcher) {
        delay(durationInMillis)
        block.invoke()
    }
}