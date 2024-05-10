package com.nrlm.baselinesurvey.model

import java.io.Serializable

data class Tuple4<out A, out B, out C, out D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
) : Serializable {

    override fun toString(): String = "($first, $second, $third, $fourth)"

}

public fun <T> Tuple4<T, T, T, T>.toList(): List<T> = listOf(first, second, third, fourth)

