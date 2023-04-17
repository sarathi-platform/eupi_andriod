package com.patsurvey.nudge.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.ui.theme.Android_starter_projectTheme
import com.patsurvey.nudge.activities.ui.theme.Teal50
import kotlinx.coroutines.delay

private const val DELAY = 1000L

class SplashComposeActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Android_starter_projectTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Greeting()
                }
            }
        }
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

@Composable
fun Greeting() {
    val painterRes = painterResource(R.drawable.ic_shopping)
    Image(
        painter = painterRes,
        contentDescription = "Splash Image",
        modifier = Modifier.fillMaxSize(1f).background(color = Teal50).padding(12.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    Android_starter_projectTheme {
        Greeting()
    }
}