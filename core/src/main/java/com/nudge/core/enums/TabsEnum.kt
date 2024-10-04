package com.nudge.core.enums

enum class TabsEnum(val id: Int, val tabIndex: Int) {
    ProgressTab(0, 0),
    MissionTab(1, 0),
    DidiCrpTab(3, 1),
    DidiUpcmTab(4, 2),
    DataTab(5, 1),
    DataSummaryTab(6, 3),
    SettingsTab(7, 0)

}

sealed class SubTabs(val id: Int) {
    object NO_TAB : SubTabs(-1)
    object DidiTab : SubTabs(0)
    object SmallGroupTab : SubTabs(1)
    object All : SubTabs(2)
    object NoEntryMonthTab : SubTabs(3)
    object NoEntryWeekTab : SubTabs(4)
    object LastWeekTab : SubTabs(5)
    object LastMonthTab : SubTabs(6)
    object Last3MonthsTab : SubTabs(7)
    object CustomDateRange : SubTabs(8)
    object Step1 : SubTabs(9)
    object Step2 : SubTabs(10)


}
