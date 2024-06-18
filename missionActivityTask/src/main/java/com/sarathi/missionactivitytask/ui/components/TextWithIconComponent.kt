package com.sarathi.missionactivitytask.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import com.nudge.core.ui.theme.dimen_8_dp

@Composable
fun BasicTextWithIconComponent(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    spacerWidth: Dp = dimen_8_dp,
    iconContent: @Composable () -> Unit,
    textContent: @Composable () -> Unit
) {

    Row(
        modifier = Modifier.then(modifier),
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = verticalAlignment
    ) {

        iconContent()
        Spacer(modifier = Modifier.width(spacerWidth))
        textContent()

    }

}

@Composable
fun <T, U> TextWithIconComponent(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    spacerWidth: Dp = dimen_8_dp,
    iconProperties: IconProperties<T>,
    textProperties: TextProperties<U>
) {

    BasicTextWithIconComponent(
        modifier = modifier,
        verticalAlignment = verticalAlignment,
        horizontalArrangement = horizontalArrangement,
        spacerWidth = spacerWidth,
        iconContent = {
            when (iconProperties.icon) {
                is Painter -> {
                    Icon(
                        painter = iconProperties.icon,
                        contentDescription = iconProperties.contentDescription,
                        tint = iconProperties.tint,
                        modifier = iconProperties.modifier
                    )
                }

                is ImageVector -> {
                    Icon(
                        imageVector = iconProperties.icon,
                        contentDescription = iconProperties.contentDescription,
                        tint = iconProperties.tint,
                        modifier = iconProperties.modifier
                    )
                }

                is ImageBitmap -> {
                    Icon(
                        bitmap = iconProperties.icon,
                        contentDescription = iconProperties.contentDescription,
                        tint = iconProperties.tint,
                        modifier = iconProperties.modifier
                    )
                }
            }

        },
        textContent = {
            when (textProperties.text) {
                is String -> {
                    Text(
                        text = textProperties.text,
                        modifier = textProperties.modifier,
                        color = textProperties.color,
                        fontSize = textProperties.fontSize,
                        fontStyle = textProperties.fontStyle,
                        fontWeight = textProperties.fontWeight,
                        fontFamily = textProperties.fontFamily,
                        letterSpacing = textProperties.letterSpacing,
                        textDecoration = textProperties.textDecoration,
                        textAlign = textProperties.textAlign,
                        lineHeight = textProperties.lineHeight,
                        overflow = textProperties.overflow,
                        softWrap = textProperties.softWrap,
                        maxLines = textProperties.maxLines,
                        onTextLayout = textProperties.onTextLayout,
                        style = textProperties.style
                    )
                }

                is AnnotatedString -> {
                    Text(
                        text = textProperties.text,
                        modifier = textProperties.modifier,
                        color = textProperties.color,
                        fontSize = textProperties.fontSize,
                        fontStyle = textProperties.fontStyle,
                        fontWeight = textProperties.fontWeight,
                        fontFamily = textProperties.fontFamily,
                        letterSpacing = textProperties.letterSpacing,
                        textDecoration = textProperties.textDecoration,
                        textAlign = textProperties.textAlign,
                        lineHeight = textProperties.lineHeight,
                        overflow = textProperties.overflow,
                        softWrap = textProperties.softWrap,
                        maxLines = textProperties.maxLines,
                        onTextLayout = textProperties.onTextLayout,
                        style = textProperties.style
                    )
                }
            }

        }

    )

}


data class IconProperties<T>(
    val icon: T,
    val contentDescription: String?,
    val tint: Color = Color.Black,
    val modifier: Modifier = Modifier
)

data class TextProperties<T>(
    val text: T,
    val modifier: Modifier = Modifier,
    val color: Color = Color.Unspecified,
    val fontSize: TextUnit = TextUnit.Unspecified,
    val fontStyle: FontStyle? = null,
    val fontWeight: FontWeight? = null,
    val fontFamily: FontFamily? = null,
    val letterSpacing: TextUnit = TextUnit.Unspecified,
    val textDecoration: TextDecoration? = null,
    val textAlign: TextAlign? = null,
    val lineHeight: TextUnit = TextUnit.Unspecified,
    val overflow: TextOverflow = TextOverflow.Clip,
    val softWrap: Boolean = true,
    val maxLines: Int = Int.MAX_VALUE,
    val minLines: Int = 1,
    val onTextLayout: (TextLayoutResult) -> Unit = {},
    val style: TextStyle
)