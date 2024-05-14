package com.nudge.syncmanager.firebase

import com.nudge.core.database.entities.Events

interface FirebaseRepository {

    suspend fun addEventToFirebase(event: Events)

    suspend fun addEventsToFirebase(events: List<Events>)

}