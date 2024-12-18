package com.nudge.core

sealed class SearchTab() {
    object ALL_TAB : SearchTab()
    object QUESTION_DATA_TAB : SearchTab()
    object SECTION_INFORMATION_TAB : SearchTab()
}