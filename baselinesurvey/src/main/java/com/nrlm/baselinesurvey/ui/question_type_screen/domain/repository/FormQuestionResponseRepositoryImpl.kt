package com.nrlm.baselinesurvey.ui.question_type_screen.domain.repository

import androidx.lifecycle.LiveData
import com.nrlm.baselinesurvey.DEFAULT_LANGUAGE_ID
import com.nrlm.baselinesurvey.data.prefs.PrefRepo
import com.nrlm.baselinesurvey.database.dao.ContentDao
import com.nrlm.baselinesurvey.database.dao.FormQuestionResponseDao
import com.nrlm.baselinesurvey.database.dao.OptionItemDao
import com.nrlm.baselinesurvey.database.dao.QuestionEntityDao
import com.nrlm.baselinesurvey.database.entity.ContentEntity
import com.nrlm.baselinesurvey.database.entity.FormQuestionResponseEntity
import com.nrlm.baselinesurvey.database.entity.OptionItemEntity
import com.nrlm.baselinesurvey.database.entity.QuestionEntity
import javax.inject.Inject

class FormQuestionResponseRepositoryImpl @Inject constructor(
    private val questionEntityDao: QuestionEntityDao,
    private val optionItemDao: OptionItemDao,
    private val formQuestionResponseDao: FormQuestionResponseDao,
    private val prefRepo: PrefRepo,
    private val contentDao: ContentDao
) : FormQuestionResponseRepository {
    override suspend fun getFormQuestionOptions(
        surveyId: Int,
        sectionId: Int,
        questionId: Int,
        selectDefaultLanguage: Boolean
    ): List<OptionItemEntity> {
        return optionItemDao.getSurveySectionQuestionOptions(
            userId = getBaseLineUserId(),
            surveyId = surveyId,
            sectionId = sectionId,
            questionId = questionId,
            languageId = if (selectDefaultLanguage) DEFAULT_LANGUAGE_ID else getSelectedLanguage()
        )
    }

    fun getSelectedLanguage(): Int {
        return prefRepo.getAppLanguageId() ?: 2
    }

    override suspend fun getFormResponsesForQuestion(
        surveyId: Int,
        sectionId: Int,
        questionId: Int,
        didiId: Int
    ): List<FormQuestionResponseEntity> {
        return formQuestionResponseDao.getFormResponsesForQuestion(
            userId = getBaseLineUserId(),
            surveyId = surveyId,
            sectionId = sectionId,
            questionId = questionId,
            didiId = didiId
        )
    }

    override suspend fun getFormResponsesForQuestionLive(
        surveyId: Int,
        sectionId: Int,
        questionId: Int,
        didiId: Int
    ): LiveData<List<FormQuestionResponseEntity>> {
        return formQuestionResponseDao.getFormResponsesForQuestionLive(
            getBaseLineUserId(),
            surveyId,
            sectionId,
            questionId,
            didiId
        )
    }

    override suspend fun getFormResponsesForQuestionOption(
        surveyId: Int,
        sectionId: Int,
        questionId: Int,
        referenceId: String,
        didiId: Int,
        optionId: Int
    ): List<FormQuestionResponseEntity> {
        return formQuestionResponseDao.getFormResponsesForQuestionOption(
            userId = getBaseLineUserId(),
            surveyId = surveyId,
            sectionId = sectionId,
            questionId = questionId,
            referenceId = referenceId,
            didiId = didiId,
            optionId = optionId
        )
    }

    override suspend fun addFormResponseForQuestion(
        formQuestionResponseEntity: FormQuestionResponseEntity
    ) {
        formQuestionResponseEntity.userId = getBaseLineUserId()
        formQuestionResponseDao.addFormResponse(formQuestionResponseEntity)
    }

    override suspend fun updateOptionItemValue(
        surveyId: Int,
        sectionId: Int,
        questionId: Int,
        optionId: Int,
        selectedValue: String,
        referenceId: String,
        didiId: Int
    ) {
        return formQuestionResponseDao.updateOptionItemValue(
            userId = getBaseLineUserId(),
            surveyId = surveyId,
            sectionId = sectionId,
            questionId = questionId,
            optionId = optionId,
            selectedValue = selectedValue,
            referenceId = referenceId,
            didiId = didiId
        )
    }

    override suspend fun getFormResponseForReferenceId(referenceId: String): List<FormQuestionResponseEntity> {
        return formQuestionResponseDao.getFormResponseForReferenceId(
            getBaseLineUserId(),
            referenceId
        )
    }

    override suspend fun deleteFormQuestionResponseForReferenceId(referenceId: String) {
        formQuestionResponseDao.deleteFormResponseQuestionForReferenceId(
            userId = getBaseLineUserId(),
            referenceId = referenceId
        )
    }

    override suspend fun saveFormsIntoDB(form: List<FormQuestionResponseEntity>) {
        formQuestionResponseDao.addFormResponseList(form)
    }

    override suspend fun deleteFormQuestionResponseForOption(
        optionId: Int,
        questionId: Int,
        sectionId: Int,
        surveyId: Int,
        surveyeeId: Int
    ) {
        formQuestionResponseDao.deleteFormResponseQuestionForOption(
            userId = getBaseLineUserId(),
            optionId = optionId,
            questionId = questionId,
            sectionId = sectionId,
            surveyId = surveyId,
            surveyeeId = surveyeeId
        )
    }

    override suspend fun updateFromListItemIntoDb(formQuestionResponseEntity: FormQuestionResponseEntity) {
        formQuestionResponseDao.updateOptionItemValue(
            surveyId = formQuestionResponseEntity.surveyId,
            sectionId = formQuestionResponseEntity.sectionId,
            questionId = formQuestionResponseEntity.questionId,
            optionId = formQuestionResponseEntity.optionId,
            selectedValue = formQuestionResponseEntity.selectedValue,
            referenceId = formQuestionResponseEntity.referenceId,
            didiId = formQuestionResponseEntity.didiId,
            userId = getBaseLineUserId()
        )
    }

    override suspend fun getOptionItem(formQuestionResponseEntity: FormQuestionResponseEntity): Int {
        return formQuestionResponseDao.getOptionItem(
            surveyId = formQuestionResponseEntity.surveyId,
            sectionId = formQuestionResponseEntity.sectionId,
            questionId = formQuestionResponseEntity.questionId,
            optionId = formQuestionResponseEntity.optionId,
            referenceId = formQuestionResponseEntity.referenceId,
            didiId = formQuestionResponseEntity.didiId,
            userId = getBaseLineUserId()
        )
    }

    override suspend fun getFormQuestionForId(
        surveyId: Int,
        sectionId: Int,
        questionId: Int
    ): QuestionEntity? {
        return questionEntityDao.getFormQuestionForId(
            userid = getBaseLineUserId(),
            surveyId,
            sectionId,
            questionId,
            languageId = prefRepo.getAppLanguageId() ?: DEFAULT_BUFFER_SIZE
        )
    }

    override suspend fun getFormQuestionCountForSection(
        surveyId: Int,
        sectionId: Int,
        didiId: Int
    ): List<FormQuestionResponseEntity> {
        return formQuestionResponseDao.getFormQuestionCountForSection(
            surveyId = surveyId,
            sectionId = sectionId,
            didiId = didiId,
            userId = getBaseLineUserId()
        )
    }

    override suspend fun getQuestionTag(surveyId: Int, sectionId: Int, questionId: Int): Int {
        return questionEntityDao.getQuestionTag(
            userid = getBaseLineUserId(),
            surveyId,
            sectionId,
            questionId
        )
    }

    override suspend fun getContentFromDB(contentKey: String): ContentEntity {
        return contentDao.getContentFromIds(contentKey, languageId = getSelectedLanguage())
    }

    override fun getBaseLineUserId(): String {
        return prefRepo.getUniqueUserIdentifier()
    }

}

