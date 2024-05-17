package com.nrlm.baselinesurvey.ui.backup.domain.use_case

import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.ui.backup.domain.repository.ExportImportRepository
import com.nrlm.baselinesurvey.ui.setting.domain.SettingTagEnum
import com.nrlm.baselinesurvey.utils.BaselineCore
import com.nudge.core.model.SettingOptionModel

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
                6,
                context.getString(R.string.export_log_file),
                BLANK_STRING,
                SettingTagEnum.EXPORT_LOG_FILE.name
            )
        )
        list.add(
            SettingOptionModel(
                8,
                context.getString(R.string.export_baseline_qna),
                BLANK_STRING,
                SettingTagEnum.EXPORT_BASELINE_QNA.name
            )
        )
        list.add(
            SettingOptionModel(
                5,
                context.getString(R.string.import_data),
                BLANK_STRING,
                SettingTagEnum.IMPORT_DATA.name
            )
        )

        list.add(
                SettingOptionModel(
                    4,
                    context.getString(R.string.load_server_data),
                    BLANK_STRING,
                    SettingTagEnum.LOAD_SERVER_DATA.name
                )
        )

        list.add(
            SettingOptionModel(
                7,
                context.getString(R.string.regenerate_all_events),
                BLANK_STRING,
                SettingTagEnum.REGENERATE_EVENTS.name
            )
        )

        list.add(
            SettingOptionModel(
                8,
                "Mark Activity Inprogress",
                BLANK_STRING,
                SettingTagEnum.MARK_ACTIVITY_IN_PROGRESS.name
            )
        )

        return list.ifEmpty { arrayListOf() }
    }
}