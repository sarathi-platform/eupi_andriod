package com.nudge.core.ui.commonUi

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString

@Composable
fun <T> CustomTextViewComponent(
    textProperties: TextProperties<T>,
    isMandatory: Boolean = false
) {
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