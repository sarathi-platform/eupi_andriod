package com.patsurvey.nudge.activities.ui.transect_walk

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.ui.theme.NotoSans
import com.patsurvey.nudge.activities.ui.theme.bgGreyLight
import com.patsurvey.nudge.activities.ui.theme.blueDark
import com.patsurvey.nudge.activities.ui.theme.borderGrey
import com.patsurvey.nudge.activities.ui.theme.borderGreyLight
import com.patsurvey.nudge.activities.ui.theme.brownLight
import com.patsurvey.nudge.activities.ui.theme.buttonTextStyle
import com.patsurvey.nudge.activities.ui.theme.greenOnline
import com.patsurvey.nudge.activities.ui.theme.greyBorder
import com.patsurvey.nudge.activities.ui.theme.locationIconActiveColor
import com.patsurvey.nudge.activities.ui.theme.mediumTextStyle
import com.patsurvey.nudge.activities.ui.theme.placeholderGrey
import com.patsurvey.nudge.activities.ui.theme.red
import com.patsurvey.nudge.activities.ui.theme.redDark
import com.patsurvey.nudge.activities.ui.theme.redMessageColor
import com.patsurvey.nudge.activities.ui.theme.smallTextStyle
import com.patsurvey.nudge.activities.ui.theme.smallTextStyleMediumWeight
import com.patsurvey.nudge.activities.ui.theme.smallTextStyleNormalWeight
import com.patsurvey.nudge.activities.ui.theme.textColorDark
import com.patsurvey.nudge.utils.*
import com.patsurvey.nudge.utils.LocationUtil.showPermissionDialog
import kotlinx.coroutines.CoroutineScope
import com.patsurvey.nudge.utils.TextButtonWithIcon
import com.patsurvey.nudge.utils.containsEmoji
import com.patsurvey.nudge.utils.openSettings
import com.patsurvey.nudge.utils.showCustomToast
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AddTolaBox(
    modifier: Modifier = Modifier,
    tolaName: String = "",
    isLocationAvailable: Boolean = false,
    onSaveClicked: (name: String, location: LocationCoordinates?) -> Unit,
    onCancelClicked: () -> Unit
) {
    val activity = LocalContext.current as Activity

    val context = LocalContext.current

    val focusManager = LocalFocusManager.current

    var mTolaName by remember {
        mutableStateOf(tolaName)
    }
    var locationAdded by remember {
        mutableStateOf(isLocationAvailable)
    }
    var location: LocationCoordinates? by remember { mutableStateOf(LocationCoordinates()) }

    val shouldRequestPermission = remember {
        mutableStateOf(false)
    }

    val showLoader = remember {
        mutableStateOf(false)
    }

    val showInlineLocationError = remember {
        mutableStateOf(false)
    }

    LaunchedEffect(key1 = showLoader.value) {
        if (showLoader.value) {
            delay(3000)
            location = LocationUtil.location
            if ((location!!.lat != null && location!!.long != null) && (location?.lat != 0.0 && location?.long != 0.0)) {
                locationAdded = true
            } else {
                if (showPermissionDialog) {
                    shouldRequestPermission.value = true
                } else {
                    showInlineLocationError.value = true
                }
            }
            showLoader.value = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = brownLight,
                shape = RoundedCornerShape(6.dp)
            )
            .clip(RoundedCornerShape(6.dp))
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .then(modifier)
    ) {

        if (shouldRequestPermission.value){
            ShowDialogForTolaLocation(
                title = stringResource(R.string.permission_required_prompt_title),
                message = stringResource(R.string.location_permission_dialog_prompt_message),
                setShowDialog = {
                    shouldRequestPermission.value = it
                },
                positiveButtonClicked = {
                    openSettings(context)
                    showPermissionDialog = false
                },
                cancelButtonClicked = {
                    showPermissionDialog = true
                }
            )
        }

        Column(
            modifier = Modifier
                .scrollable(rememberScrollState(), orientation = Orientation.Vertical),
        ) {
            Text(
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            color = textColorDark,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = NotoSans
                        )
                    ) {
                        append(stringResource(id = R.string.tola_name_text))
                    }
                    withStyle(
                        style = SpanStyle(
                            color = red,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = NotoSans
                        )
                    ) {
                        append("*")
                    }
                }
            )
            OutlinedTextField(
                value = mTolaName,
                onValueChange = {
                    if(!it.contains(IGNORED_REGEX)
                        && !it.contains(DOUBLE_QUOTE_REGEX)
                        && !it.contains(LEFT_BRACKET_QUOTE_REGEX)
                        && !it.contains(RIGHT_BRACKET_QUOTE_REGEX)
                        && !containsEmoji(it)){
                        mTolaName = it
                    }
                },
                placeholder = {
                    Text(text = stringResource(id = R.string.enter_name_text), style = TextStyle(
                        fontFamily = NotoSans,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    ), color = placeholderGrey)
                },
                textStyle = TextStyle(
                    fontFamily = NotoSans,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                ),
                singleLine = true,
                maxLines = 1,
                colors = TextFieldDefaults.textFieldColors(
                    textColor = textColorDark,
                    backgroundColor = Color.White,
                    focusedIndicatorColor = borderGrey,
                    unfocusedIndicatorColor = borderGrey,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = greyBorder,
                        shape = RoundedCornerShape(6.dp)
                    )
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color.White)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberRipple(
                            bounded = true,
                            color = Color.Black
                        )

                    ) {
                        showLoader.value = true
                        /*if (SDK_INT >= android.os.Build.VERSION_CODES.R) {
                            var locationByGps: Location? = null
                            var locationByNetwork: Location? = null
                            val gpsConsumer = Consumer<Location> { gpsLocation ->
                                NudgeLogger.d("AddTola", "gpsConsumer: called")
                                if (gpsLocation != null) {
                                    NudgeLogger.d(
                                        "AddTola",
                                        "gpsConsumer: gpsLocation != null => gpsLocation: $gpsLocation"
                                    )
                                    locationByGps = gpsLocation
                                    location = LocationCoordinates(
                                        locationByGps?.latitude ?: 0.0,
                                        locationByGps?.longitude ?: 0.0
                                    )
                                    locationAdded = true
                                    showLoader.value = false
                                } else {
                                    showLoader.value = false
                                    NudgeLogger.d("AddTola", "gpsConsumer: gpsLocation == null")
                                }
                                locationAdded = true
                            }
                            val networkConsumer = Consumer<Location> { networkLocation ->
                                NudgeLogger.d("AddTola", "networkLocation: called")
                                if (networkLocation != null) {
                                    NudgeLogger.d(
                                        "AddTola",
                                        "gpsConsumer: gpsLocation != null => gpsLocation: $networkLocation"
                                    )

                                    locationByNetwork = networkLocation
                                    location = LocationCoordinates(
                                        locationByNetwork?.latitude ?: 0.0,
                                        locationByNetwork?.longitude ?: 0.0
                                    )
                                    locationAdded = true
                                    showLoader.value = false
                                } else {
                                    showLoader.value = false
                                    NudgeLogger.d("AddTola", "gpsConsumer: gpsLocation == null")
                                }
                                locationAdded = true

                            }
                            LocationUtil.getLocation(activity, gpsConsumer, networkConsumer)
                        } else
                        {
                            var locationByGps: Location? = null
                            var locationByNetwork: Location? = null
                            NudgeLogger.d("AddTola", "gpsLocationListener called")
                            val gpsLocationListener: LocationListener = object : LocationListener {
                                override fun onLocationChanged(gpsLocation: Location) {
                                    NudgeLogger.d(
                                        "AddTola",
                                        "gpsLocationListener onLocationChanged: location => $location"
                                    )
                                    locationByGps = gpsLocation
                                    location = LocationCoordinates(
                                        locationByGps?.latitude ?: 0.0,
                                        locationByGps?.longitude ?: 0.0
                                    )
                                    locationAdded = true
                                    showLoader.value = false
                                }

                                override fun onStatusChanged(
                                    provider: String,
                                    status: Int,
                                    extras: Bundle
                                ) {
                                    NudgeLogger.d(
                                        "AddTola",
                                        "gpsLocationListener onStatusChanged: provider => $provider status: $status"
                                    )
                                }

                                override fun onProviderEnabled(provider: String) {
                                    NudgeLogger.d(
                                        "AddTola",
                                        "gpsLocationListener onProviderEnabled: provider => $provider"
                                    )
                                }

                                override fun onProviderDisabled(provider: String) {
                                    NudgeLogger.d(
                                        "AddTola",
                                        "gpsLocationListener onProviderDisabled: provider => $provider"
                                    )
                                }
                            }
                            NudgeLogger.d("AddTola", "networkLocationListener called")
                            val networkLocationListener: LocationListener = object :
                                LocationListener {
                                override fun onLocationChanged(networkLocation: Location) {
                                    NudgeLogger.d(
                                        "AddTola",
                                        "networkLocationListener onLocationChanged: location => $location"
                                    )
                                    locationByNetwork = networkLocation
                                    location = LocationCoordinates(
                                        locationByNetwork?.latitude ?: 0.0,
                                        locationByNetwork?.longitude ?: 0.0
                                    )
                                    locationAdded = true
                                    showLoader.value = false
                                }

                                override fun onStatusChanged(
                                    provider: String,
                                    status: Int,
                                    extras: Bundle
                                ) {
                                    NudgeLogger.d(
                                        "AddTola",
                                        "networkLocationListener onStatusChanged: provider => $provider status: $status"
                                    )
                                }

                                override fun onProviderEnabled(provider: String) {
                                    NudgeLogger.d(
                                        "AddTola",
                                        "networkLocationListener onProviderEnabled: provider => $provider"
                                    )
                                }

                                override fun onProviderDisabled(provider: String) {
                                    NudgeLogger.d(
                                        "AddTola",
                                        "networkLocationListener onProviderEnabled: provider => $provider"
                                    )
                                }
                            }
                            LocationUtil.getLocation(
                                activity,
                                gpsLocationListener,
                                networkLocationListener
                            )
                        }*/
                        focusManager.clearFocus()
                    }
                    .height(45.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (showLoader.value) {
                        Box(contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(
                                color = blueDark,
                                modifier = Modifier
                                    .size(18.dp)
                                    .align(Alignment.Center),
                                strokeWidth = 1.5.dp
                            )
                        }
                    } else {
                        Icon(
                            painter = painterResource(id = R.drawable.icon_get_location),
                            contentDescription = "Get Location",
                            modifier = Modifier.absolutePadding(top = 2.dp),
                            tint = blueDark,

                            )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (locationAdded) stringResource(R.string.location_added_text) else stringResource(R.string.get_location_text),
                        textAlign = TextAlign.Center,
                        style = smallTextStyle,
                        color = if (locationAdded) greenOnline else blueDark,
                        modifier = Modifier.absolutePadding(bottom = 2.dp)
                    )
                }
            }
            if (showInlineLocationError.value) {
                Text(
                    text = stringResource(id = R.string.location_unavailable),
                    style = mediumTextStyle,
                    textAlign = TextAlign.Start,
                    fontSize = 12.sp,
                    fontFamily = NotoSans,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Normal,
                    color = redMessageColor,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            ) {
                ButtonOutline(
                    buttonTitle = stringResource(R.string.cancel_tola_text),
                    outlineColor = redDark,
                    textColor = redDark,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(45.dp)
                        .weight(1f)
                ) {
                    onCancelClicked()
                }

                Spacer(modifier = Modifier.width(6.dp))

                ButtonPositive(
                    buttonTitle = stringResource(id = R.string.save_tola_text),
                    isArrowRequired = false,
                    isActive = mTolaName.isNotEmpty(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(45.dp)
                        .weight(1f)
                ) {
                    onSaveClicked(mTolaName, location)
                }


            }
        }
    }
}

@Composable
fun TolaBox(
    modifier: Modifier = Modifier,
    tolaName: String = "khabd",
    tolaLocation: LocationCoordinates?,
    isLocationAvailable: Boolean = false,
    isTransectWalkCompleted: Boolean = false,
    deleteButtonClicked: () -> Unit,
    listState: LazyListState,
    coroutineScope : CoroutineScope,
    index : Int,
    saveButtonClicked: (newName: String, newLocation: LocationCoordinates?) -> Unit
) {
    var showEditView by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val activity = context as Activity

    val focusManager = LocalFocusManager.current

    var mTolaName by remember {
        mutableStateOf(tolaName)
    }
    var locationAdded by remember {
        mutableStateOf(isLocationAvailable)
    }
    var location: LocationCoordinates? by remember {
        mutableStateOf(
            tolaLocation ?: LocationCoordinates()
        )
    }

    val shouldRequestPermission = remember {
        mutableStateOf(false)
    }

    val showLoader = remember {
        mutableStateOf(false)
    }

    val showInlineLocationError = remember {
        mutableStateOf(false)
    }

    LaunchedEffect(key1 = showLoader.value) {
        if (showLoader.value) {
            delay(3000)
            location = LocationUtil.location
            if ((location!!.lat != null && location!!.long != null) && (location?.lat != 0.0 && location?.long != 0.0)) {
                locationAdded = true
            } else {
                if (showPermissionDialog) {
                    shouldRequestPermission.value = true
                } else {
                    showInlineLocationError.value = true
                }
            }
            showLoader.value = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = borderGreyLight,
                shape = RoundedCornerShape(6.dp)
            )
            .clip(RoundedCornerShape(6.dp))
            .background(bgGreyLight)
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .then(modifier),
    ) {


        if (shouldRequestPermission.value){
            ShowDialogForTolaLocation(
                title = stringResource(R.string.permission_required_prompt_title),
                message = stringResource(R.string.location_permission_dialog_prompt_message),
                setShowDialog = {
                    shouldRequestPermission.value = it
                },
                positiveButtonClicked = {
                    openSettings(context)
                    showPermissionDialog = false
                },
                cancelButtonClicked = {
                    showPermissionDialog = true
                }
            )
        }


        ConstraintLayout {
            val (contentBox, showBox, editBox) = createRefs()
            if (!showEditView) {
                Column(modifier = Modifier
                    .clickable {
                        mTolaName = tolaName
                        showEditView = true
                    }
                    .constrainAs(contentBox) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                        width = Dimension.fillToConstraints
                    }) {
                    Text(
                        text = tolaName,
                        modifier = Modifier.fillMaxWidth(),
                        style = buttonTextStyle,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = textColorDark
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    )
                    {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_location_icn),
                            contentDescription = "Location Icon",
                            modifier = Modifier.absolutePadding(top = 2.dp),
                            tint = if (isLocationAvailable) greenOnline else locationIconActiveColor
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isLocationAvailable) stringResource(id = R.string.location_added_text) else stringResource(id = R.string.not_added),
                            style = smallTextStyleNormalWeight,
                            color = textColorDark
                        )
                    }
                }
                TextButtonWithIcon(
                    modifier = Modifier
                        .constrainAs(showBox) {
                            bottom.linkTo(contentBox.bottom)
                            top.linkTo(contentBox.top)
                            end.linkTo(parent.end)
                        })
                {
                    mTolaName = tolaName
                    showEditView = true
                    // edit tola
                    coroutineScope.launch {
                        delay(200)
                        NudgeLogger.e("tola->",index.toString())
                        listState.animateScrollToItem(index+2)
                    }
                }
            }
            Column(modifier = Modifier
                .constrainAs(editBox) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                }) {
                AnimatedVisibility(
                    visible = showEditView,
                ) {
                    Column(
                        modifier = Modifier,
                    ) {
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = buildAnnotatedString {
                                    withStyle(
                                        style = SpanStyle(
                                            color = textColorDark,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            fontFamily = NotoSans
                                        )
                                    ) {
                                        append(stringResource(id = R.string.tola_name_text))
                                    }
                                    withStyle(
                                        style = SpanStyle(
                                            color = red,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            fontFamily = NotoSans
                                        )
                                    ) {
                                        append("*")
                                    }
                                }
                            )
                            Icon(imageVector = Icons.Default.Close, contentDescription = null, tint = textColorDark, modifier = Modifier
                                .absolutePadding(top = 2.dp)
                                .clickable {
                                    location = tolaLocation ?: LocationCoordinates()
                                    showEditView = false
                                    showInlineLocationError.value = false
                                })
                        }
                        OutlinedTextField(
                            value = mTolaName,
                            onValueChange = {
                                if(!it.contains(IGNORED_REGEX)
                                    && !it.contains(DOUBLE_QUOTE_REGEX)
                                    && !it.contains(LEFT_BRACKET_QUOTE_REGEX)
                                    && !it.contains(RIGHT_BRACKET_QUOTE_REGEX)
                                    && !containsEmoji(it)){
                                    mTolaName = it
                                }
                            },
                            placeholder = {
                                Text(
                                    text = stringResource(id = R.string.enter_name_text),
                                    style = TextStyle(
                                        fontFamily = NotoSans,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.sp
                                    ),
                                    color = placeholderGrey
                                )
                            },
                            textStyle = TextStyle(
                                fontFamily = NotoSans,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            ),
                            singleLine = true,
                            maxLines = 1,
                            colors = TextFieldDefaults.textFieldColors(
                                textColor = textColorDark,
                                backgroundColor = Color.White,
                                focusedIndicatorColor = borderGrey,
                                unfocusedIndicatorColor = borderGrey,
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp)
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    width = 1.dp,
                                    color = greyBorder,
                                    shape = RoundedCornerShape(6.dp)
                                )
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color.White)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = rememberRipple(
                                        bounded = true,
                                        color = Color.Black
                                    )

                                ) {
                                    showLoader.value = true
                                    focusManager.clearFocus()
                                }
                                .height(45.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.Center),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (showLoader.value) {
                                    Box(contentAlignment = Alignment.Center) {
                                        CircularProgressIndicator(
                                            color = blueDark,
                                            modifier = Modifier
                                                .size(18.dp)
                                                .align(Alignment.Center),
                                            strokeWidth = 1.5.dp
                                        )
                                    }
                                } else {
                                    Icon(
                                        painter = painterResource(id = if ((location!!.lat != null && location!!.long != null) && (location?.lat != 0.0 && location?.long != 0.0)) R.drawable.baseline_location_icn else R.drawable.icon_get_location),
                                        contentDescription = "Get Location",
                                        modifier = Modifier.absolutePadding(top = 2.dp),
                                        tint = blueDark,

                                        )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if ((location!!.lat != null && location!!.long != null) && (location?.lat != 0.0 && location?.long != 0.0)) stringResource(R.string.location_added_text) else stringResource(R.string.get_location_text),
                                    textAlign = TextAlign.Center,
                                    style = smallTextStyle,
                                    color = if ((location!!.lat != null && location!!.long != null) && (location?.lat != 0.0 && location?.long != 0.0)) greenOnline else blueDark,
                                    modifier = Modifier.absolutePadding(bottom = 2.dp)
                                )
                            }
                        }
                        if (showInlineLocationError.value && !locationAdded) {
                            Text(
                                text =  stringResource(id = R.string.location_unavailable),
                                style = mediumTextStyle,
                                textAlign = TextAlign.Start,
                                fontSize = 12.sp,
                                fontFamily = NotoSans,
                                overflow = TextOverflow.Ellipsis,
                                fontWeight = FontWeight.Normal,
                                color = redMessageColor,
                                modifier = Modifier
                                    .fillMaxWidth()
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
//                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (!isTransectWalkCompleted) {
                                ButtonOutline(
                                    buttonTitle = stringResource(id = R.string.delete_tola_text),
                                    outlineColor = redDark,
                                    textColor = redDark,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(45.dp)
                                        .weight(1f)
                                ) {
                                    deleteButtonClicked()
                                    showEditView = false
                                    showInlineLocationError.value = false
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                            }
                            ButtonPositive(
                                buttonTitle = stringResource(id = R.string.save_tola_text),
                                isArrowRequired = false,
                                isActive = mTolaName.isNotEmpty(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(45.dp)
                                    .weight(1f)
                            ) {
                                if (mTolaName.isNotEmpty()) {
                                    saveButtonClicked(mTolaName, location)
                                    showEditView = false
                                    showInlineLocationError.value = false
                                } else {
                                    showCustomToast(context, context.getString(R.string.enter_tola_name_message))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ShowDialogForTolaLocation(
    title: String,
    message: String,
    setShowDialog: (Boolean) -> Unit,
    positiveButtonClicked: () -> Unit,
    cancelButtonClicked:() -> Unit,
) {
    Dialog(onDismissRequest = { setShowDialog(false) }) {
        Surface(
            shape = RoundedCornerShape(6.dp),
            color = Color.White
        ) {
            Box(contentAlignment = Alignment.Center) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = title,
                        textAlign = TextAlign.Start,
                        style = buttonTextStyle,
                        maxLines = 1,
                        color = textColorDark,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = message,
                        textAlign = TextAlign.Start,
                        style = smallTextStyleMediumWeight,
                        maxLines = 2,
                        color = textColorDark,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(modifier = Modifier.fillMaxWidth()) {
                        ButtonNegative(
                            buttonTitle = stringResource(id = R.string.cancel_tola_text),
                            isArrowRequired = false,
                            modifier = Modifier.weight(1f)
                        ) {
                            setShowDialog(false)
                            cancelButtonClicked()
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        ButtonPositive(
                            buttonTitle = stringResource(id = R.string.yes_text),
                            isArrowRequired = false,
                            modifier = Modifier.weight(1f)
                        ) {
                            positiveButtonClicked()
                            setShowDialog(false)
                        }
                    }
                }
            }
        }
    }
}