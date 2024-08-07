package com.nudge.core.model.uiModel

data class LivelihoodModel(
    var livelihoodId: Int,
    var name: String,
    var status: Int,
) {

    companion object {
        fun getAllFilter(): LivelihoodModel {
            return LivelihoodModel(livelihoodId = 0, "All", status = 1)
        }
    }

}