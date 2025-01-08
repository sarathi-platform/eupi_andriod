package com.nudge.core.ui.commonUi

import android.net.Uri
import android.text.TextUtils
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import coil.compose.rememberImagePainter
import com.nudge.core.model.FilterUiModel
import com.nudge.core.ui.theme.defaultCardElevation
import com.nudge.core.ui.theme.dimen_1_dp
import com.nudge.core.ui.theme.dimen_45_dp
import com.nudge.core.ui.theme.dimen_4_dp
import com.nudge.core.ui.theme.dimen_60_dp
import com.nudge.core.ui.theme.dimen_72_dp
import com.nudge.core.ui.theme.filterItemSelectedDark
import com.nudge.core.ui.theme.filterItemSelectedLight
import com.nudge.core.ui.theme.smallerTextStyleNormalWeight
import com.nudge.core.ui.theme.textColorDark
import com.nudge.core.ui.theme.uncheckedTrackColor
import com.nudge.core.ui.theme.white
import com.nudge.core.utils.FileUtils

@Composable
fun FilterRowItem(
    modifier: Modifier = Modifier,
    isSelected: Boolean,
    item: FilterUiModel,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val colors = getColorsForFilterItem(isSelected)

    Column(
        modifier = Modifier
            .widthIn(dimen_60_dp, dimen_72_dp)
            .padding(horizontal = dimen_4_dp)
            .clickable {
                onClick()
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .size(dimen_60_dp),
            shape = CircleShape,
            elevation = defaultCardElevation,
            border = BorderStroke(dimen_1_dp, colors.second)
        ) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(color = colors.first, CircleShape)
                    .border(dimen_1_dp, colors.second, CircleShape)
                    .size(dimen_60_dp)
            ) {
                val imageUri =
                    if (TextUtils.isEmpty(item.imageFileName)) Uri.EMPTY else item.imageFileName?.let {
                        FileUtils.getImageUri(
                            context = context,
                            fileName = it
                        )
                    }
                Image(
                    painter = rememberImagePainter(
                        imageUri
                    ),
                    contentDescription = "filter icon",
                    contentScale = ContentScale.Inside,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .aspectRatio(1f, matchHeightConstraintsFirst = true)
                        .width(dimen_45_dp)
                        .height(dimen_45_dp)
                )
            }
        }


        Text(
            text = item.filterTitle,
            style = smallerTextStyleNormalWeight.copy(color = colors.third),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

    }

}

@Composable
fun getColorsForFilterItem(isSelected: Boolean): Triple<Color, Color, Color> {
    return if (isSelected) {
        Triple(filterItemSelectedLight, filterItemSelectedDark, filterItemSelectedDark)
    } else {
        Triple(white, uncheckedTrackColor, textColorDark)
    }
}