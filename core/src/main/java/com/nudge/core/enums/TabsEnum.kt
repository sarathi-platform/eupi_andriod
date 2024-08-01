package com.nudge.core.enums

enum class TabsEnum(val id: Int) {
    ProgressTab(0),
    MissionTab(1),
    DidiCrpTab(3),
    DidiUpcmTab(4),
    DataTab(5)
    ;
}

sealed class SubTabs(val id: Int) {
    object NO_TAB : SubTabs(-1)
    object DidiTab : SubTabs(0)
    object SmallGroupTab : SubTabs(1)
    object All : SubTabs(2)
}
