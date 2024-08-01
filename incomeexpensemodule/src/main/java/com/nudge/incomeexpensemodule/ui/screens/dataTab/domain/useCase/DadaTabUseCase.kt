package com.nudge.incomeexpensemodule.ui.screens.dataTab.domain.useCase

import com.nudge.core.BLANK_STRING
import com.nudge.core.CoreDispatchers
import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.core.utils.CoreLogger
import kotlinx.coroutines.withContext
import javax.inject.Inject


class DadaTabUseCase @Inject constructor(
    private val coreSharedPrefs: CoreSharedPrefs,
) {

    suspend operator fun invoke(
        isRefresh: Boolean,
        onComplete: (isSuccess: Boolean, successMsg: String) -> Unit,
    ) {

        try {

            if (isRefresh || !coreSharedPrefs.isDataTabDataLoaded()) {


                withContext(CoreDispatchers.mainDispatcher) {
                    onComplete(true, BLANK_STRING)
                }
            } else {
                withContext(CoreDispatchers.mainDispatcher) {
                    onComplete(true, BLANK_STRING)
                }
            }

        } catch (ex: Exception) {
            CoreLogger.e(
                tag = "DadaTabUseCase",
                msg = "invoke: exception -> ${ex.message}",
                ex = ex,
                stackTrace = true
            )
            withContext(CoreDispatchers.mainDispatcher) {
                onComplete(true, BLANK_STRING)
            }
        }

    }

}