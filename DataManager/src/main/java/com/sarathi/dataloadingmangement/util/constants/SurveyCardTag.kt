package com.sarathi.dataloadingmangement.util.constants

import com.sarathi.dataloadingmangement.DATE_TAG
import com.sarathi.dataloadingmangement.DISBURSED_AMOUNT_TAG
import com.sarathi.dataloadingmangement.DISBURSEMENT_DATE_TAG
import com.sarathi.dataloadingmangement.MODE_TAG
import com.sarathi.dataloadingmangement.NATURE_TAG
import com.sarathi.dataloadingmangement.NO_OF_POOR_DIDI_TAG
import com.sarathi.dataloadingmangement.RECEIVED_AMOUNT_TAG


enum class SurveyCardTag(val tag: Int) {
    SURVEY_TAG__RECEIEVED_AMOUNT(tag = RECEIVED_AMOUNT_TAG),
    SURVEY_TAG_DISBURSED_AMOUNT(tag = DISBURSED_AMOUNT_TAG),
    SURVEY_TAG_DATE(tag = DATE_TAG),
    SURVEY_DISBURSEMENT_TAG_DATE(tag = DISBURSEMENT_DATE_TAG),
    SURVEY_TAG_MODE(tag = MODE_TAG),
    SURVEY_TAG_NATURE(tag = NATURE_TAG),
    SURVEY_TAG_NO_OF_DIDI(tag = NO_OF_POOR_DIDI_TAG)
}
