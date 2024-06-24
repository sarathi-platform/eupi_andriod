package com.sarathi.missionactivitytask.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import com.nudge.core.ui.theme.brownDark
import com.nudge.core.ui.theme.defaultCardElevation
import com.nudge.core.ui.theme.dimen_10_dp
import com.nudge.core.ui.theme.dimen_2_dp
import com.nudge.core.ui.theme.dimen_4_dp
import com.nudge.core.ui.theme.dimen_56_dp
import com.nudge.core.ui.theme.roundedCornerRadiusDefault
import com.nudge.core.ui.theme.white
import com.nudge.core.ui.theme.yellowBg
import com.sarathi.dataloadingmangement.BLANK_STRING

@Composable
fun BasicCardView(
    modifier: Modifier = Modifier,
    cardShape: Shape = RoundedCornerShape(roundedCornerRadiusDefault),
    colors: CardColors = CardDefaults.cardColors(
        containerColor = white
    ),
    cardElevation: CardElevation = CardDefaults.cardElevation(
        defaultElevation = defaultCardElevation
    ),
    cardBorder: BorderStroke? = null,
    content: @Composable ColumnScope.() -> Unit
) {

    Card(
        elevation = cardElevation,
        shape = cardShape,
        border = cardBorder,
        colors = colors,
        modifier = Modifier
            .then(modifier),
    ) {
        content()
    }

}

@Composable
fun CardWithImage(
    modifier: Modifier = Modifier,
    cardShape: Shape = RoundedCornerShape(roundedCornerRadiusDefault),
    colors: CardColors = CardDefaults.cardColors(),
    cardElevation: CardElevation = CardDefaults.cardElevation(
        defaultElevation = defaultCardElevation
    ),
    cardBorder: BorderStroke? = null,
    imageProperties: ImageProperties,
    content: @Composable () -> Unit
) {

    BasicCardView(
        modifier = modifier,
        cardShape = cardShape,
        colors = colors,
        cardElevation = cardElevation,
        cardBorder = cardBorder
    ) {

        ContentWithImage(imageProperties = imageProperties) {
            content()
        }

    }
}

@Composable
fun ContentWithImage(
    modifier: Modifier = Modifier,
    imageProperties: ImageProperties,
    mainContent: @Composable RowScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(white)
            .padding(dimen_10_dp)
            .then(modifier)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimen_4_dp),
            horizontalArrangement = Arrangement.spacedBy(dimen_10_dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularImageViewComponent(
                modifier = Modifier
                    .border(width = dimen_2_dp, shape = CircleShape, color = brownDark)
                    .clip(CircleShape)
                    .width(dimen_56_dp)
                    .height(dimen_56_dp)
                    .background(color = yellowBg),
                imageProperties = imageProperties
            )
            mainContent()
        }
    }
}

@Composable
fun ImageCardWithBottomContent(
    modifier: Modifier = Modifier,
    cardShape: Shape = RoundedCornerShape(roundedCornerRadiusDefault),
    colors: CardColors = CardDefaults.cardColors(),
    cardElevation: CardElevation = CardDefaults.cardElevation(
        defaultElevation = defaultCardElevation
    ),
    cardBorder: BorderStroke? = null,
    imageProperties: ImageProperties,
    bottomContent: @Composable () -> Unit,
    mainContent: @Composable () -> Unit
) {

    BasicCardView(
        modifier = modifier,
        cardShape = cardShape,
        colors = colors,
        cardElevation = cardElevation,
        cardBorder = cardBorder
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(white)
                .padding(dimen_10_dp)
                .then(modifier)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimen_4_dp),
                horizontalArrangement = Arrangement.spacedBy(dimen_10_dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularImageViewComponent(
                    modifier = Modifier
                        .border(width = dimen_2_dp, shape = CircleShape, color = brownDark)
                        .clip(CircleShape)
                        .width(dimen_56_dp)
                        .height(dimen_56_dp)
                        .background(color = yellowBg),
                    imageProperties = imageProperties
                )
                mainContent()
            }

            bottomContent()

        }
    }

}

data class ImageProperties(
    val path: String = BLANK_STRING,
    val contentDescription: String?,
    val modifier: Modifier = Modifier,
    val alignment: Alignment = Alignment.Center,
    val contentScale: ContentScale = ContentScale.FillBounds,
    val alpha: Float = DefaultAlpha,
    val colorFilter: ColorFilter? = null
)