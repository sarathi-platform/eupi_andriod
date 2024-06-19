package com.nudge.core.utils

data class PdfModel(
    val pdfCenterTitle: String,
    val pdfLeftTitle: String,
    val pdfRightTitle: String,
    val tableHeaders: List<String>,
    val rows: List<List<String>>,
    val pageNo: Int,
    val pdfDescription: String

)
