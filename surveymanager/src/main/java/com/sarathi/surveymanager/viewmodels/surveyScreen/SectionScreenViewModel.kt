package com.sarathi.surveymanager.viewmodels.surveyScreen

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.sarathi.dataloadingmangement.data.entities.Content
import com.sarathi.dataloadingmangement.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SectionScreenViewModel @Inject constructor(

) : BaseViewModel() {

    private var surveyId: Int = 0
    private var sectionId: Int = 0
    private var taskId: Int = 0
    private var subjectType: String = ""
    private var activityConfigId: Int = 0

    private val _contentList = mutableStateOf<List<Content>>(emptyList())
    val contentList: State<List<Content>> get() = _contentList

    override fun <T> onEvent(event: T) {
        TODO("Not yet implemented")
    }

    override fun refreshData() {
        super.refreshData()

    }

    fun setPreviousScreenData(
        surveyId: Int,
        sectionId: Int,
        taskId: Int,
        subjectType: String,
        activityConfigId: Int,
    ) {
        this.surveyId = surveyId
        this.sectionId = sectionId
        this.taskId = taskId
        this.subjectType = subjectType
        this.activityConfigId = activityConfigId
    }

    fun handleMediaContentClick(
        contentKey: String, callNavigation: (
            contentType: String,
            contentTitle: String
        ) -> Unit
    ) {
        val content = contentList.value.find { it.contentKey == contentKey }
        content?.let {
            callNavigation(content.contentType, content.contentValue)
        }

    }

}