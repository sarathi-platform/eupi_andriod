package com.nudge.core.datamodel

import com.nudge.core.BLANK_STRING
import com.nudge.core.exportcsv.Exportable
import com.opencsv.bean.CsvBindByName

data class BaseLineQnATableCSV(
@CsvBindByName(column = "id")
var id: Int? = 0,

@CsvBindByName(column = "question")
var question : String? = BLANK_STRING,

@CsvBindByName(column = "response")
var response : String? = BLANK_STRING,

@CsvBindByName(column = "section")
var section : String? = BLANK_STRING,

@CsvBindByName(column = "subjectId")
var subjectId : Int = 0,

) : Exportable

