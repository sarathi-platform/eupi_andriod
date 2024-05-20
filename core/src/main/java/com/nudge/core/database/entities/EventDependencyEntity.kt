package com.nudge.core.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.nudge.core.EventDependencyTable
import com.nudge.core.database.converters.ListConvertor

@Entity(tableName = EventDependencyTable, indices = [Index(value = ["id", "dependent_event_id"], unique = true)])
data class EventDependencyEntity(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo("id")
    val event_id: String,

    @ColumnInfo("dependent_event_id")
//    @TypeConverters(ListConvertor::class)
    val dependent_event_id: String

)


fun List<EventDependencyEntity>.getDependentEventsId(): List<String> {
    val dependentEventsId = mutableSetOf<String>()
    this.forEach { eventDependencyEntity ->
        dependentEventsId.add(eventDependencyEntity.dependent_event_id)
    }
    return dependentEventsId.toList()
}