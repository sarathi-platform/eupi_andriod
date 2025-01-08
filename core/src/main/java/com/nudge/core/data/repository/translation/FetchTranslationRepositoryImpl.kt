package com.nudge.core.data.repository.translation

import com.nudge.core.apiService.CoreApiService
import com.nudge.core.database.dao.translation.TranslationConfigDao
import com.nudge.core.database.entities.traslation.TranslationConfigEntity
import com.nudge.core.model.ApiResponseModel
import com.nudge.core.model.response.TranslationModel
import com.nudge.core.preference.CoreSharedPrefs
import javax.inject.Inject

class FetchTranslationRepositoryImpl @Inject constructor(
    private val apiInterface: CoreApiService,
    private val coreSharedPrefs: CoreSharedPrefs,
    private val translationConfigDao: TranslationConfigDao,
) : IFetchTranslationRepository {

    /**
     * Fetch translations from the network using the state ID from shared preferences.
     */
    override suspend fun getTranslationFromNetwork(): ApiResponseModel<List<TranslationModel>> {
        return apiInterface.fetchTranslationConfigData(coreSharedPrefs.getStateId())
    }

    /**
     * Save the fetched translations into the local Room database.
     * It deletes existing data for the user and inserts new translation entities.
     */
    override suspend fun saveTranslationDataToDB(translations: List<TranslationModel>) {
        val userId = coreSharedPrefs.getUniqueUserIdentifier()

        // Delete existing translations for the current user in the DB
        translationConfigDao.deleteTranslationConfigModelForUser(userId)

        // Convert the TranslationModel list into TranslationConfigEntity list for the user
        val translationEntities = translations.flatMap { translation ->
            translation.languages.map { language ->
                TranslationConfigEntity.getTranslationConfigEntity(
                    userId = userId,
                    key = translation.key,
                    languages = language
                )
            }
        }

        // Insert the new translations into the database
        translationConfigDao.insertTranslationsConfig(translationEntities)
    }

    /**
     * Deletes all translation data for the current user from the database.
     */
    override suspend fun deleteTranslationDataFromDB() {
        val userId = coreSharedPrefs.getUniqueUserIdentifier()

        // Delete the user's translation data from the database
        translationConfigDao.deleteTranslationConfigModelForUser(userId)
    }

    /**
     * Fetch a specific translation entry from the database based on a key.
     */
    override suspend fun getTranslationAsPerKeyDataFromDB(key: String): TranslationConfigEntity? {
        val userId = coreSharedPrefs.getUniqueUserIdentifier()

        // Fetch the translation for the specific key from the database
        return translationConfigDao.getTranslationAsPerKeyConfigModel(userId, key)
    }

    /**
     * Fetch all translations for the current user from the database.
     */
    override suspend fun getTranslationsConfig(): List<TranslationConfigEntity>? {
        val userId = coreSharedPrefs.getUniqueUserIdentifier()

        // Fetch all translation configurations for the current user from the database
        return translationConfigDao.getTranslationsConfig(userId)
    }
}
