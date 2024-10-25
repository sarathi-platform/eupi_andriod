package com.nudge.core.usecase

import com.nudge.core.SOMETHING_WENT_WRONG
import com.nudge.core.data.repository.SyncMigrationRepository
import com.nudge.core.model.CoreAppDetails
import com.nudge.core.utils.CoreLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class SyncMigrationUseCase @Inject constructor(
    val repository: SyncMigrationRepository
) {
    fun deleteEventsAfter1To2Migration() {
        CoroutineScope(Dispatchers.IO).launch {
            if (repository.isSyncDBMigrate()) {
                try {
                    repository.deleteEventsAfter1To2Migration()
                    CoreLogger.d(
                        CoreAppDetails.getApplicationContext().applicationContext,
                        "SyncMigrationUseCase",
                        "Delete Events Details after Migration"
                    )
                } catch (e: Exception) {
                    CoreLogger.d(
                        CoreAppDetails.getApplicationContext().applicationContext,
                        "SyncMigrationUseCase Exception: ",
                        e.message ?: SOMETHING_WENT_WRONG
                    )
                } finally {
                    repository.setSyncDBMigrateToFalse()
                }
            }
        }
    }
}