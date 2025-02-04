package com.nudge.core.helper

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import com.nudge.core.DOUBLE_SLASH_N
import com.nudge.core.SLASH_N
import com.nudge.core.database.dao.translation.TranslationConfigDao
import com.nudge.core.preference.CoreSharedPrefs
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class TranslationHelper @Inject constructor(
    private val translationConfigDao: TranslationConfigDao,
    private val coreSharedPrefs: CoreSharedPrefs,
    @ApplicationContext private val context: Context
) {
    private val _translationMap = mutableStateMapOf<String, String>()
    private val translationMap: SnapshotStateMap<String, String> get() = _translationMap

    suspend fun initTranslationHelper(translationEnum: TranslationEnum) {
        val translations = getScreenNameKeys(translationEnum)
        translationEnum.keys.forEach {
            _translationMap.remove(it)
        }
        translations.forEach { (key, value) ->
            _translationMap[key] = value
        }
    }

    fun getString(resId: Int): String {
        val resourceKey = context.resources?.getResourceEntryName(resId)
        val dbString = translationMap[resourceKey]
        return if (!dbString.isNullOrEmpty()) {
            dbString.replace(DOUBLE_SLASH_N, SLASH_N)
        } else {
            context.getString(
                resId
            )
        }
    }

    fun getString(resId: Int, vararg formatArgs: Any): String {
        val resourceKey = context.resources?.getResourceEntryName(resId)
        val dbString = translationMap[resourceKey]
        return if (!dbString.isNullOrEmpty()) {
            String.format(dbString.replace(DOUBLE_SLASH_N, SLASH_N), *formatArgs)
        } else {
            context.getString(resId, *formatArgs)
        }
    }

    fun stringResource(id: Int): String {
        return getString(resId = id)
    }

    fun stringResource(resId: Int, vararg formatArgs: Any): String {
        return getString(resId = resId, formatArgs = formatArgs)
    }

    private suspend fun getScreenNameKeys(translationEnum: TranslationEnum): Map<String, String> {
        val configList = translationConfigDao.getTranslationConfigForUser(
            userId = coreSharedPrefs.getUniqueUserIdentifier(),
            languageCode = coreSharedPrefs.getSelectedLanguageCode(),
            keys = translationEnum.keys
        )
        return configList.associate { it.key to it.value }
    }

    fun getPluralString(resId: Int, quantity: Int): String {
        val resourceKey = context.resources?.getResourceEntryName(resId)
        val dbString = translationMap[resourceKey]

        return if (!dbString.isNullOrEmpty()) {
            dbString
        } else {
            context.resources.getQuantityString(resId, quantity)
        }
    }
}


val LocalTranslationHelper = compositionLocalOf<TranslationHelper> {
    error("No TranslationHelper provided")
}

@Composable
fun ProvideTranslationHelper(
    translationHelper: TranslationHelper,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalTranslationHelper provides translationHelper) {
        content()
    }
}
