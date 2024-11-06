package com.nudge.core.helper

import android.content.Context
import android.text.TextUtils
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import com.nudge.core.BLANK_STRING
import com.nudge.core.database.dao.translation.TranslationConfigDao
import com.nudge.core.preference.CoreSharedPrefs
import javax.inject.Inject

class TranslationHelper @Inject constructor(
    private val translationConfigDao: TranslationConfigDao,
    private val coreSharedPrefs: CoreSharedPrefs
) {
    private val _translationMap = mutableStateMapOf<String, String>()
    val translationMap: SnapshotStateMap<String, String> get() = _translationMap

    /**
     * Initializes the translation map for the given TranslationEnum
     */
    suspend fun initTranslationHelper(translationEnum: TranslationEnum) {
        // Get translations from DB and populate the map
        val translations = getScreenNameKeys(translationEnum)
        translations.forEach { (key, value) ->
            _translationMap[key] = value
        }
    }

    /**
     * Returns a translated string based on the resource ID and the current language setting.
     */
    fun getString(context: Context, resId: Int): String {
        // Get the resource key from the resource ID
        val resourceKey = context.resources?.getResourceEntryName(resId)

        // Fetch the translated string from the map using the resource key
        val dbString = translationMap[resourceKey]

        // Return the translated string if available, else fallback to the default string resource
        return if (!TextUtils.isEmpty(dbString)) dbString ?: BLANK_STRING else context.getString(
            resId
        )
    }
    fun getString(context: Context, resId: Int, formatArgs: Any): String {
        // Get the resource key from the resource ID
        val resourceKey = context.resources?.getResourceEntryName(resId)

        // Fetch the translated string from the map using the resource key
        val dbString = translationMap[resourceKey]

        // If dbString is not empty, format it with formatArgs; otherwise, fallback to the default string resource
        return if (!TextUtils.isEmpty(dbString)) {
            String.format(dbString ?: BLANK_STRING, formatArgs)
        } else {
            context.getString(resId, formatArgs)
        }
    }

    fun getString(context: Context, resId: Int, formatArg1: Any, formatArg2: Any): String {
        // Get the resource key from the resource ID
        val resourceKey = context.resources?.getResourceEntryName(resId)

        // Fetch the translated string from the map using the resource key
        val dbString = translationMap[resourceKey]

        // If dbString is not empty, format it with formatArgs; otherwise, fallback to the default string resource
        return if (!TextUtils.isEmpty(dbString)) {
            String.format(dbString ?: BLANK_STRING, formatArg1, formatArg2)
        } else {
            context.getString(resId, formatArg1, formatArg2)
        }
    }

    /**
     * Wrapper around getString for easier access to string resources.
     */
    fun stringResource(context: Context, id: Int): String {
        return getString(context = context, resId = id)
    }

    fun stringResource(context: Context, resId: Int, formatArgs: Any): String {
        return getString(context = context, resId = resId, formatArgs = formatArgs)
    }

    fun stringResource(context: Context, resId: Int, formatArg1: Any, formatArg2: Any): String {
        return getString(
            context = context,
            resId = resId,
            formatArg1 = formatArg1,
            formatArg2 = formatArg2
        )
    }

    /**
     * Fetches translations from the database based on the TranslationEnum's keys.
     * Caches the translations in the map.
     */
    private suspend fun getScreenNameKeys(translationEnum: TranslationEnum): Map<String, String> {
        // Fetch translations from the database using DAO and user preferences
        val configList = translationConfigDao.getTranslationConfigForUser(
            userId = coreSharedPrefs.getUniqueUserIdentifier(),
            languageCode = coreSharedPrefs.getSelectedLanguageCode(),
            keys = translationEnum.keys
        )

        // Convert the list of TranslationConfig into a Map<String, String> (key -> value)
        return configList.associate { it.key to it.value }
    }

}
