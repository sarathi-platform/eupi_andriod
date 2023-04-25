package com.patsurvey.nudge.activities

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.ui.theme.*
import com.patsurvey.nudge.utils.LocationCoordinates
import com.patsurvey.nudge.utils.Tola

val tolas = listOf(
    Tola(
        name = "Sundar Pahar",
        location = LocationCoordinates(),
        completed = true
    ),
    Tola(
        name = "Sundar Pahar",
        location = LocationCoordinates(1121.34, 11231.55),
        completed = false
    ),
    Tola(
        name = "Sundar Pahar",
        location = LocationCoordinates(),
        completed = false
    ),
    Tola(
        name = "Sundar Pahar",
        location = LocationCoordinates(1121.34, 11231.55),
        completed = true
    )

)

class SocialMappingActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Nudge_Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    SocialMapping()
                }
            }
        }
    }
}

@Composable
fun SocialMapping() {
    SocialMappingScreen(true, stringResource(id = R.string.social_mapping), tolas)
}

@Composable
fun SocialMappingScreen(isOnline: Boolean, title: String, tolas: List<Tola>) {
    val context = LocalContext.current
    Column() {
        NetworkBanner(
            modifier = Modifier,
            isOnline = isOnline
        )
        Spacer(modifier = Modifier.padding(12.dp))
        Text(
            text = title,
            style = largeTextStyle,
            color = blueDark,
            modifier = Modifier.align(CenterHorizontally),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(modifier = Modifier.padding(12.dp))
        Text(
            text = stringResource(id = R.string.showing_added_tolas, tolas.size),
            style = smallTextStyleMediumWeight,
            color = black1,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(start = 16.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(modifier = Modifier.padding(12.dp))
        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(tolas) { tola ->
                PendingTolaCard(tola, tola.completed, onItemclick = {

                }) {
                val updatedTola =Tola(tola.name, tola.location, !tola.completed)

                }
            }

        }
        DoubleButtonBox(
            modifier = Modifier.shadow(10.dp),
            positiveButtonText = stringResource(id = R.string.submit),
            negativeButtonText = stringResource(id = R.string.go_back_text),
            positiveButtonOnClick = {

            },
            negativeButtonOnClick = {
                if(context is Activity) {
                    context.finish()
                }
            }
        )


    }

}

@Composable
fun PendingTolaCard(tola: Tola, isCompleted: Boolean, onItemclick: () -> Unit, onCompleteClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .border(
                width = 1.dp,
                color = borderGreyLight,
                shape = RoundedCornerShape(6.dp)
            )
            .background(if (isCompleted) greenLight else bgGreyLight)

    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = tola.name,
                    style = largeTextStyle,
                    color = blueDark,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (tola.completed) {
                    Image(
                        painter = painterResource(R.drawable.ic_completed_tick),
                        contentDescription = "completed",
                        modifier = Modifier.padding(end = 15.dp)
                    )
                }
            }
            if (tola.location.lat == null && tola.location.long == null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier.padding(
                        top = 17.dp,
                        bottom = if (tola.completed) 10.dp else 17.dp
                    )
                ) {
                    Image(
                        painter = painterResource(R.drawable.baseline_location_icn),
                        contentDescription = "Location icon",
                        modifier = Modifier.padding(end = 15.dp)
                    )
                    Text(
                        text = stringResource(id = R.string.not_added),
                        style = smallTextStyleMediumWeight,
                        color = black1,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            } else {
                Spacer(modifier = Modifier.padding(10.dp))
            }
            if (!tola.completed) {
                SimpleActionButton(
                    buttonTitle = stringResource(id = R.string.mark_as_completed),
                    textColor = white,
                    backgroundColor = greenOnline,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    onCompleteClick()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    Nudge_Theme {
        SocialMapping()
    }
}