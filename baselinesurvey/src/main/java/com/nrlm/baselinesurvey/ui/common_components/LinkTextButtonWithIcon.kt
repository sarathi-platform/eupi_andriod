package com.nrlm.baselinesurvey.ui.common_components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.ui.theme.blueDark
import com.nrlm.baselinesurvey.ui.theme.smallTextStyleMediumWeight
import com.nrlm.baselinesurvey.ui.theme.textColorDark

@Composable
fun LinkTextButtonWithIcon(
    modifier: Modifier = Modifier,
    title: String = stringResource(id = R.string.show),
    textColor: Color = textColorDark,
    iconTint: Color = blueDark,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    Row(modifier = Modifier
        .clickable {
            onClick()
        }
        .indication(
            interactionSource = interactionSource,
            indication = rememberRipple(
                bounded = true,
                color = Color.White
            )
        )
        .then(modifier)
    ) {
        Text(
            text = title,
            style = smallTextStyleMediumWeight,
            color = textColor,
            maxLines = 2,
            textDecoration = TextDecoration.Underline,
            overflow = TextOverflow.Ellipsis
        )
        Icon(
            imageVector = Icons.Default.ArrowForward,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier
                .absolutePadding(top = 4.dp, left = 2.dp)
                .size(24.dp)
        )
    }
}