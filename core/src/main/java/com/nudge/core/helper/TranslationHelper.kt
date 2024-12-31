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

    suspend fun initTranslationHelper(translationEnum: TranslationEnum) {
        val translations = getScreenNameKeys(translationEnum)
        translations.forEach { (key, value) ->
            _translationMap[key] = value
        }
    }

    fun getString(context: Context, resId: Int): String {
        val resourceKey = context.resources?.getResourceEntryName(resId)
        val dbString = translationMap[resourceKey]
        return if (!TextUtils.isEmpty(dbString)) dbString ?: BLANK_STRING else context.getString(
            resId
        )
    }

    fun getString(context: Context, resId: Int, vararg formatArgs: Any): String {
        val resourceKey = context.resources?.getResourceEntryName(resId)
        val dbString = translationMap[resourceKey]
        return if (!dbString.isNullOrEmpty()) {
            String.format(dbString, *formatArgs)
        } else {
            context.getString(resId, *formatArgs)
        }
    }

    fun stringResource(context: Context, id: Int): String {
        return getString(context = context, resId = id)
    }

    fun stringResource(context: Context, resId: Int, vararg formatArgs: Any): String {
        return getString(context = context, resId = resId, formatArgs = formatArgs)
    }

    private suspend fun getScreenNameKeys(translationEnum: TranslationEnum): Map<String, String> {
        val configList = translationConfigDao.getTranslationConfigForUser(
            userId = coreSharedPrefs.getUniqueUserIdentifier(),
            languageCode = coreSharedPrefs.getSelectedLanguageCode(),
            keys = translationEnum.keys
        )
        return configList.associate { it.key to it.value }
    }
}
