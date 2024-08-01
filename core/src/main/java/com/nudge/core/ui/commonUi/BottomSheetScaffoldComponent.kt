package com.nudge.core.ui.commonUi


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nudge.core.ui.theme.defaultTextStyle
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BottomSheetScaffoldComponent() {

    val selectedItem = remember { mutableStateOf("None") }
    val items = remember { mutableStateOf(listOf("Item 1", "Item 2", "Item 3")) }
    val coroutineScope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)

    ModalBottomSheetLayout(
        sheetState = bottomSheetState,
        sheetShape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
        sheetContent = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .background(MaterialTheme.colors.surface)
            ) {
                ItemSelectionSheet(
                    items = items.value,
                    onItemSelected = { item ->
                        coroutineScope.launch {
                            selectedItem.value = item
                            bottomSheetState.hide()
                        }
                    }
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {
                    coroutineScope.launch {
                        bottomSheetState.show()
                    }
                }
            ) {
                Text(text = "Filter")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Selected Item: ${selectedItem.value}", fontSize = 20.sp)
        }
    }
}

@Composable
fun ItemSelectionSheet(items: List<String>, onItemSelected: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        items.forEach { item ->
            Text(
                text = item,
                style = defaultTextStyle,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable { onItemSelected(item) }
            )
        }
    }
}

// Step 4: Preview the composable (Optional)

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    BottomSheetScaffoldComponent()
}