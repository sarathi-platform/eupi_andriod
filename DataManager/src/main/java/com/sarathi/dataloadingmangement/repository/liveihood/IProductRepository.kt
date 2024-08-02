package com.sarathi.dataloadingmangement.repository.liveihood

import com.sarathi.dataloadingmangement.model.response.Product

interface IProductRepository {
    //suspend fun fetchLivelihoodFromServer(mainLivelihood: LivelihoodResponse): ApiResponseModel<LivelihoodResponse>
    suspend fun saveProductEntityToDB(product: Product)
}