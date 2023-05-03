package com.patsurvey.nudge.activities

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.patsurvey.nudge.R
import com.patsurvey.nudge.model.dataModel.DidiDetailsModel
import com.patsurvey.nudge.utils.DoubleButtonBox

@Composable
fun AddDidiScreen(navController: NavHostController, modifier: Modifier, isOnline: Boolean = true, didiViewModel: AddDidiViewModel) {
    val context = LocalContext.current
    var houseNumber by remember {
        mutableStateOf("")
    }
    var didiName by remember {
        mutableStateOf("")
    }
    var dadaName by remember {
        mutableStateOf("")
    }
    val castes = listOf("Hindu", "Muslim", "Sikh", "Isai")
    var casteExpanded by remember { mutableStateOf(false) }
    var casteSelectedText by remember { mutableStateOf("") }
    var casteTextFieldSize by remember { mutableStateOf(Size.Zero) }

    val tolas = listOf("Hindu", "Muslim", "Sikh", "Isai")
    var tolaExpended by remember { mutableStateOf(false) }
    var tolaSelectedText by remember { mutableStateOf("") }
    var tolaTextFieldSize by remember { mutableStateOf(Size.Zero) }
    Column(modifier = modifier
        .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally) {
        NetworkBanner(
            modifier = Modifier,
            isOnline = isOnline
        )
        MainTitle(
            title = stringResource(id = R.string.add_didi),
            modifier = Modifier.padding(top = 30.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .weight(1f).padding(bottom = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            EditTextWithTitle(
                stringResource(id = R.string.house_number),
                modifier = Modifier
                    .padding(top = 20.dp, start = 16.dp, end = 16.dp)
                    .fillMaxWidth(),
                currentString = houseNumber,
                isRequiredField = false
            ) {
                houseNumber = it
            }

            EditTextWithTitle(
                stringResource(id = R.string.didi_name),
                modifier = Modifier
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                    .fillMaxWidth(),
                currentString = didiName,
                isRequiredField = false
            ) {
                didiName = it
            }

            EditTextWithTitle(
                stringResource(id = R.string.dada_name),
                modifier = Modifier
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                    .fillMaxWidth(),
                currentString = dadaName,
                isRequiredField = false
            ) {
                dadaName = it
            }
            DropDownWithTitle(
                title = stringResource(id = R.string.caste),
                items = castes,
                modifier = Modifier
                    .padding(top = 20.dp, start = 16.dp, end = 16.dp)
                    .fillMaxWidth(),
                expanded = casteExpanded,
                onExpandedChange = {
                    Log.i("AddDidi", "Expended state: $it")
                    casteExpanded = !it
                },
                onDismissRequest = {
                    casteExpanded = false
                },
                mTextFieldSize = casteTextFieldSize,
                onGlobalPositioned = { coordinates ->
                    casteTextFieldSize = coordinates.size.toSize()
                },
                selectedItem = casteSelectedText
            ) {
                Log.i("AddDidi", "on item selected state: $it")
                casteSelectedText = it
                casteExpanded = false
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }

            DropDownWithTitle(
                title = stringResource(id = R.string.tola),
                items = tolas,
                modifier = Modifier
                    .padding(top = 20.dp, start = 16.dp, end = 16.dp)
                    .fillMaxWidth(),
                expanded = tolaExpended,
                onExpandedChange = {
                    Log.i("AddDidi", "Expended state: $it")
                    tolaExpended = !it
                },
                onDismissRequest = {
                    tolaExpended = false
                },
                mTextFieldSize = tolaTextFieldSize,
                onGlobalPositioned = { coordinates ->
                    tolaTextFieldSize = coordinates.size.toSize()
                },
                selectedItem = tolaSelectedText
            ) {
                Log.i("AddDidi", "on item selected state: $it")
                tolaSelectedText = it
                tolaExpended = false
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }


        }

        DoubleButtonBox(
            modifier = Modifier.shadow(10.dp),
            negativeButtonRequired = false,
            positiveButtonText = stringResource(id = R.string.add_didi),
            positiveButtonOnClick = {
                val allData = "$houseNumber, $didiName, $dadaName, $casteSelectedText, $tolaSelectedText"
                Toast.makeText(context, allData, Toast.LENGTH_SHORT).show()
                didiViewModel.addDidiFromData(houseNumber, didiName, dadaName, casteSelectedText, tolaSelectedText)
                navController.popBackStack()
            },
            negativeButtonOnClick = {

            }
        )
    }

}

@Preview(showBackground = true)
@Composable
fun AddDidiPreview() {
    AddDidiScreen(navController = rememberNavController(), modifier = Modifier, didiViewModel = viewModel())
}