package com.nudge.core.enums

enum class TabsEnum(val id: Int, val tabIndex: Int) {
    ProgressTab(0, 0),
    MissionTab(1, 0),
    DidiCrpTab(3, 1),
    DidiUpcmTab(4, 2),
    DataTab(5, 1)
    ;
}

sealed class SubTabs(val id: Int) {
    object NO_TAB : SubTabs(-1)
    object DidiTab : SubTabs(0)
    object SmallGroupTab : SubTabs(1)
    object All : SubTabs(2)
    object NoEntryMonthTab : SubTabs(3)
    object NoEntryWeekTab : SubTabs(4)


}
