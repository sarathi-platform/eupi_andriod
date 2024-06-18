package com.sarathi.smallgroupmodule

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.sarathi.smallgroupmodule.ui.theme.The_nudgeTheme

class SmallGroupActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            The_nudgeTheme {

            }
        }

    }
}