package com.patsurvey.nudge.activities

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.nudge.core.ui.theme.dimen_10_dp
import com.nudge.core.ui.theme.dimen_1_dp
import com.nudge.core.ui.theme.dimen_300_dp
import com.nudge.core.ui.theme.dimen_30_dp
import com.nudge.core.ui.theme.grayColor
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.ui.theme.blueDark
import com.patsurvey.nudge.activities.ui.theme.darkYellow

@Composable
fun SplashScreenV2(
    showLoader: Boolean,
    modifier: Modifier = Modifier
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(color = grayColor)
    ) {

        val (backgroundImage, nrlmContent, appNameContent, loader, bottomContent) = createRefs()
        Image(
            painter = painterResource(id = R.drawable.splash_screen),
            contentDescription = "Background Image",
            modifier = Modifier
                .fillMaxSize()
                .constrainAs(backgroundImage) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
            contentScale = ContentScale.Crop

        )
        NrlmLogo(modifier = Modifier.constrainAs(nrlmContent) {
            top.linkTo(parent.top)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        })

        AppNameContent(modifier = Modifier
            .constrainAs(appNameContent) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            })

        if (showLoader) {
            Loader(modifier = Modifier.constrainAs(loader) {
                top.linkTo(appNameContent.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            })
        }

        BottomContent(modifier = Modifier.constrainAs(bottomContent) {
            bottom.linkTo(parent.bottom)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        })
    }
}

@Composable
fun NrlmLogo(modifier: Modifier) {
    Box {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimen_30_dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_loks_aajeevika_logo),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(97.dp)
            )
            Image(
                painter = painterResource(id = R.drawable.ic_lokos_logo_only),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(67.dp)

            )
        }
    }
}

@Composable
fun AppNameContent(modifier: Modifier) {
    Box(
        modifier = modifier
            .size(dimen_300_dp)
            .shadow(16.dp, shape = CircleShape)
            .border(dimen_1_dp, darkYellow, CircleShape)
            .background(Color.White, shape = CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.sarathi_logo_full),
                contentDescription = "Sarathi Logo"
            )
        }
    }
}

@Composable
fun Loader(modifier: Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 20.dp)
            .height(48.dp)
    ) {
        CircularProgressIndicator(
            color = blueDark,
            modifier = Modifier
                .size(28.dp)
                .align(Alignment.Center)
        )
    }
}

@Composable
fun BottomContent(modifier: Modifier) {
    Box(modifier = modifier.padding(vertical = dimen_10_dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                modifier = Modifier.size(height = 41.dp, width = 71.dp),
                painter = painterResource(id = R.drawable.ministry_logo),
                contentDescription = "Ministry Logo"
            )
            Image(
                painter = painterResource(id = R.drawable.ic_digital_india_logo),
                contentDescription = "Digital India Logo"
            )
            Image(
                painter = painterResource(id = R.drawable.nudge_logo),
                contentDescription = "nudge Logo"
            )
        }
    }
}
