package com.sarathi.dataloadingmangement.network.response

data class ContentResponse(
    val contentId: Int,
    val contentKey: String,
    val contentType: String,
    val contentValue: String,
    val contentName: String,
    val languageCode: Int
) {
}
