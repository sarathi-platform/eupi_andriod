package com.patsurvey.nudge.activities

import android.annotation.SuppressLint
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.gson.Gson
import com.patsurvey.nudge.R
import com.patsurvey.nudge.customviews.CustomSnackBarShow
import com.patsurvey.nudge.customviews.VOAndVillageBoxView
import com.patsurvey.nudge.customviews.rememberSnackBarState
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.intefaces.LocalDbListener
import com.patsurvey.nudge.utils.ADD_DIDI_BLANK_STRING
import com.patsurvey.nudge.utils.BLANK_STRING
import com.patsurvey.nudge.utils.ButtonPositive
import com.patsurvey.nudge.utils.showCustomToast

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun AddDidiScreen(navController: NavHostController, modifier: Modifier,
                  isOnline: Boolean = true, didiViewModel: AddDidiViewModel,didiDetails:String,onNavigation:()->Unit) {
    var casteExpanded by remember { mutableStateOf(false) }
    var casteTextFieldSize by remember { mutableStateOf(Size.Zero) }
    var editDidiId by remember { mutableStateOf(-1) }
    var tolaExpended by remember { mutableStateOf(false) }
    var tolaTextFieldSize by remember { mutableStateOf(Size.Zero) }
    val snackState= rememberSnackBarState()
    val context = LocalContext.current
    Column(modifier = modifier
        .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally) {

        VOAndVillageBoxView(prefRepo = didiViewModel.prefRepo,modifier=Modifier.fillMaxWidth())

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .weight(1f)
                .padding(bottom = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            MainTitle(
                title = stringResource(id = R.string.add_didi),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp)
            )

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
                didiViewModel.saveLastSelectedTolaForVillage(it.id,it.name)
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
                buttonTitle = if(didiDetails.equals(ADD_DIDI_BLANK_STRING,true)) stringResource(id = R.string.add_didi)
                else stringResource(id = R.string.update_didi),
                isArrowRequired = true,
                isActive =  if(didiDetails.equals(ADD_DIDI_BLANK_STRING,true)) didiViewModel.isDidiValid.value else true,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                if(didiDetails.equals(ADD_DIDI_BLANK_STRING,true)) {
                   didiViewModel.saveDidiIntoDatabase(object :LocalDbListener{
                       override fun onInsertionSuccess() {
                           onNavigation()
                           showCustomToast(context,context.getString(R.string.didi_has_been_successfully_added))
                       }
                       override fun onInsertionFailed() {
                           showCustomToast(context,context.getString(R.string.didi_already_exist))
                       }
                   })
                }
                else{
                    didiViewModel.updateDidiIntoDatabase(editDidiId)
                    showCustomToast(context,context.getString(R.string.didi_has_been_successfully_updated))
                    onNavigation()
                }

            }
        }


    }

    LaunchedEffect(key1 = Unit){
        if(!didiDetails.equals(ADD_DIDI_BLANK_STRING,true)){
            // TODO: Need to improve after using Parcable or Serializable
            val editDidiDetails=Gson().fromJson(didiDetails,DidiEntity::class.java)
            editDidiId=editDidiDetails.id
            didiViewModel.didiName.value=editDidiDetails.name
            didiViewModel.dadaName.value=editDidiDetails.guardianName
            didiViewModel.houseNumber.value=editDidiDetails.address
            didiViewModel.selectedTola.value=Pair(editDidiDetails.cohortId,editDidiDetails.cohortName)
            didiViewModel.selectedCast.value=Pair(editDidiDetails.castId,editDidiDetails.castName)
        }
    }
    CustomSnackBarShow(state = snackState)
}

@Preview(showBackground = true)
@Composable
fun AddDidiPreview() {
    AddDidiScreen(navController = rememberNavController(), modifier = Modifier, didiViewModel = viewModel(),
        isOnline = true, didiDetails = BLANK_STRING){
    }
}