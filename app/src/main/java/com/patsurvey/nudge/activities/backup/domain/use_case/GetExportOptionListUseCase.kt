package com.patsurvey.nudge.activities.backup.domain.use_case

import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.utils.BaselineCore
import com.nudge.core.model.SettingOptionModel
import com.patsurvey.nudge.activities.backup.domain.repository.ExportImportRepository
import com.patsurvey.nudge.activities.settings.domain.SettingTagEnum
import com.patsurvey.nudge.utils.UPCM_USER
import com.sarathi.dataloadingmangement.model.uiModel.MissionUiModel

class GetExportOptionListUseCase(private val repository: ExportImportRepository) {

    fun fetchExportOptionList():List<SettingOptionModel>{
        val list = ArrayList<SettingOptionModel>()
        val context=BaselineCore.getAppContext()
        list.add(
                SettingOptionModel(
                    1,
                    context.getString(R.string.export_images),
                    BLANK_STRING,
                    SettingTagEnum.EXPORT_IMAGES.name
                )
            )
            list.add(
                SettingOptionModel(
                    2,
                    context.getString(R.string.export_event_file),
                    BLANK_STRING,
                    SettingTagEnum.EXPORT_BACKUP_FILE.name
                )
            )
            list.add(
                SettingOptionModel(
                    3,
                    context.getString(R.string.export_database),
                    BLANK_STRING,
                    SettingTagEnum.EXPORT_DATABASE.name
                )
            )
        list.add(
            SettingOptionModel(
                4,
                context.getString(R.string.export_log_file),
                BLANK_STRING,
                SettingTagEnum.EXPORT_LOG_FILE.name
            )
        )
        if (repository.getLoggedInUserType() == UPCM_USER) {
            list.add(
                SettingOptionModel(
                    5,
                    context.getString(R.string.export_baseline_qna),
                    BLANK_STRING,
                    SettingTagEnum.EXPORT_BASELINE_QNA.name
                )
            )
        }
        // Removing this for now in Merged build, may get completely removed later.
        list.add(
            SettingOptionModel(
                6,
                context.getString(R.string.import_data),
                BLANK_STRING,
                SettingTagEnum.IMPORT_DATA.name
            )
        )

        list.add(
            SettingOptionModel(
                    7,
                    context.getString(R.string.load_server_data),
                    BLANK_STRING,
                    SettingTagEnum.LOAD_SERVER_DATA.name
                )
        )

        list.add(
            SettingOptionModel(
                8,
                context.getString(R.string.regenerate_all_events),
                BLANK_STRING,
                SettingTagEnum.REGENERATE_EVENTS.name
            )
        )
        if(repository.getLoggedInUserType()== UPCM_USER) {
            list.add(
                SettingOptionModel(
                    9,
                    context.getString(R.string.mark_activity_inprogress_label),
                    BLANK_STRING,
                    SettingTagEnum.MARK_ACTIVITY_IN_PROGRESS.name
                )
            )
        }
        return list.ifEmpty { arrayListOf() }
    }

    suspend fun fetchMissionsForUser(): List<MissionUiModel> {
        return repository.fetchMissionsForUser()
    }
}