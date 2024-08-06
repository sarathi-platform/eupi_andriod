package com.nudge.core.model.uiModel

data class LivelihoodModel(
    var id: Int,
    var name: String,
    var status: Int,
) {

    companion object {
        fun getAllFilter(): LivelihoodModel {
            return LivelihoodModel(id = 0, "All", status = 1)
        }
    }

}