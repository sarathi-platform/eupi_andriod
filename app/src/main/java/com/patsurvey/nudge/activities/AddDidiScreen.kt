package com.patsurvey.nudge.activities

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.patsurvey.nudge.R
import com.patsurvey.nudge.customviews.CustomSnackBarShow
import com.patsurvey.nudge.customviews.VOAndVillageBoxView
import com.patsurvey.nudge.customviews.rememberSnackBarState
import com.patsurvey.nudge.intefaces.LocalDbListener
import com.patsurvey.nudge.intefaces.NetworkCallbackListener
import com.patsurvey.nudge.utils.*
import kotlinx.coroutines.delay

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun AddDidiScreen(navController: NavHostController, modifier: Modifier,
                  isOnline: Boolean = true, didiViewModel: AddDidiViewModel?=null,
                  didiDetailId:Int,onNavigation:()->Unit) {
    var casteExpanded by remember { mutableStateOf(false) }
    var casteTextFieldSize by remember { mutableStateOf(Size.Zero) }
    var editDidiId by remember { mutableStateOf(-1) }
    var tolaExpended by remember { mutableStateOf(false) }
    var tolaTextFieldSize by remember { mutableStateOf(Size.Zero) }
    var topPadding by remember { mutableStateOf(5.dp) }
    var startPadding by remember { mutableStateOf(16.dp) }
    var endPadding by remember { mutableStateOf(16.dp) }
    val snackState= rememberSnackBarState()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    LaunchedEffect(key1 = true) {
        if(didiDetailId!=0){
            didiViewModel?.fetchDidiDetails(didiDetailId)
            editDidiId=didiDetailId
        }else{
            didiViewModel?.fetchLastSelectedTola()
        }
        delay(200)
        didiViewModel?.checkIfTolaIsNotDeleted()
        didiViewModel?.checkIfTolaIsUpdated()
    }

    Column(modifier = modifier
        .fillMaxSize()
        .padding(top = 14.dp)
        .pointerInput(true) {
            detectTapGestures(onTap = {
                focusManager.clearFocus()
            })
        },
        horizontalAlignment = Alignment.CenterHorizontally) {

        didiViewModel?.addDidiRepository?.prefRepo?.let {
            VOAndVillageBoxView(
                prefRepo = it,
                modifier = Modifier.fillMaxWidth(),
                bottomPadding = 14.dp
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .weight(1f)
                .padding(bottom = 20.dp)
                .padding(horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            MainTitle(
                title = stringResource(id = R.string.add_didi),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp)
            )

            EditTextWithTitle(
                stringResource(id = R.string.didi_name),
                modifier = Modifier
                    .padding(top = topPadding, start = startPadding, end = endPadding)
                    .fillMaxWidth(),
                currentString = didiViewModel?.didiName?.value?: BLANK_STRING,
                isRequiredField = false
            ) {
                didiViewModel?.didiName?.value = it
                didiViewModel?.validateDidiDetails()
            }

            EditTextWithTitle(
                stringResource(id = R.string.dada_name),
                modifier = Modifier
                    .padding(top = topPadding, start = startPadding, end = endPadding)
                    .fillMaxWidth(),
                currentString = didiViewModel?.dadaName?.value ?: BLANK_STRING,
                isRequiredField = false
            ) {
                didiViewModel?.dadaName?.value = it
                didiViewModel?.validateDidiDetails()
            }

            EditTextWithTitle(
                stringResource(id = R.string.house_number),
                modifier = Modifier
                    .padding(top = topPadding, start = startPadding, end = endPadding)
                    .fillMaxWidth(),
                currentString = didiViewModel?.houseNumber?.value ?: BLANK_STRING,
                isRequiredField = false
            ) {
                didiViewModel?.houseNumber?.value = it
                didiViewModel?.validateDidiDetails()
            }

            DropDownWithTitle(
                title = stringResource(id = R.string.caste),
                items = didiViewModel?.casteList?.value?: emptyList(),
                modifier = Modifier
                    .padding(top = topPadding, start = startPadding, end = endPadding)
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
                selectedItem = didiViewModel?.selectedCast?.value?.second ?: BLANK_STRING
            ) {
                didiViewModel?.selectedCast?.value=Pair(it.id,it.casteName)
                didiViewModel?.validateDidiDetails()
                casteExpanded = false
            }

            DropDownWithTitle(
                title = stringResource(id = R.string.tola),
                items = didiViewModel?.tolaList?.value ?: emptyList(),
                modifier = Modifier
                    .padding(top = topPadding, start = startPadding, end = endPadding)
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
                selectedItem = didiViewModel?.selectedTola?.value?.second ?: BLANK_STRING
            ) {
                val tolaName =
                    if (it.name == EMPTY_TOLA_NAME)
                        NO_TOLA_TITLE
                    else
                        it.name
                didiViewModel?.selectedTola?.value=Pair(it.id,tolaName)
                didiViewModel?.saveLastSelectedTolaForVillage(it.id,tolaName)
                didiViewModel?.validateDidiDetails()
                tolaExpended = false

            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = dimensionResource(id = R.dimen.dp_20))
        ) {
            ButtonPositiveWithDebounce(
                buttonTitle = if (didiDetailId == 0) stringResource(id = R.string.add_didi)
                else stringResource(id = R.string.update_didi),
                isArrowRequired = true,
                isActive = didiViewModel?.isDidiValid?.value == true,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                didiViewModel?.validateDidiDetails()
                if (didiDetailId == 0) {
                    didiViewModel?.saveDidiIntoDatabase(
                        (context as MainActivity).isOnline.value ?: false,
                        object : LocalDbListener {
                       override fun onInsertionSuccess() {
                           onNavigation()
                           showCustomToast(context,context.getString(R.string.didi_has_been_successfully_added,didiViewModel?.didiName?.value))
                       }
                       override fun onInsertionFailed() {
                           showCustomToast(context,context.getString(R.string.didi_already_exist))
                       }
                   }, object : NetworkCallbackListener{
                       override fun onSuccess() {
                       }

                       override fun onFailed() {
                           Log.d("AddDidiScreen saveDidiIntoDatabase onFailed: ", "Online Sync Failed")
//                           showCustomToast(context, SYNC_FAILED)
                       }

                   })
                }
                else{
                    didiViewModel?.updateDidiIntoDatabase(editDidiId, isOnline = (context as MainActivity).isOnline.value ?: false,
                        object : NetworkCallbackListener{
                        override fun onSuccess() {
                        }

                        override fun onFailed() {
                            Log.d("AddDidiScreen updateDidiIntoDatabase onFailed: ", "Online Sync Failed")
//                            showCustomToast(context, SYNC_FAILED)
                        }

                    }, object : LocalDbListener{
                            override fun onInsertionSuccess() {
                                showCustomToast(context,context.getString(R.string.didi_has_been_successfully_updated))
                                onNavigation()
                            }

                            override fun onInsertionFailed() {
                                showCustomToast(context,context.getString(R.string.didi_already_exist))
                            }

                        })
                }

            }
        }


    }
    CustomSnackBarShow(state = snackState)
}

@Preview(showBackground = true)
@Composable
fun AddDidiPreview() {
    AddDidiScreen(navController = rememberNavController(), modifier = Modifier,
        isOnline = true, didiDetailId = 0, onNavigation = {})
}