package com.nudge.core.ui.commonUi

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import com.nudge.core.BLANK_STRING
import com.nudge.core.formatProgressNumber
import com.nudge.core.ui.theme.dimen_8_dp
import com.nudge.core.ui.theme.progressIndicatorColor
import com.nudge.core.ui.theme.smallTextStyle
import com.nudge.core.ui.theme.textColorDark
import com.nudge.core.ui.theme.trackColor
import java.util.concurrent.atomic.AtomicInteger

@Composable
fun CustomLinearProgressIndicator(
    modifier: Modifier = Modifier,
    showProgressText: Boolean = true,
    progressState: CustomProgressState = rememberCustomProgressState(),
    color: Color = progressIndicatorColor,
    progressTrackColor: Color = trackColor,
    strokeCap: StrokeCap = ProgressIndicatorDefaults.LinearStrokeCap

) {

    val progress = remember(progressState.key.value.get()) {
        progressState.getProgressAsState()
    }

    val progressText = remember(progressState.key.value.get()) {
        progressState.getProgressTextAsState()
    }

    Row(
        verticalAlignment = Alignment.CenterVertically, modifier = Modifier
            .fillMaxWidth()
            .then(modifier)
    ) {
        LinearProgressIndicator(
            modifier = Modifier
                .weight(1f)
                .height(dimen_8_dp)
                .padding(top = 1.dp)
                .clip(RoundedCornerShape(14.dp)),
            color = color,
            trackColor = progressTrackColor,
            progress = formatProgressNumber(progress.value),
            strokeCap = strokeCap
        )
        if (showProgressText) {
            Spacer(modifier = Modifier.width(dimen_8_dp))
            Text(
                text = progressText.value,
                color = textColorDark,
                style = smallTextStyle
            )
        }
    }

}

class CustomProgressState(
    initialValue: Float = DEFAULT_PROGRESS_VALUE,
    progressText: String = BLANK_STRING
) {

    private val mProgress = mutableStateOf(initialValue)
    private val mProgressText = mutableStateOf(progressText)

    val key = mutableStateOf(AtomicInteger(0))

    fun getProgressAsState() = mProgress

    fun getProgressTextAsState() = mProgressText

    fun updateProgress(updatedProgress: Float) {
        mProgress.value = updatedProgress
        key.value.set(key.value.incrementAndGet())
    }

    fun updateProgressText(progressText: String) {
        mProgressText.value = progressText
        key.value.set(key.value.incrementAndGet())
    }

    fun updateCompleteProgressState(stateValues: Pair<State<Float>, State<String>>) {
        updateProgress(stateValues.first.value)
        updateProgressText(stateValues.second.value)
    }
}

@Composable
fun rememberCustomProgressState(
    initialValue: Float = DEFAULT_PROGRESS_VALUE,
    progressText: String = BLANK_STRING
): CustomProgressState {
    return CustomProgressState(initialValue, progressText)
}

@Composable
fun <T> List<T>.produceLinearProgressState(
    initialValue: Float = DEFAULT_PROGRESS_VALUE,
    predicate: (T) -> Boolean
): State<Float> {
    val list = this
    return produceState(initialValue = initialValue) {
        value = list.filter(predicate).size.toFloat()
    }
}

@Composable
fun <T> List<T>.produceLinearProgressTextState(
    initialValue: String = BLANK_STRING,
    predicate1: (T) -> Boolean,
    predicate2: (T) -> Boolean
): State<String> {
    val list = this
    return produceState(initialValue = initialValue) {
        value =
            "${list.filter(predicate1).size.toFloat()}/${list.filter(predicate2).size.toFloat()}"
    }
}

@Composable
fun <T> List<T>.produceLinearProgressTextState(
    initialValue: String = BLANK_STRING,
    predicate1: (T) -> Boolean,
): State<String> {
    val list = this
    return produceState(initialValue = initialValue) {
        value =
            "${list.filter(predicate1).size.toFloat()}/${list.size.toFloat()}"
    }
}

@Composable
fun <T> getUpdatedValuesForProgressState(
    list: List<T>,
    initialValue1: Float,
    initialValue2: String,
    predicate1: (T) -> Boolean,
    predicate2: (T) -> Boolean
): Pair<State<Float>, State<String>> {
    return Pair(
        list.produceLinearProgressState(initialValue = initialValue1, predicate = predicate1),
        list.produceLinearProgressTextState(
            initialValue = initialValue2,
            predicate1 = predicate1,
            predicate2 = predicate2
        )
    )
}

@Composable
fun <T> getUpdatedValuesForProgressState(
    list: List<T>,
    initialValue1: Float,
    initialValue2: String,
    predicate1: (T) -> Boolean
): Pair<State<Float>, State<String>> {
    return Pair(
        list.produceLinearProgressState(initialValue = initialValue1, predicate = predicate1),
        list.produceLinearProgressTextState(
            initialValue = initialValue2,
            predicate1 = predicate1
        )
    )
}


const val DEFAULT_PROGRESS_VALUE = 0F
const val DEFAULT_PROGRESS_TEXT_VALUE = "0"