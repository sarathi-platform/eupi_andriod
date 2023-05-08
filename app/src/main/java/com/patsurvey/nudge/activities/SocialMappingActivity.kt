package com.patsurvey.nudge.activities

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.ui.theme.*
import com.patsurvey.nudge.utils.DoubleButtonBox
import com.patsurvey.nudge.utils.LocationCoordinates
import com.patsurvey.nudge.utils.SimpleActionButton
import com.patsurvey.nudge.utils.Tola

val tolas = listOf(
    Tola(
        name = "Sundar Pahar",
        location = LocationCoordinates(),
        completed = true
    ),
    Tola(
        name = "Sundar Pahar",
        location = LocationCoordinates(1121, 11231),
        completed = false
    ),
    Tola(
        name = "Sundar Pahar",
        location = LocationCoordinates(),
        completed = false
    ),
    Tola(
        name = "Sundar Pahar",
        location = LocationCoordinates(1121, 11231),
        completed = true
    )

)

class SocialMappingActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("SocialMappingActivity", "opening Social Mapping...")
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
    val tolaList = remember {
        val list = mutableStateListOf<Tola>()
        list.addAll(tolas)
        list
    }
    SocialMappingScreen(
        true,
        stringResource(id = R.string.social_mapping),
        tolaList,
        onCompleteClick = { index, tola ->
            // will be called, on "mark as complete" clicked
            tolaList.removeAt(index)
            tolaList.add(index, tola)
        }) {
        // will be called, on card item clicked

    }
}

@Composable
fun SocialMappingScreen(
    isOnline: Boolean,
    title: String,
    tolas: List<Tola>,
    onCompleteClick: (Int, Tola) -> Unit,
    onItemclick: (Tola) -> Unit
) {
    val context = LocalContext.current
    Column() {
        MainTitle(
            title = stringResource(id = R.string.social_mapping),
            modifier = Modifier.padding(top = 30.dp).align(CenterHorizontally)
        )
        Spacer(modifier = Modifier.padding(12.dp))
        Text(
            text = buildAnnotatedString {
                append("${stringResource(id = R.string.showing)} ")
                withStyle(
                    style = SpanStyle(
                        color = textColorDark,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = NotoSans
                    )
                ) {
                    append("${tolas.size}")
                }
                append(" ${stringResource(id = R.string.added_tolas)}")
            },
            style = smallTextStyleNormalWeight,
            color = black1,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(start = 16.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(modifier = Modifier.padding(12.dp))
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(tolas) { index, tola ->
                PendingTolaCard(tola, tola.completed, onItemclick = {
                    onItemclick(tola)
                }) {
                    val updatedTola = Tola(tola.name, tola.location, !tola.completed)
                    onCompleteClick(index, updatedTola)
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
                if (context is Activity) {
                    context.finish()
                }
            }
        )


    }

}

@Composable
fun PendingTolaCard(
    tola: Tola,
    isCompleted: Boolean,
    onItemclick: () -> Unit,
    onCompleteClick: () -> Unit
) {
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
                        contentDescription = "completed"
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