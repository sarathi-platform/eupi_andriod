package com.nudge.core.ui.commonUi

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuBoxScope
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import com.nudge.core.BLANK_STRING
import com.nudge.core.R
import com.nudge.core.ui.theme.blueDark
import com.nudge.core.ui.theme.borderGrey
import com.nudge.core.ui.theme.dimen_16_dp
import com.nudge.core.ui.theme.dimen_250_dp
import com.nudge.core.ui.theme.dimen_2_dp
import com.nudge.core.ui.theme.dimen_60_dp
import com.nudge.core.ui.theme.newMediumTextStyle
import com.nudge.core.ui.theme.placeholderGrey
import com.nudge.core.ui.theme.white
import androidx.compose.material3.TextFieldDefaults as TextFieldDefaults1

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun <T, U> SearchBarWithDropdownComponent(
    title: TextProperties<U>,
    state: SearchBarWithDropDownState<T>,
    paddingValues: PaddingValues = PaddingValues(horizontal = dimen_16_dp),
    onGlobalPositioned: (LayoutCoordinates) -> Unit,
    onItemSelected: (index: Int) -> Unit,
    onSearchQueryChanged: (searchQuery: TextFieldValue) -> Unit,
    DropDownItem: @Composable ExposedDropdownMenuBoxScope.(item: T) -> Unit
) {
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }

    val icon = if (state.getDropDownStateValue())
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown
    var textFieldWidth by remember { mutableStateOf(0.dp) }
    val currentDensity = LocalDensity.current

    Column(
        modifier = Modifier.padding(paddingValues),
        horizontalAlignment = Alignment.Start
    ) {
        CustomTextViewComponent(textProperties = title)
        Spacer(modifier = Modifier.height(4.dp))
        CustomOutlineTextField(
            value = state.getSelectedValue().value,
            onValueChange = {
            },
            interactionSource = remember { MutableInteractionSource() }
                .also { interactionSource ->
                    LaunchedEffect(interactionSource) {
                        interactionSource.interactions.collect {
                            if (it is PressInteraction.Release) {
                                state.updateDropDownState()
                            }
                        }
                    }
                },
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .height(dimen_60_dp)
                .clickable { state.updateDropDownState() }
                .onGloballyPositioned { coordinates ->
                    onGlobalPositioned(coordinates)
                    textFieldWidth = with(currentDensity) { coordinates.size.width.toDp() }

                },
            textStyle = newMediumTextStyle.copy(blueDark),
            singleLine = true,
            maxLines = 1,
            placeholder = {
                Text(text = "hint", style = newMediumTextStyle, color = placeholderGrey)
            },
            colors = TextFieldDefaults.textFieldColors(
                textColor = blueDark,
                backgroundColor = Color.White,
                focusedIndicatorColor = borderGrey,
                unfocusedIndicatorColor = borderGrey,
            ),
            trailingIcon = {
                Icon(icon, "contentDescription",
                    Modifier.clickable {
                        state.updateDropDownState()
                    }
                )
            }
        )
        ExposedDropdownMenuBox(
            expanded = state.getDropDownStateValue(),
            onExpandedChange = {},
            modifier = Modifier.fillMaxWidth()
        ) {
            AnimatedVisibility(
                visible = state.getDropDownStateValue(),
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                BasicCardView {
                    Column {
                        TextField(
                            value = state.getSearchQueryStateValue(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(white)
                                .exposedDropdownSize(),
                            onValueChange = {
                                state.searchQueryValueUpdated(it)
                                onSearchQueryChanged(state.getSearchQueryStateValue())
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "search drop down",
                                    modifier = Modifier.absolutePadding(top = dimen_2_dp)
                                )
                            },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "clear drop down search",
                                    modifier = Modifier
                                        .absolutePadding(top = dimen_2_dp)
                                        .clickable {
                                            state.clearSearchQueryValue()
                                            onSearchQueryChanged(TextFieldValue(BLANK_STRING))
                                        }
                                )
                            },
                            placeholder = {
                                Text(
                                    text = stringResource(R.string.search_label),
                                    color = Color.Gray
                                )
                            },
                            textStyle = TextStyle(fontSize = 16.sp),
                            interactionSource = remember { MutableInteractionSource() }
                                .also { interactionSource ->
                                    LaunchedEffect(interactionSource) {
                                        interactionSource.interactions.collect {
                                            if (it is PressInteraction.Release) {
                                            }
                                        }
                                    }
                                },
                            colors = TextFieldDefaults1.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                            )
                        )
                        Divider()
                        val scrollState = rememberScrollState()
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(dimen_250_dp)
                                .verticalScroll(scrollState)
                        ) {
                            state.getFilteredDropDownMenuItemList().value.forEachIndexed { index, item ->
                                DropdownMenuItem(
                                    modifier = Modifier.exposedDropdownSize(),
                                    onClick = {
                                        onItemSelected(index)
                                        state.clearSearchQueryValue()
                                    }
                                ) {
                                    DropDownItem(item)
                                }
                            }
                        }


                    }
                }
            }
        }
        CustomVerticalSpacer()
    }
}

class SearchBarWithDropDownState<T>(initialValue: Boolean = false, dropdownMenuItemList: List<T>) {
    private val dropDownState = mutableStateOf(initialValue)

    private val selectedValue = mutableStateOf("Select")

    private val searchQuery = mutableStateOf(TextFieldValue(BLANK_STRING))

    private val _dropdownMenuItemListState: MutableState<List<T>> = mutableStateOf(emptyList())
    private val dropdownMenuItemListState: State<List<T>> get() = _dropdownMenuItemListState

    private val _filteredList: MutableState<List<T>> = mutableStateOf(emptyList())

    private val filteredList: State<List<T>> = _filteredList

    init {
        _dropdownMenuItemListState.value = dropdownMenuItemList
        _filteredList.value = dropdownMenuItemListState.value
    }

    fun getDropDownState() = dropDownState

    fun getDropDownStateValue() = dropDownState.value

    fun getSelectedValue() = selectedValue

    fun getDropDownMenuItemListState() = dropdownMenuItemListState

    fun getDropDownMenuItemListStateValue() = dropdownMenuItemListState.value

    fun getFilteredDropDownMenuItemList() = filteredList

    fun getFilteredDropDownMenuItemListValue() = filteredList.value

    fun getSearchQueryState() = searchQuery

    fun getSearchQueryStateValue() = searchQuery.value

    fun show() {
        dropDownState.value = true
    }

    fun hide() {
        dropDownState.value = false
    }

    fun updateDropDownState() {
        if (dropDownState.value) {
            hide()
        } else {
            show()
        }
    }

    fun searchQueryValueUpdated(mSearchQuery: TextFieldValue) {
        searchQuery.value = mSearchQuery
    }

    fun clearSearchQueryValue() {
        searchQuery.value = TextFieldValue(BLANK_STRING)
    }

    fun filterDropDownMenuItemList(listToFilter: List<T>, predicate: (T) -> Boolean) {
        _filteredList.value = if (getSearchQueryStateValue().text.isNotEmpty())
            listToFilter.filter(predicate)
        else
            dropdownMenuItemListState.value
    }

    fun setSelectedItemValue(mSelectedValue: String) {
        selectedValue.value = mSelectedValue
    }
}


fun <T> rememberSearchBarWithDropDownState(
    initialValue: Boolean = false,
    dropdownMenuItemList: List<T>
): SearchBarWithDropDownState<T> {
    return SearchBarWithDropDownState(initialValue, dropdownMenuItemList)
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
@Preview(showBackground = true, showSystemUi = true)
fun PerviewSerch() {
    var textFieldSize by remember { mutableStateOf(Size.Zero) }

    val state = remember {
        rememberSearchBarWithDropDownState(
            dropdownMenuItemList = listOf(
                "Birth",
                "Death",
                "Gift",
                "Purchase",
                "Sale",
                "Feed Procurement",
                "Health",
                "Birth",
                "Death",
                "Gift",
                "Purchase",
                "Sale",
                "Feed Procurement",
                "Health",
                "Birth",
                "Death",
                "Gift",
                "Purchase",
                "Sale",
                "Feed Procurement",
                "Health1",
                "Birth1",
                "Death",
                "Gift",
                "Purchase1",
                "Sale1",
                "Feed Procurement1",
                "Health"
            )
        )
    }
    SearchBarWithDropdownComponent(
        title = TextProperties.getBasicTextProperties(text = buildAnnotatedString {
            append("Event")
            append("*")
        }),
        state = state,
        onGlobalPositioned = { coordinates ->
            textFieldSize = coordinates.size.toSize()
        },
        onItemSelected = {
            state.hide()
            state.setSelectedItemValue(state.getFilteredDropDownMenuItemListValue()[it])
        },
        onSearchQueryChanged = { searchQuery ->
            state.filterDropDownMenuItemList(state.getDropDownMenuItemListStateValue()) {
                it.contains(searchQuery.text, true)
            }
        }
    ) {
        CustomTextViewComponent(
            textProperties = TextProperties.getBasicTextProperties(text = it).copy(
                modifier = Modifier
                    .fillMaxWidth()
                    .exposedDropdownSize()
            )
        )
    }
}