package com.nudge.core.enums

import com.nudge.core.BLANK_STRING
import com.nudge.core.value

enum class ValueTypes(val dataType: String) {

    INT("Int"),
    LONG("Long"),
    FLOAT("Float"),
    DOUBLE("Double"),
    STRING("String"),
    BOOLEAN("Boolean")
    ;

    companion object {

        fun String?.convertToDataType(valueTypes: String): Any {

            return when (valueTypes.getValueTypesFromString()) {
                INT -> {
                    val value = this.value()
                    if (value == BLANK_STRING) {
                        0
                    } else {
                        value.toInt()
                    }
                }

                LONG -> {
                    val value = this.value()
                    if (value == BLANK_STRING) {
                        0.toLong()
                    } else {
                        value.toLong()
                    }
                }

                FLOAT -> {
                    val value = this.value()
                    if (value == BLANK_STRING) {
                        0.0
                    } else {
                        value.toFloat()
                    }
                }

                DOUBLE -> {
                    val value = this.value()
                    if (value == BLANK_STRING) {
                        0.0
                    } else {
                        value.toDouble()
                    }
                }

                STRING -> {
                    this.value()
                }

                else -> this.value()
            }

        }


        fun String.getValueTypesFromString(): ValueTypes {
            return when (this) {
                "Int" -> INT
                "Long" -> LONG
                "Float" -> FLOAT
                "Double" -> DOUBLE
                "String" -> STRING
                else -> STRING
            }
        }

    }

}