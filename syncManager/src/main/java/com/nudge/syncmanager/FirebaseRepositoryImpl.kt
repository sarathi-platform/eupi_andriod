package com.nudge.syncmanager

import com.nudge.core.Core
import com.nudge.core.EVENTS_BACKUP_COLLECTION
import com.nudge.core.database.entities.Events
import javax.inject.Inject

class FirebaseRepositoryImpl @Inject constructor(
    val core: Core
) : FirebaseRepository {

    override suspend fun addEventToFirebase(event: Events) {
        core.getFirebaseDb()?.collection(EVENTS_BACKUP_COLLECTION)?.document(event.id)?.set(event)
    }

    override suspend fun addEventsToFirebase(events: List<Events>) {
        events.forEach {
            addEventToFirebase(event = it)
        }
    }
}