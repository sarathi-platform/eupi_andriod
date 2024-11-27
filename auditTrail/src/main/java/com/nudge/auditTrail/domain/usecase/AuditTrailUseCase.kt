package com.nudge.auditTrail.domain.usecase

import android.content.Context
import android.os.Build
import com.nudge.auditTrail.APP_BUILD_NUMBER
import com.nudge.auditTrail.APP_VERSION
import com.nudge.auditTrail.BRAND
import com.nudge.auditTrail.DEVICE_ID
import com.nudge.auditTrail.MODEL
import com.nudge.auditTrail.OS_VERSION
import com.nudge.auditTrail.domain.repository.AuditTrailRepository
import com.nudge.core.model.CoreAppDetails
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AuditTrailUseCase@Inject constructor(
    private val repository: AuditTrailRepository,
    @ApplicationContext private val context: Context
) {

    fun getDefaultPropertyOfDevice() : Map<String,Any>{

        var metaDataMap = hashMapOf<String, Any>(
            OS_VERSION to Build.VERSION.RELEASE,
            BRAND to  Build.BRAND,
            MODEL to Build.MODEL,
            APP_VERSION to context.packageManager.getPackageInfo(context.packageName, 0).versionName,
            APP_BUILD_NUMBER to CoreAppDetails.getApplicationDetails()!!.buildVersion   ,
            DEVICE_ID to Build.ID,
        )
        return  metaDataMap
    }
    suspend fun invoke(
        auditDetailProperties: HashMap<String, Any>,
        actionType: String,
        actionStatusType: String,
        message:String
    ) {
        auditDetailProperties.putAll(getDefaultPropertyOfDevice())
        repository.insertEvent(auditDetailProperties, actionType, actionStatusType, message)

    }


}