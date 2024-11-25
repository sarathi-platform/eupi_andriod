package com.sarathi.dataloadingmangement.repository

import com.nudge.core.DEFAULT_LANGUAGE_CODE
import com.nudge.core.checkStringNullOrEmpty
import com.nudge.core.getFileNameFromURL
import com.nudge.core.model.ApiResponseModel
import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.core.removeExtension
import com.sarathi.dataloadingmangement.DELEGATE_COMM
import com.sarathi.dataloadingmangement.DELEGATE_DOT
import com.sarathi.dataloadingmangement.data.dao.ActivityConfigDao
import com.sarathi.dataloadingmangement.data.dao.ContentConfigDao
import com.sarathi.dataloadingmangement.data.dao.ContentDao
import com.sarathi.dataloadingmangement.data.dao.QuestionEntityDao
import com.sarathi.dataloadingmangement.data.dao.SectionEntityDao
import com.sarathi.dataloadingmangement.data.dao.SurveyAnswersDao
import com.sarathi.dataloadingmangement.data.dao.UiConfigDao
import com.sarathi.dataloadingmangement.data.dao.livelihood.LivelihoodDao
import com.sarathi.dataloadingmangement.data.entities.Content
import com.sarathi.dataloadingmangement.model.survey.response.ContentList
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
    val activityConfigDao: ActivityConfigDao,
    val livelihoodDao: LivelihoodDao,
    val sectionEntityDao: SectionEntityDao,
    val questionEntityDao: QuestionEntityDao
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
        val userIdentifier = coreSharedPrefs.getUniqueUserIdentifier()

        // Helper function to add ContentRequest
        fun addContentRequest(languageCode: String, contentKey: String?) {
            contentKey?.let {
                contentRequests.add(ContentRequest(languageCode = languageCode, contentKey = it))
            }
        }

        // Handle content from contentConfigDao
        contentConfigDao.getAllContentKey(userIdentifier).forEach {
            addContentRequest(it.languageCode, it.key)
        }

        // Handle content from uiConfigDao
        uiConfigDao.getAllIconsKey().forEach {
            addContentRequest(DEFAULT_LANGUAGE_CODE, it)
        }

        // Handle content from activityConfigDao
        activityConfigDao.getAllActivityIconsKey(userIdentifier)?.forEach {
            addContentRequest(coreSharedPrefs.getAppLanguage(), it)
        }

        // Handle survey answers
        val surveyAnswers = surveyAnswersDao.getSurveyAnswerImageKeys(
            uniqueUserIdentifier = userIdentifier,
            questionType = QuestionType.MultiImage.name
        )
        surveyAnswers?.forEach { survey ->
            survey.optionItems.forEach { optionsUiModel ->
                checkStringNullOrEmpty(optionsUiModel.selectedValue).split(DELEGATE_COMM)
                    .forEach { key ->
                        addContentRequest(
                            DEFAULT_LANGUAGE_CODE,
                            getFileNameFromURL(key).split(DELEGATE_DOT).firstOrNull()
                        )
                    }
            }
        }

        // Handle livelihood data
        livelihoodDao.getLivelihoodForUser(userIdentifier).forEach { livelihoodData ->
            livelihoodData.image?.let { image ->
                addContentRequest(
                    DEFAULT_LANGUAGE_CODE,
                    getFileNameFromURL(image.removeExtension()).split(DELEGATE_DOT).firstOrNull()
                )
            }
        }

        // Handle sectionEntityDao content
        sectionEntityDao.getSections(userIdentifier)?.map {
            it?.contentEntities?.forEach { content ->
                addContentRequest(coreSharedPrefs.getSelectedLanguageCode(), content.contentKey)
            }
        }

        // Handle questionEntityDao content
        questionEntityDao.getQuestions(userIdentifier)?.map {
            it?.contentEntities?.forEach { content ->
                addContentRequest(coreSharedPrefs.getSelectedLanguageCode(), content.contentKey)
            }
        }
        return contentRequests
    }


    override suspend fun getSelectedAppLanguage(): String {
        return coreSharedPrefs.getAppLanguage()
    }

    override suspend fun getContentList(contentKey: String, languageCode: String): ContentList? {
        return contentDao.getContentFromIds(contentKey, languageCode)?.let { contentDBData ->
            ContentList(
                contentKey = contentKey,
                contentValue = contentDBData.contentValue,
                contentType = contentDBData.contentType
            )
        }
    }

}