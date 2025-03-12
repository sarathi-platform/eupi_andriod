package com.nudge.core.enums

enum class RemoteQueryConfigExecutionLevelEnum(val executionPriority: Int) {
    USER(1),
    GLOBAL_STATE(2),
    GLOBAL(3);

    companion object {

        fun getExecutionPriorityForLevel(level: String): Int {
            return RemoteQueryConfigExecutionLevelEnum.values()
                .find { it.name.equals(level, true) }?.executionPriority ?: USER.executionPriority
        }

    }
}