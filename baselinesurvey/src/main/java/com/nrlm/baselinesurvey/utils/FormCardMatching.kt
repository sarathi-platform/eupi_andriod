package com.nrlm.baselinesurvey.utils

import android.util.Log
import com.nrlm.baselinesurvey.BLANK_STRING

enum class FormCardMatching(val title: String = "", val alternatives: List<String> = emptyList()) {

    NAME("Name", listOf("Name", "নাম")),
    RELATIONSHIP("Relationship", listOf("Relationship", "সম্পর্ক")),
    AGE("Age", listOf("Age", "বয়স")),
    INCOME_SOURCE("sources of income", listOf("sources of income", "আয়ের উৎস")),
    AGRICULTURE_PRODUCE(
        "(A) Agricultural produce - ",
        listOf("(A) Agricultural produce - ", "(ক) কৃষিপণ্য- ")
    ),
    LIVESTOCK(
        "(A) Type of livestock - ",
        listOf("(A) Type of livestock - ", "(ক) গবাদি পশুর ধরন - ")
    ),
    INCOME_FREQUENCY("Income frequency", listOf("Income frequency", "আয় ফ্রিকোয়েন্সি")),
    NO_INCOME("No income", listOf("No income", "কোন আয়")),
    TOTAL_INCOME("(E) Total Income", listOf("(E) Total Income", "(ঙ) মোট আয়")),
    TOTAL_UNITS("(D) Total units", listOf("(D) Total units", "(D) মোট একক")),
    INCOME("Income", listOf("Income", "আয়")),
    SMALL_BUSINESS_INCOME(
        "What is the household income from small business in the last 12 months?",
        listOf(
            "What is the household income from small business in the last 12 months?",
            "গত 12 মাসে ছোট ব্যবসা থেকে পরিবারের আয় কত?"
        )
    );


    companion object {
        fun checkCardCondition(condition: String?, matchEqual: Boolean = false): Boolean {
            values().forEach {
                if (matchEqual) {
                    if (condition?.contains(it.title, true) == true)
                        return true
                    it.alternatives.forEach { alternative ->
                        if (condition.equals(alternative, true))
                            return true
                    }
                } else {
                    if (condition?.contains(it.title, true) == true)
                        return true
                    it.alternatives.forEach { alternative ->
                        if (condition?.contains(alternative, true) == true)
                            return true
                    }
                }
            }
            return false
        }

        /*fun getStringToCompare(display: String?, matchingKey: FormCardMatching, matchEqual: Boolean = false): String {
            values().forEach {
                it.alternatives.forEach { alternative ->
                    if (matchEqual) {
                        if (display.equals(alternative, true) && matchingKey.alternatives.contains(alternative))
                            return alternative
                    } else {
                        Log.d("TAG",
                            "getStringToCompare: " +
                                    "display: $display, alternative: $alternative, matchingKey.title: ${matchingKey.title}")
                        if (display?.contains(alternative, true) == true && matchingKey.alternatives.contains(alternative)) {
                            Log.d("TAG",
                                "getStringToCompare: " +
                                        "display?.contains(alternative, true) == true: ${
                                            display?.contains(
                                                alternative,
                                                true
                                            ) == true
                                        } &&  matchingKey.alternatives.contains(alternative): ${matchingKey.alternatives.contains(alternative)}")
                            return alternative
                        }
                    }
                }
            }
            return BLANK_STRING
        }*/

        fun getStringToCompare(
            display: String?,
            matchingKey: FormCardMatching,
            matchEqual: Boolean = false
        ): String {

            matchingKey.alternatives.forEach { alternative ->
                Log.d(
                    "TAG",
                    "getStringToCompare: " +
                            "display: $display, alternative: $alternative, matchingKey.title: ${matchingKey.title}"
                )
                if (matchEqual) {
                    Log.d(
                        "TAG", "getStringToCompare: getStringToCompare: " +
                                "alternative.equals(display, true): ${
                                    alternative.equals(
                                        display ?: BLANK_STRING,
                                        true
                                    )
                                }"
                    )
                    if (alternative.equals(display ?: BLANK_STRING, true)) {
                        return alternative
                    }
                } else {
                    Log.d(
                        "TAG", "getStringToCompare: getStringToCompare: " +
                                "alternative.contains(display, true): ${
                                    alternative.contains(
                                        display ?: BLANK_STRING,
                                        true
                                    )
                                }"
                    )
                    if (alternative.contains(display ?: BLANK_STRING, true)) {
                        return alternative
                    }
                }
            }

            return BLANK_STRING
        }

        fun getMatchingCardForText(text: String?): FormCardMatching? {
            values().forEach {
                it.alternatives.forEach { alternative ->
                    if (text?.contains(alternative, true) == true)
                        return it
                }
            }
            return null
        }
    }


}