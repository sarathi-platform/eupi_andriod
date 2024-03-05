package com.patsurvey.nudge.model.request

import com.google.gson.annotations.SerializedName
import com.patsurvey.nudge.database.StepListEntity

data class AddWorkFlowRequest(
    @SerializedName("status") var status: String,
    @SerializedName("villageId") var villageId: Int,
    @SerializedName("programId") var programId: Int,
    @SerializedName("programsProcessId") var programsProcessId : Int
)

data class UpdateWorkflowRequest(
    @SerializedName("workflowId") var workflowId: Int,
    @SerializedName("status") var status: String,
    @SerializedName("villageId") var villageId: Int,
    @SerializedName("programId") var programId: Int,
    @SerializedName("programsProcessId") var programsProcessId : Int
) {
    companion object {
        fun getUpdateWorkflowRequest(stepListEntity: StepListEntity, status: String): UpdateWorkflowRequest {
            return UpdateWorkflowRequest(
                workflowId = stepListEntity.workFlowId,
                villageId = stepListEntity.villageId,
                programId = stepListEntity.programId,
                programsProcessId = stepListEntity.id,
                status = status
            )
        }
    }
}