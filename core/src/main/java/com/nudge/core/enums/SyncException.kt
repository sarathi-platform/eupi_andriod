package com.nudge.core.enums

enum class SyncException(val message: String) {
    RESPONSE_DATA_LIST_IS_EMPTY_EXCEPTION("Response data list is empty"),
    RESPONSE_DATA_IS_NULL_EXCEPTION("Response data is null"),
    RESPONSE_STATUS_FAILED_EXCEPTION("Response status is failed"),
    EXCEPTION_WHILE_FINDING_IMAGE("Exception while finding image"),
    IMAGE_FILE_IS_NOT_EXIST_EXCEPTION("Image file not exist"),
    IMAGE_EMPTY_URI_EXCEPTION("Image URI empty Exception"),
    PRODUCER_RETRY_COUNT_EXCEEDED_EXCEPTION("Producer Retry Count Exceeded Exception"),
    SOMETHING_WENT_WRONG("Something Went Wrong")
}