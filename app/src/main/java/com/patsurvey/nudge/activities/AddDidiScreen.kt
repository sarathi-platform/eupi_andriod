package com.patsurvey.nudge.activities

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.navigation.NavHostController
import com.patsurvey.nudge.R
import com.patsurvey.nudge.customviews.VOAndVillageBoxView
import com.patsurvey.nudge.utils.ARG_FROM_HOME
import com.patsurvey.nudge.utils.ButtonPositive

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun AddDidiScreen(navController: NavHostController, modifier: Modifier,
                  isOnline: Boolean = true, didiViewModel: AddDidiViewModel,navigateFrom:String,onNavigation:()->Unit) {
    var casteExpanded by remember { mutableStateOf(false) }
    var casteTextFieldSize by remember { mutableStateOf(Size.Zero) }


    var tolaExpended by remember { mutableStateOf(false) }
    var tolaTextFieldSize by remember { mutableStateOf(Size.Zero) }
    Column(modifier = modifier
        .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally) {
        AnimatedVisibility(visible = !navigateFrom.equals(ARG_FROM_HOME,true)) {
            NetworkBanner(
                modifier = Modifier,
                isOnline = isOnline
            )
        }
        VOAndVillageBoxView(prefRepo = didiViewModel.prefRepo,modifier=Modifier.fillMaxWidth())
        MainTitle(
            title = stringResource(id = R.string.add_didi),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .weight(1f)
                .padding(bottom = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            EditTextWithTitle(
                stringResource(id = R.string.house_number),
                modifier = Modifier
                    .padding(top = 20.dp, start = 16.dp, end = 16.dp)
                    .fillMaxWidth(),
                currentString = didiViewModel.houseNumber.value,
                isRequiredField = false
            ) {
                didiViewModel.houseNumber.value = it
                didiViewModel.validateDidiDetails()
            }

            EditTextWithTitle(
                stringResource(id = R.string.didi_name),
                modifier = Modifier
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                    .fillMaxWidth(),
                currentString = didiViewModel.didiName.value,
                isRequiredField = false
            ) {
                didiViewModel.didiName.value = it
                didiViewModel.validateDidiDetails()
            }

            EditTextWithTitle(
                stringResource(id = R.string.dada_name),
                modifier = Modifier
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                    .fillMaxWidth(),
                currentString = didiViewModel.dadaName.value,
                isRequiredField = false
            ) {
                didiViewModel.dadaName.value = it
                didiViewModel.validateDidiDetails()
            }
            DropDownWithTitle(
                title = stringResource(id = R.string.caste),
                items = didiViewModel.casteList.value,
                modifier = Modifier
                    .padding(top = 20.dp, start = 16.dp, end = 16.dp)
                    .fillMaxWidth(),
                expanded = casteExpanded,
                onExpandedChange = {
                    casteExpanded = !it
                },
                onDismissRequest = {
                    casteExpanded = false
                },
                mTextFieldSize = casteTextFieldSize,
                onGlobalPositioned = { coordinates ->
                    casteTextFieldSize = coordinates.size.toSize()
                },
                selectedItem = didiViewModel.selectedCast.value.second
            ) {
                didiViewModel.selectedCast.value=Pair(it.id,it.casteName)
                didiViewModel.validateDidiDetails()
                casteExpanded = false
            }

            DropDownWithTitle(
                title = stringResource(id = R.string.tola),
                items = didiViewModel.tolaList.value,
                modifier = Modifier
                    .padding(top = 20.dp, start = 16.dp, end = 16.dp)
                    .fillMaxWidth(),
                expanded = tolaExpended,
                onExpandedChange = {
                    tolaExpended = !it
                },
                onDismissRequest = {
                    tolaExpended = false
                },
                mTextFieldSize = tolaTextFieldSize,
                onGlobalPositioned = { coordinates ->
                    tolaTextFieldSize = coordinates.size.toSize()
                },
                selectedItem = didiViewModel.selectedTola.value.second
            ) {
                didiViewModel.selectedTola.value=Pair(it.id,it.name)
                didiViewModel.prefRepo.saveLastSelectedTola(Pair(it.id,it.name))
                didiViewModel.validateDidiDetails()
                tolaExpended = false

            }


        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = dimensionResource(id = R.dimen.dp_20))
        ) {
            ButtonPositive(
                buttonTitle = stringResource(id = R.string.add_didi),
                isArrowRequired = true,
                isActive = didiViewModel.isDidiValid.value,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                didiViewModel.saveDidiIntoDatabase()
                onNavigation()
            }
        }


    }

}

@Preview(showBackground = true)
@Composable
fun AddDidiPreview() {
    /*AddDidiScreen(navController = rememberNavController(), modifier = Modifier, didiViewModel = viewModel(),
        navigateFrom = ARG_FROM_HOME
    )*/
}