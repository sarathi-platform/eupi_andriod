import com.sarathi.dataloadingmangement.data.entities.livelihood.SubjectLivelihoodMappingEntity
import com.sarathi.dataloadingmangement.model.uiModel.livelihood.SubjectEntityWithLivelihoodMappingUiModel

fun SubjectEntityWithLivelihoodMappingUiModel.getSubjectLivelihoodMappingEntity(userId: String): SubjectLivelihoodMappingEntity {
    return SubjectLivelihoodMappingEntity(
        userId = userId,
        subjectId = this.subjectId,
        primaryLivelihoodId = this.primaryLivelihoodId,
        secondaryLivelihoodId = this.secondaryLivelihoodId
    )
}