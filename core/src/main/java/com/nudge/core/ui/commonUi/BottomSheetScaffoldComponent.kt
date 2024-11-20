package com.nudge.core.ui.commonUi


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetDefaults
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.contentColorFor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.nudge.core.BLANK_STRING
import com.nudge.core.NO_FILTER_VALUE
import com.nudge.core.model.uiModel.LivelihoodModel
import com.nudge.core.ui.theme.blueDark
import com.nudge.core.ui.theme.dimen_10_dp
import com.nudge.core.ui.theme.dimen_1_dp
import com.nudge.core.ui.theme.dimen_20_dp
import com.nudge.core.ui.theme.greenOnline
import com.nudge.core.ui.theme.greyBorder
import com.nudge.core.ui.theme.mediumTextStyle
import com.nudge.core.ui.theme.newBoldTextStyle
import com.nudge.core.ui.theme.searchFieldBg
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun <T> BottomSheetScaffoldComponent(
    bottomSheetScaffoldProperties: CustomBottomSheetScaffoldProperties = rememberCustomBottomSheetScaffoldProperties(),
    defaultValue: String = BLANK_STRING,
    headerTitle: String? = BLANK_STRING,
    bottomSheetContentItemList: List<T>,
    selectedIndex: Int = 0,
    onBottomSheetItemSelected: (selectedItemIndex: Int) -> Unit,
    content: @Composable () -> Unit
) {

    val coroutineScope = rememberCoroutineScope()

    val selectedItemIndex = remember(bottomSheetContentItemList) {
        mutableStateOf(selectedIndex)
    }

    ModalBottomSheetLayout(
        sheetState = bottomSheetScaffoldProperties.sheetState,
        sheetShape = bottomSheetScaffoldProperties.sheetShape,
        sheetContent = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(bottomSheetScaffoldProperties.sheetShape)
                    .background(MaterialTheme.colors.surface)
            ) {
                if (bottomSheetContentItemList.isNotEmpty()) {

                    SelectionSheetItemView(
                        headerTitle = headerTitle,
                        items = bottomSheetContentItemList,
                        SelectionSheetItem = { index, item ->
                            when (item) {
                                is LivelihoodModel -> {
                                    CustomTextViewComponent(
                                        textProperties = TextProperties
                                            .getBasicTextProperties(text = item.name)
                                            .copy(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable {
                                                        coroutineScope.launch {
                                                            onBottomSheetItemSelected(index)
                                                            bottomSheetScaffoldProperties.sheetState.hide()
                                                        }
                                                    }
                                            )
                                    )
                                }

                                is String? -> {
                                    (item as String?)?.let {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    coroutineScope.launch {
                                                        selectedItemIndex.value = index
                                                        onBottomSheetItemSelected(index)
                                                        bottomSheetScaffoldProperties.sheetState.hide()
                                                    }
                                                },
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            val itemValue =
                                                if (it.contains(NO_FILTER_VALUE, true)) {
                                                    it.replace(NO_FILTER_VALUE, defaultValue)
                                                } else {
                                                    it
                                                }
                                            CustomTextViewComponent(
                                                textProperties = TextProperties
                                                    .getBasicTextProperties(text = itemValue)
                                                    .copy(
                                                        style = mediumTextStyle,
                                                        modifier = Modifier
                                                            .weight(0.9f),
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                            )
                                            if (index == selectedItemIndex.value) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = "Selected item",
                                                    tint = greenOnline,
                                                    modifier = Modifier
                                                        .weight(0.1f)
                                                )
                                            }
                                        }
                                    }
                                }

                                else -> {
                                    CustomTextViewComponent(
                                        textProperties = TextProperties
                                            .getBasicTextProperties(text = item)
                                            .copy(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable {
                                                        coroutineScope.launch {
                                                            onBottomSheetItemSelected(index)
                                                            bottomSheetScaffoldProperties.sheetState.hide()
                                                        }
                                                    }
                                            )
                                    )
                                }
                            }

                        }
                    )
                }
            }
        }
    ) {
        content()
    }
}

@Composable
fun <T> SelectionSheetItemView(
    headerTitle: String? = BLANK_STRING,
    items: List<T>,
    SelectionSheetItem: @Composable (index: Int, item: T) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimen_20_dp)
    ) {
        if (headerTitle?.isNotEmpty() == true)
            item {
                Text(headerTitle, style = newBoldTextStyle, color = blueDark)
            }
        itemsIndexed(items) { index, item ->
            Column {
                CustomVerticalSpacer()
                SelectionSheetItem(index, item)
                CustomVerticalSpacer()
                Divider(thickness = dimen_1_dp, color = greyBorder)
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
data class CustomBottomSheetScaffoldProperties(
    val modifier: Modifier = Modifier,
    val sheetState: ModalBottomSheetState,
    val sheetShape: Shape,
    val sheetElevation: Dp,
    val sheetBackgroundColor: Color,
    val sheetContentColor: Color,
    val scrimColor: Color,
)

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun rememberCustomBottomSheetScaffoldProperties(
    modifier: Modifier = Modifier,
    sheetState: ModalBottomSheetState = rememberModalBottomSheetState(
        ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    ),
    sheetShape: Shape = RoundedCornerShape(topStart = dimen_10_dp, topEnd = dimen_10_dp),
    sheetElevation: Dp = ModalBottomSheetDefaults.Elevation,
    sheetBackgroundColor: Color = searchFieldBg,
    sheetContentColor: Color = contentColorFor(sheetBackgroundColor),
    scrimColor: Color = ModalBottomSheetDefaults.scrimColor,
): CustomBottomSheetScaffoldProperties {
    return remember {
        CustomBottomSheetScaffoldProperties(
            modifier = modifier,
            sheetState = sheetState,
            sheetShape = sheetShape,
            sheetElevation = sheetElevation,
            sheetBackgroundColor = sheetBackgroundColor,
            sheetContentColor = sheetContentColor,
            scrimColor = scrimColor
        )
    }
}

// Step 4: Preview the composable (Optional)

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    val selectedItem = remember { mutableStateOf("None") }
    val items = remember { mutableStateOf(listOf("Item 1", "Item 2", "Item 3")) }

    BottomSheetScaffoldComponent(
        bottomSheetContentItemList = items.value,
        onBottomSheetItemSelected = {

        }
    ) {

    }
}