package com.nudge.core.model.response

data class SyncEventResponse(val id:String, val status:List<String>,val  consumerPayload:String,val errorMessage:String)
