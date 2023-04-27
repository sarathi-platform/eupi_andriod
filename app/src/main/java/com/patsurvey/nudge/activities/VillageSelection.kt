package com.patsurvey.nudge.activities

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.graphics.Color.Companion.Cyan
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.ui.theme.*

@Preview
@Composable
fun VillageSelectionScreen(
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Text(text = "Select Village & VO", style = largeTextStyle, color = textColorDark)
        VillageAndVoBox(
            tolaName = "Sundar Pahar",
            voName = "Sundar Pahar Mahila Mandal",
            modifier = modifier,
        ) {

        }
        VillageAndVoBox(
            tolaName = "Sundar Pahar",
            voName = "Sundar Pahar Mahila Mandal",
            modifier = modifier
        ) {}
        VillageAndVoBox(
            tolaName = "Sundar Pahar",
            voName = "Sundar Pahar Mahila Mandal",
            modifier = modifier
        ) {}
        VillageAndVoBox(
            tolaName = "Sundar Pahar",
            voName = "Sundar Pahar Mahila Mandal",
            modifier = modifier
        ) {}
    }

}

@Composable
fun VillageAndVoBox(
    tolaName: String = "",
    voName: String = "",
    modifier: Modifier = Modifier,
    onVillageSeleted: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = greyBorder,
                shape = RoundedCornerShape(6.dp)
            )
            .shadow(
                elevation = 10.dp,
                ambientColor = White,
                spotColor = Black,
                shape = RoundedCornerShape(6.dp),
            )
            .clip(RoundedCornerShape(6.dp))
            .background(White)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(
                    bounded = true,
                    color = Black
                )
            ) {
                onVillageSeleted()
            }
            .then(modifier),
        elevation = 10.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row() {
                Icon(
                    painter = painterResource(id = R.drawable.home_icn),
                    contentDescription = null,
                    tint = textColorDark
                )
                Text(
                    text = tolaName,
                    modifier = Modifier
                        .fillMaxWidth(),
                    color = textColorDark,
                    style = smallTextStyle
                )
            }
            Row(
                modifier = Modifier
                    .absolutePadding(left = 4.dp)
            ) {
                Text(
                    text = "VO:",
                    modifier = Modifier,
                    color = textColorDark,
                    style = smallTextStyle
                )
                Text(
                    text = voName,
                    modifier = Modifier
                        .fillMaxWidth(),
                    color = textColorDark,
                    style = smallTextStyle
                )
            }
        }
    }
}