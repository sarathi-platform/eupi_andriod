package com.sarathi.smallgroupmodule.ui.didiTab.domain.use_case

import com.sarathi.dataloadingmangement.domain.use_case.smallGroup.ShgVerificationUseCase
import com.sarathi.dataloadingmangement.domain.use_case.smallGroup.SubjectEntityUseCase
import com.sarathi.dataloadingmangement.domain.use_case.smallGroup.VerificationEventWriterUseCase

data class DidiVerificationUseCase(
    val subjectEntityUseCase: SubjectEntityUseCase,
    val shgVerificationUseCase: ShgVerificationUseCase,
    val verificationEventWriterUseCase: VerificationEventWriterUseCase
)