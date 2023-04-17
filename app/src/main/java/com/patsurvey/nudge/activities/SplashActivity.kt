package com.patsurvey.nudge.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.patsurvey.nudge.databinding.SplashActivityBinding
import kotlinx.coroutines.delay

private const val DELAY = 1000L

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        super.onCreate(savedInstanceState)
        setContentView(SplashActivityBinding.inflate(layoutInflater).root)
    }

    override fun onResume() {
        super.onResume()
        this.lifecycleScope.launchWhenResumed {
            delay(DELAY)
            launchMain()
        }
    }

    private fun launchMain() {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }

        startActivity(intent)
        finish()
    }
}