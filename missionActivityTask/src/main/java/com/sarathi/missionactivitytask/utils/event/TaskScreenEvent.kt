package com.sarathi.missionactivitytask.utils.event

sealed class TaskScreenEvent {

    data class OnFilterSelected(val index: Int) : TaskScreenEvent()

}