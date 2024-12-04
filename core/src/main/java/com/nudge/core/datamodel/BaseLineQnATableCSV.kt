package com.nudge.core.datamodel

import com.nudge.core.BLANK_STRING
import com.nudge.core.exportcsv.Exportable
import com.opencsv.bean.CsvBindByName

data class BaseLineQnATableCSV(
var id: String? = BLANK_STRING,

@CsvBindByName(column = "Didi Name")
var didiName : String? = BLANK_STRING,

@CsvBindByName(column = "Husband/Father Name")
var dadaName : String? = BLANK_STRING,

@CsvBindByName(column = "Didi-House-No")
var house : String? = BLANK_STRING,

@CsvBindByName(column = "Question")
var question : String? = BLANK_STRING,

@CsvBindByName(column = "Question-Sub")
var subQuestion : String? = BLANK_STRING,

@CsvBindByName(column = "Response")
var response : String? = BLANK_STRING,

@CsvBindByName(column = "Section")
var section : String? = BLANK_STRING,

@CsvBindByName(column = "Village-Name")
var villageName : String? = BLANK_STRING,

@CsvBindByName(column = "Tola Name")
var cohortName: String? = BLANK_STRING,

var surveyId: Int? = 0,

@CsvBindByName(column = "Subject Id")
var subjectId : Int? = 0,

var sectionId: Int? = 0,
var orderId: Int? = 0,
var sortKey: Int? = 0,
var formOder: Int? = 0,
var referenceId: String? = BLANK_STRING

) : Exportable

data class HamletQnATableCSV(
    var id: String? = BLANK_STRING,

    @CsvBindByName(column = "Question")
    var question : String? = BLANK_STRING,

    @CsvBindByName(column = "Question-Sub")
    var subQuestion : String? = BLANK_STRING,

    @CsvBindByName(column = "Response")
    var response : String? = BLANK_STRING,

    @CsvBindByName(column = "Section")
    var section : String? = BLANK_STRING,

    @CsvBindByName(column = "Village-Name")
    var villageName : String? = BLANK_STRING,

    @CsvBindByName(column = "Hamlet Name")
    var cohoretName : String? = BLANK_STRING,

    var surveyId: Int? = 0,

    @CsvBindByName(column = "Subject Id")
    var subjectId : Int? = 0,

    var sectionId: Int? = 0,
    var orderId: Int? = 0,
    var formOrder: Int? = 0,
    var referenceId: String? = BLANK_STRING

    ) : Exportable

