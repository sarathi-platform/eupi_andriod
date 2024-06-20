package com.sarathi.dataloadingmangement.repository

import com.sarathi.dataloadingmangement.data.entities.FormEntity
import com.sarathi.dataloadingmangement.model.events.SaveFormAnswerEventDto

interface IFormEventRepository {

    fun getSaveFormAnswerEventDto(formEntity: FormEntity): SaveFormAnswerEventDto

}