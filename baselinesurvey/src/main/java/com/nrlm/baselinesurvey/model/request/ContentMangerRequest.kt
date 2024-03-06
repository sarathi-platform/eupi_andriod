package com.nrlm.baselinesurvey.model.request

data class ContentMangerRequest(val languageCode: String, val contentKeys: List<String?>) {
}