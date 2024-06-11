package com.sarathi.smallgroupmodule.ui.smallGroupAttendance.domain.repository

interface FetchMarkedDatesRepository {

    suspend fun fetchMarkedDates(subjectIds: List<Int>): List<Long>

}
