package com.nudge.core.ui.commonUi

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetDefaults
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.SwipeableDefaults
import androidx.compose.material.contentColorFor
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePickerColors
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerFormatter
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerDefaults
import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nudge.core.ui.theme.blueDark
import com.nudge.core.ui.theme.dimen_10_dp
import com.nudge.core.ui.theme.dimen_14_dp
import com.nudge.core.ui.theme.searchFieldBg
import com.nudge.core.ui.theme.smallTextStyleWithNormalWeight
import com.nudge.core.ui.theme.white
import com.sarathi.missionactivitytask.R

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CustomDateRangePickerBottomSheetComponent(
    customDateRangePickerBottomSheetProperties: CustomDateRangePickerBottomSheetProperties = rememberDateRangePickerBottomSheetProperties(),
    dateRangePickerProperties: DateRangePickerProperties = rememberDateRangePickerProperties(),
    sheetHeight: SheetHeight = SheetHeight.Default,
    onSheetConfirmButtonClicked: () -> Unit,
    content: @Composable () -> Unit
) {

    val localConfiguration = LocalConfiguration.current

    val screenHeightDp = localConfiguration.screenHeightDp.dp

    val height = remember {
        when (sheetHeight) {
            is SheetHeight.Default -> {
                screenHeightDp
            }

            is SheetHeight.CustomSheetHeight -> {
                screenHeightDp - sheetHeight.height
            }
        }
    }

    /*Scaffold {
        val a = it.calculateBottomPadding()
        content()
    }*/

    //TDOO Fix the crash for bottom sheet.
    ModalBottomSheetLayout(
        modifier = customDateRangePickerBottomSheetProperties.modifier,
        sheetShape = customDateRangePickerBottomSheetProperties.sheetShape,
        sheetState = customDateRangePickerBottomSheetProperties.sheetState,
        sheetGesturesEnabled = true,
        sheetElevation = customDateRangePickerBottomSheetProperties.sheetElevation,
        sheetBackgroundColor = customDateRangePickerBottomSheetProperties.sheetBackgroundColor,
        sheetContentColor = customDateRangePickerBottomSheetProperties.sheetContentColor,
        scrimColor = customDateRangePickerBottomSheetProperties.scrimColor,
        sheetContent = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height)
                    .padding(top = dimen_14_dp)
                    .background(searchFieldBg)
            ) {
                DateRangePicker(
                    state = dateRangePickerProperties.state,
                    modifier = dateRangePickerProperties.modifier,
                    dateFormatter = dateRangePickerProperties.dateFormatter,
                    dateValidator = dateRangePickerProperties.dateValidator,
                    title = dateRangePickerProperties.title,
                    headline = dateRangePickerProperties.headline,
                    showModeToggle = dateRangePickerProperties.showModeToggle,
                    colors = dateRangePickerProperties.colors,
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .wrapContentWidth()
                        .padding(end = dimen_10_dp, bottom = dimen_10_dp)
                ) {
                    Button(
                        modifier = Modifier,
                        onClick = {
                            onSheetConfirmButtonClicked()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = blueDark
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.ok),
                            color = white,
                            style = smallTextStyleWithNormalWeight
                        )
                    }
                }

            }
        }
    ) {
        content()
    }

}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun rememberCustomDateRangePickerSheetState(
    initialValue: ModalBottomSheetValue = ModalBottomSheetValue.Hidden,
    animationSpec: AnimationSpec<Float> = SwipeableDefaults.AnimationSpec,
    confirmValueChange: (ModalBottomSheetValue) -> Boolean = { true },
    skipHalfExpanded: Boolean = true,
): ModalBottomSheetState {

    return rememberModalBottomSheetState(
        initialValue = initialValue,
        animationSpec = animationSpec,
        confirmValueChange = confirmValueChange,
        skipHalfExpanded = skipHalfExpanded
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberDateRangePickerProperties(
    state: DateRangePickerState = rememberDateRangePickerState(),
    modifier: Modifier = Modifier,
    dateFormatter: DatePickerFormatter = remember { DatePickerFormatter() },
    dateValidator: (Long) -> Boolean = { true },
    title: (@Composable () -> Unit)? = {
        DateRangePickerDefaults.DateRangePickerTitle(
            state = state,
            modifier = Modifier.padding(DateRangePickerTitlePadding)
        )
    },
    headline: (@Composable () -> Unit)? = {
        DateRangePickerDefaults.DateRangePickerHeadline(
            state,
            dateFormatter,
            modifier = Modifier.padding(DateRangePickerHeadlinePadding)
        )
    },
    showModeToggle: Boolean = true,
    colors: DatePickerColors = DatePickerDefaults.colors(
        containerColor = searchFieldBg,
        todayDateBorderColor = blueDark,
        dayInSelectionRangeContainerColor = blueDark.copy(0.5f),
        selectedDayContainerColor = blueDark,
        selectedDayContentColor = white
    )
): DateRangePickerProperties {
    return DateRangePickerProperties(
        state = state,
        modifier = modifier,
        dateFormatter = dateFormatter,
        dateValidator = dateValidator,
        title = title,
        headline = headline,
        showModeToggle = showModeToggle,
        colors = colors
    )
}

@OptIn(ExperimentalMaterial3Api::class)
data class DateRangePickerProperties(
    val modifier: Modifier,
    val state: DateRangePickerState,
    val dateFormatter: DatePickerFormatter,
    val dateValidator: (Long) -> Boolean,
    val title: (@Composable () -> Unit)?,
    val headline: (@Composable () -> Unit)?,
    val showModeToggle: Boolean,
    val colors: DatePickerColors
)


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun rememberDateRangePickerBottomSheetProperties(
    modifier: Modifier = Modifier,
    sheetState: ModalBottomSheetState = rememberCustomDateRangePickerSheetState(),
    sheetShape: Shape = RoundedCornerShape(topStart = dimen_10_dp, topEnd = dimen_10_dp),
    sheetElevation: Dp = ModalBottomSheetDefaults.Elevation,
    sheetBackgroundColor: Color = searchFieldBg,
    sheetContentColor: Color = contentColorFor(sheetBackgroundColor),
    scrimColor: Color = ModalBottomSheetDefaults.scrimColor,
): CustomDateRangePickerBottomSheetProperties {
    return CustomDateRangePickerBottomSheetProperties(
        modifier = modifier,
        sheetState = sheetState,
        sheetShape = sheetShape,
        sheetElevation = sheetElevation,
        sheetBackgroundColor = sheetBackgroundColor,
        sheetContentColor = sheetContentColor,
        scrimColor = scrimColor
    )
}

@OptIn(ExperimentalMaterialApi::class)
data class CustomDateRangePickerBottomSheetProperties(
    val modifier: Modifier = Modifier,
    val sheetState: ModalBottomSheetState,
    val sheetShape: Shape,
    val sheetElevation: Dp,
    val sheetBackgroundColor: Color,
    val sheetContentColor: Color,
    val scrimColor: Color,
)

private val DateRangePickerTitlePadding = PaddingValues(start = 64.dp, end = 12.dp)
private val DateRangePickerHeadlinePadding =
    PaddingValues(start = 64.dp, end = 12.dp, bottom = 12.dp)

sealed class SheetHeight() {

    object Default : SheetHeight()

    /**
     *  @param height The value which should be subtracted from the screen height.
     **/
    data class CustomSheetHeight(val height: Dp) : SheetHeight()

}