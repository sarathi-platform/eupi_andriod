package com.sarathi.smallgroupmodule.ui.didiTab.viewModel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.nudge.core.BLANK_STRING
import com.nudge.core.SHG_VERIFICATION_STATUS_NOT_VERIFIED
import com.nudge.core.SHG_VERIFICATION_STATUS_VERIFIED
import com.nudge.core.SHG_VERIFICATION_STATUS_VERIFIED_ID_NOT_FOUND
import com.nudge.core.helper.TranslationEnum
import com.nudge.core.model.response.ShgMember
import com.nudge.core.model.response.VillageDetailsFromLokOs
import com.nudge.core.value
import com.sarathi.dataloadingmangement.data.entities.ShgVerificationDataModel
import com.sarathi.dataloadingmangement.data.entities.SubjectEntity
import com.sarathi.dataloadingmangement.util.event.InitDataEvent
import com.sarathi.dataloadingmangement.util.event.LoaderEvent
import com.sarathi.dataloadingmangement.viewmodel.BaseViewModel
import com.sarathi.smallgroupmodule.ui.didiTab.domain.use_case.DidiVerificationUseCase
import com.sarathi.smallgroupmodule.ui.smallGroupAttendanceHistory.presentation.event.DidiVerificationEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class DidiShgVerificationViewModel @Inject constructor(
    val didiVerificationUseCase: DidiVerificationUseCase
) : BaseViewModel() {
    var isSubmitButtonEnable = mutableStateOf(false)

    var subjectId: Int = -1

    private val _subjectEntity: MutableState<SubjectEntity?> = mutableStateOf(null)
    val subjectEntity: State<SubjectEntity?> get() = _subjectEntity

    private val _shgList = mutableStateListOf<VillageDetailsFromLokOs>()
    val shgList: SnapshotStateList<VillageDetailsFromLokOs> get() = _shgList

    val selectedShg = mutableStateOf<VillageDetailsFromLokOs?>(null)

    val showShgMemberListDropDown = mutableStateOf(false)

    private val _shgMemberList = mutableStateListOf<ShgMember>()
    val shgMemberList: SnapshotStateList<ShgMember> get() = _shgMemberList

    val selectedShgMember = mutableStateOf<ShgMember?>(null)

    val showLokOsData = mutableStateOf(false)

    val lokOsDataModel = mutableStateOf<LokOsDataModel?>(LokOsDataModel.getDefaultLokOsDataObject())

    val showLoader = mutableStateOf(false)

    override fun <T> onEvent(event: T) {
        when (event) {
            is InitDataEvent.InitDataState -> {
                setTranslationConfig()
                initShgVerificationScreen()
            }

            is LoaderEvent.UpdateLoaderState -> {
                _loaderState.value = _loaderState.value.copy(
                    isLoaderVisible = event.showLoader
                )
            }

            is DidiVerificationEvent.OnShgSelected -> {
                selectedShg.value = event.selectedShg
                _shgMemberList.clear()
                selectedShgMember.value = null
                if (showLokOsData.value) {
                    showLokOsData.value = false
                }
                fetchDataForSelectedShg(selectedShg.value?.cboCode.value())
            }

            is DidiVerificationEvent.OnShgMemberSelected -> {
                selectedShgMember.value = event.selectedShgMember
                lokOsDataModel.value = LokOsDataModel.getLokOsDataModelFromShgMemberModel(
                    subjectId,
                    selectedShg.value?.cboName.value(),
                    selectedShgMember.value
                )
                if (selectedShgMember.value != null && selectedShgMember.value?.memberId != -1) {
                    showLokOsData.value = true
                }
            }

            is DidiVerificationEvent.SaveShgVerificationStatus -> {
                showLoader.value = true
                ioViewModelScope {
                    selectedShgMember.value?.let { it ->
                        var shgVerificationDataModel = ShgVerificationDataModel(
                            subjectId = subjectId,
                            shgVerificationStatus = SHG_VERIFICATION_STATUS_NOT_VERIFIED,
                            shgVerificationDate = System.currentTimeMillis(),
                            shgName = selectedShg.value?.cboName.value(),
                            shgCode = selectedShg.value?.cboCode.value(),
                            shgMemberId = selectedShgMember.value?.memberId.value()
                        )
                        shgVerificationDataModel = if (it.memberId == -1) {
                            shgVerificationDataModel.copy(shgVerificationStatus = SHG_VERIFICATION_STATUS_VERIFIED_ID_NOT_FOUND)
                        } else {
                            shgVerificationDataModel.copy(shgVerificationStatus = SHG_VERIFICATION_STATUS_VERIFIED)
                        }
                        didiVerificationUseCase.shgVerificationUseCase.saveShgVerificationStatus(
                            shgVerificationDataModel
                        )
                        //TODO Write SHG Verification Event for Sync
                        withContext(mainDispatcher) {
                            showLoader.value = false
                            event.onDataSave()
                        }
                    }
                }
            }
        }
    }

    private fun fetchDataForSelectedShg(shgCode: String) {
        ioViewModelScope {
            didiVerificationUseCase.shgVerificationUseCase.getShgDetailsFromLokOs(shgCode)?.let {
                _shgMemberList.addAll(it.shgMembers)
                _shgMemberList.add(ShgMember.getDidiIdNotFoundOption(translationHelper))
                showShgMemberListDropDown.value = true
            }
        }
    }

    fun setPreviousScreenValue(subjectId: Int) {
        this.subjectId = subjectId
    }

    private fun initShgVerificationScreen() {
        ioViewModelScope {
            _subjectEntity.value =
                didiVerificationUseCase.subjectEntityUseCase.getSubjectEntity(subjectId)
            val result =
                didiVerificationUseCase.shgVerificationUseCase.getShgForVillage(subjectEntity.value?.villageId.value())
            withContext(mainDispatcher) {
                result?.let {
                    _shgList.clear()
                    _shgList.addAll(it.value)
                }
            }

        }
    }

    fun checkSubmitButtonValidation() {
        isSubmitButtonEnable.value = selectedShg.value != null && selectedShgMember.value != null
    }

    override fun getScreenName(): TranslationEnum {
        return TranslationEnum.DidiShgVerificationScreen
    }
}

data class LokOsDataModel(
    val didiId: Int,
    val shgName: String,
    val memberId: String,
    val husbandName: String,
    val relationName: String,
    val caste: String,
    val houseNumber: String
) {
    companion object {
        fun getDefaultLokOsDataObject(): LokOsDataModel {
            return LokOsDataModel(
                -1,
                shgName = BLANK_STRING,
                memberId = BLANK_STRING,
                husbandName = BLANK_STRING,
                relationName = BLANK_STRING,
                caste = BLANK_STRING,
                houseNumber = BLANK_STRING
            )
        }

        fun getLokOsDataModelFromShgMemberModel(
            subjectId: Int,
            shgName: String,
            shgMember: ShgMember?
        ): LokOsDataModel? {
            return LokOsDataModel(
                didiId = subjectId,
                shgName = shgName,
                memberId = shgMember?.memberId.toString(),
                husbandName = shgMember?.relationName.value(),
                relationName = shgMember?.fatherHusband.value(),
                caste = shgMember?.socialCategory.value(),
                houseNumber = BLANK_STRING
            )
        }

    }
}
