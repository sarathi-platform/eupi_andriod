package com.sarathi.dataloadingmangement.repository

import com.nudge.core.DEFAULT_LANGUAGE_CODE
import com.nudge.core.checkStringNullOrEmpty
import com.nudge.core.getFileNameFromURL
import com.nudge.core.model.ApiResponseModel
import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.DELEGATE_COMM
import com.sarathi.dataloadingmangement.DELEGATE_DOT
import com.sarathi.dataloadingmangement.data.dao.ActivityConfigDao
import com.sarathi.dataloadingmangement.data.dao.ContentConfigDao
import com.sarathi.dataloadingmangement.data.dao.ContentDao
import com.sarathi.dataloadingmangement.data.dao.SurveyAnswersDao
import com.sarathi.dataloadingmangement.data.dao.UiConfigDao
import com.sarathi.dataloadingmangement.data.entities.Content
import com.sarathi.dataloadingmangement.network.DataLoadingApiService
import com.sarathi.dataloadingmangement.network.request.ContentRequest
import com.sarathi.dataloadingmangement.network.response.ContentResponse
import com.sarathi.dataloadingmangement.util.constants.QuestionType
import javax.inject.Inject

class ContentRepositoryImpl @Inject constructor(
    val apiInterface: DataLoadingApiService,
    val contentDao: ContentDao,
    val contentConfigDao: ContentConfigDao,
    val uiConfigDao: UiConfigDao,
    val coreSharedPrefs: CoreSharedPrefs,
    val surveyAnswersDao: SurveyAnswersDao,
    val activityConfigDao: ActivityConfigDao
) : IContentRepository {
    override suspend fun fetchContentsFromServer(contentMangerRequest: List<ContentRequest>): ApiResponseModel<List<ContentResponse>> {
        return apiInterface.fetchContentData(contentMangerRequest)
    }

    override suspend fun saveContentToDB(contents: List<Content>) {
        contentDao.insertContent(contents)
    }

    override suspend fun deleteContentFromDB() {
        contentDao.deleteContent(coreSharedPrefs.getUniqueUserIdentifier())
    }

    override suspend fun getContentData(): List<Content> {
        return contentDao.getContentData()
    }

    override suspend fun getAllContentRequest(): List<ContentRequest> {
        val contentRequests = ArrayList<ContentRequest>()
        contentConfigDao.getAllContentKey(
            coreSharedPrefs.getUniqueUserIdentifier(),
        ).forEach {
            contentRequests.add(ContentRequest(languageCode = it.languageCode, contentKey = it.key))
        }

        uiConfigDao.getAllIconsKey().forEach {
            contentRequests.add(
                ContentRequest(
                    languageCode = DEFAULT_LANGUAGE_CODE,
                    contentKey = it
                )
            )
        }
        activityConfigDao.getAllActivityIconsKey(coreSharedPrefs.getUniqueUserIdentifier())?.forEach {
            contentRequests.add(
                ContentRequest(
                    languageCode = DEFAULT_LANGUAGE_CODE,
                    contentKey = it
                )
            )
        }
        val surveyyAnswers = surveyAnswersDao.getSurveyAnswerImageKeys(
            uniqueUserIdentifier = coreSharedPrefs.getUniqueUserIdentifier(),
            questionType = QuestionType.MultiImage.name
        )
        surveyyAnswers?.forEach { survey ->
            survey.optionItems.forEach { optionsUiModel ->
                val imageKeys =
                    checkStringNullOrEmpty(optionsUiModel.selectedValue).split(DELEGATE_COMM)
                imageKeys.forEach { key ->
                    contentRequests.add(
                        ContentRequest(
                            languageCode = DEFAULT_LANGUAGE_CODE,
                            contentKey = getFileNameFromURL(key).split(DELEGATE_DOT).firstOrNull()
                                ?: BLANK_STRING
                        )
                    )
                }
            }

        }
        return contentRequests
    }


    override suspend fun getSelectedAppLanguage(): String {
        return coreSharedPrefs.getAppLanguage()
    }

}