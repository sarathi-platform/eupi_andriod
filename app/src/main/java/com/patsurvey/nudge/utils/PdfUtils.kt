package com.patsurvey.nudge.utils

import android.content.Context
import android.graphics.Typeface
import android.os.Environment
import android.print.PrintAttributes
import android.text.Layout
import android.util.Log
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.VillageEntity
import com.wwdablu.soumya.simplypdf.SimplyPdf
import com.wwdablu.soumya.simplypdf.SimplyPdfDocument
import com.wwdablu.soumya.simplypdf.composers.properties.TableProperties
import com.wwdablu.soumya.simplypdf.composers.properties.TextProperties
import com.wwdablu.soumya.simplypdf.composers.properties.cell.Cell
import com.wwdablu.soumya.simplypdf.composers.properties.cell.TextCell
import com.wwdablu.soumya.simplypdf.document.DocumentInfo
import com.wwdablu.soumya.simplypdf.document.Margin
import java.io.File
import java.util.LinkedList

object PdfUtils {

    private const val SR_NO_HEADING_TEXT = "S. No.\n" + "(1)"
    private const val HOUSE_NO_HEADING_TEXT = "HH No.\n" + "(2)"
    private const val DIDI_NAME_HEADING_TEXT = "Name of the Didi\n" + "(3)"
    private const val GUARDIAN_NAME_HEADING_TEXT = "Name of Husband/ Father\n" + "(4)"
    private const val TOLA_NAME_HEADING_TEXT = "Name of \n" + "the Hamlet/Tola/Para\n" + "(5)"

    private val margins: UInt = 20u
    private val marginBottom: UInt = 20u
    private const val serialNumberCellWidth = 50
    private const val dataCellWidth = 125
    private val titleTextProperties = getTitleTextProperties()
    private val subTitleTextProperties = getSubTitleTextProperties()
    private val contentTextProperties = getContentTextProperties()
    private val cellDataTextProperties = getTextPropertiesForCell()

    suspend fun getFormAPdf(context: Context, villageEntity: VillageEntity, didiDetailList: List<DidiEntity>, completionDate: String): Boolean{

        val mSerialNumberCellWidth = 50
        val mDataCellWidth = 150

        val simplyPdfDocument = getSimplePdfDocument(context, villageEntity, FORM_A_PDF_NAME)

        simplyPdfDocument.text.write("Digital Form A", titleTextProperties)
        simplyPdfDocument.text.write("List of households categorized as Poor in Participatory Wealth Ranking", subTitleTextProperties)

        simplyPdfDocument.text.write("Village Name: ${villageEntity.name} \t VO Name: ${villageEntity.federationName}",
            contentTextProperties)
        simplyPdfDocument.text.write("Date of CRP Drive: $completionDate",
            contentTextProperties)

        simplyPdfDocument.insertEmptyLines(1)

        val headerRow = LinkedList<Cell>().apply {
            add(
                TextCell(SR_NO_HEADING_TEXT, cellDataTextProperties, mSerialNumberCellWidth)
            )
            add(
                TextCell(HOUSE_NO_HEADING_TEXT, cellDataTextProperties, mDataCellWidth)
            )
            add(
                TextCell(DIDI_NAME_HEADING_TEXT, cellDataTextProperties, mDataCellWidth)
            )
            add(
                TextCell(GUARDIAN_NAME_HEADING_TEXT, cellDataTextProperties, mDataCellWidth)
            )
            add(
                TextCell(TOLA_NAME_HEADING_TEXT, cellDataTextProperties, mDataCellWidth)
            )
            add(
                TextCell("Social Category\n" +
                    "(6)", cellDataTextProperties, mDataCellWidth)
            )
        }

        val rows = LinkedList<LinkedList<Cell>>().apply {
            add(headerRow)

            didiDetailList.filter { it.wealth_ranking == WealthRank.POOR.rank }.forEachIndexed { index, didiEntity ->
                add(
                    LinkedList<Cell>().apply {
                        add(TextCell("${index + 1}", cellDataTextProperties, mSerialNumberCellWidth))
                        add(
                            TextCell("${didiEntity.address}", cellDataTextProperties, mDataCellWidth)
                        )
                        add(
                            TextCell("${didiEntity.name}", cellDataTextProperties, mDataCellWidth)
                        )
                        add(
                            TextCell("${didiEntity.guardianName}", cellDataTextProperties, mDataCellWidth)
                        )
                        add(
                            TextCell("${didiEntity.cohortName}", cellDataTextProperties, mDataCellWidth)
                        )
                        add(
                            TextCell("${didiEntity.castName}", cellDataTextProperties, mDataCellWidth))
                    }
                )
            }

        }

        simplyPdfDocument.table.draw(rows, TableProperties().apply {
            borderColor = "#000000"
            borderWidth = 1
            drawBorder = true
        })

        simplyPdfDocument.insertEmptyLines(1)

        simplyPdfDocument.text.write("Findings:", TextProperties().apply {
            textSize = 8
            textColor = "#000000"
            typeface = Typeface.DEFAULT_BOLD
            alignment = Layout.Alignment.ALIGN_NORMAL

        })

        simplyPdfDocument.text.write("Total no. of families in the village: ${didiDetailList.size}",
            TextProperties().apply {
            textSize = 8
            textColor = "#000000"
            typeface = Typeface.DEFAULT
            bulletSymbol = "1"
            alignment = Layout.Alignment.ALIGN_NORMAL

        })

        simplyPdfDocument.text.write("Total no. of families categorized as Poor: ${didiDetailList.filter { it.wealth_ranking == WealthRank.POOR.rank }.size}",
            TextProperties().apply {
            textSize = 8
            textColor = "#000000"
            typeface = Typeface.DEFAULT
            bulletSymbol = "2"
            alignment = Layout.Alignment.ALIGN_NORMAL

        })

        simplyPdfDocument.text.write("Total no. of families categorized as Medium: ${didiDetailList.filter { it.wealth_ranking == WealthRank.MEDIUM.rank }.size}",
            TextProperties().apply {
            textSize = 8
            textColor = "#000000"
            typeface = Typeface.DEFAULT
            bulletSymbol = "3"
            alignment = Layout.Alignment.ALIGN_NORMAL

        })

        simplyPdfDocument.text.write("Total no. of families categorized as Rich: ${didiDetailList.filter { it.wealth_ranking == WealthRank.RICH.rank }.size}",
            TextProperties().apply {
            textSize = 8
            textColor = "#000000"
            typeface = Typeface.DEFAULT
            bulletSymbol = "4"
            alignment = Layout.Alignment.ALIGN_NORMAL

        })

        val success = simplyPdfDocument.finish()
        return success
    }



    suspend fun getFormBPdf(context: Context, villageEntity: VillageEntity, didiDetailList: List<DidiEntity>, completionDate: String): Boolean {

        val simplyPdfDocument = getSimplePdfDocument(context, villageEntity, FORM_B_PDF_NAME)

        simplyPdfDocument.text.write("Digital Form B", titleTextProperties)

        simplyPdfDocument.text.write("Families tentatively selected as Ultra-Poor after mobile-based PAT Survey", subTitleTextProperties)

        simplyPdfDocument.text.write("Village Name: ${villageEntity.name} \t VO Name: ${villageEntity.federationName}", contentTextProperties)

        simplyPdfDocument.text.write("Date of CRP Drive: $completionDate", contentTextProperties)

        simplyPdfDocument.text.write("Total no. of Ultra-Poor families selected by CRP: ${didiDetailList.size}", contentTextProperties)

        simplyPdfDocument.insertEmptyLines(1)

        val headerRow = LinkedList<Cell>().apply {
            add(TextCell(SR_NO_HEADING_TEXT, cellDataTextProperties, serialNumberCellWidth))
            add(TextCell(HOUSE_NO_HEADING_TEXT, cellDataTextProperties, dataCellWidth))
            add(
                TextCell(DIDI_NAME_HEADING_TEXT, cellDataTextProperties, dataCellWidth)
            )
            add(
                TextCell(GUARDIAN_NAME_HEADING_TEXT, cellDataTextProperties, dataCellWidth)
            )
            add(
                TextCell(TOLA_NAME_HEADING_TEXT, cellDataTextProperties, dataCellWidth)
            )
            add(
                TextCell("Caste (ST/PVTG/SC/OBC/General)\n" +
                        "(6)", cellDataTextProperties, dataCellWidth)
            )
            add(
                TextCell("SHG member (Yes/ No)\n" +
                        "(7)", cellDataTextProperties, dataCellWidth)
            )
        }

        val rows = LinkedList<LinkedList<Cell>>().apply {
            add(headerRow)

            didiDetailList.forEachIndexed { index, didiEntity ->
                add(
                    LinkedList<Cell>().apply {
                        add(TextCell("${index + 1}", cellDataTextProperties, serialNumberCellWidth))
                        add(
                            TextCell(didiEntity.address, cellDataTextProperties, dataCellWidth)
                        )
                        add(
                            TextCell(didiEntity.name, cellDataTextProperties, dataCellWidth)
                        )
                        add(
                            TextCell(didiEntity.guardianName, cellDataTextProperties, dataCellWidth)
                        )
                        add(
                            TextCell(didiEntity.cohortName, cellDataTextProperties, dataCellWidth)
                        )
                        add(
                            TextCell(didiEntity.castName, cellDataTextProperties, dataCellWidth))
                        add(
                            TextCell(SHGFlag.fromInt(didiEntity.shgFlag).toString(), cellDataTextProperties, dataCellWidth))
                    }
                )
            }
        }

        simplyPdfDocument.table.draw(rows, TableProperties().apply {
            borderColor = "#000000"
            borderWidth = 1
            drawBorder = true
        })

        simplyPdfDocument.insertEmptyLines(1)

        val success = simplyPdfDocument.finish()
        return success
    }

    suspend fun getFormCPdf(context: Context, villageEntity: VillageEntity, didiDetailList: List<DidiEntity>, completionDate: String): Boolean {

        val simplyPdfDocument = getSimplePdfDocument(context, villageEntity, FORM_C_PDF_NAME)

        simplyPdfDocument.text.write("Digital Form C", titleTextProperties)

        simplyPdfDocument.text.write("Final list of Ultra Poor families endorsed by VO (Village Organization)", subTitleTextProperties)

        simplyPdfDocument.text.write("Village Name: ${villageEntity.name} \t VO Name: ${villageEntity.federationName}", contentTextProperties)

        simplyPdfDocument.text.write("Date of CRP Drive: $completionDate", contentTextProperties)

        simplyPdfDocument.text.write("Total no. Ultra-Poor families endorsed by VO: ${didiDetailList.size}", contentTextProperties)

        simplyPdfDocument.insertEmptyLines(1)

        val headerRow = LinkedList<Cell>().apply {
            add(TextCell(SR_NO_HEADING_TEXT, cellDataTextProperties, serialNumberCellWidth))
            add(TextCell(HOUSE_NO_HEADING_TEXT, cellDataTextProperties, dataCellWidth))
            add(
                TextCell(DIDI_NAME_HEADING_TEXT, cellDataTextProperties, dataCellWidth)
            )
            add(
                TextCell(GUARDIAN_NAME_HEADING_TEXT, cellDataTextProperties, dataCellWidth)
            )
            add(
                TextCell(TOLA_NAME_HEADING_TEXT, cellDataTextProperties, dataCellWidth)
            )
            add(
                TextCell("Caste (ST/PVTG/SC/OBC/General)\n" +
                        "(6)", cellDataTextProperties, dataCellWidth)
            )
            add(
                TextCell("SHG member (Yes/ No)\n" +
                        "(7)", cellDataTextProperties, dataCellWidth)
            )
        }

        val rows = LinkedList<LinkedList<Cell>>().apply {
            add(headerRow)

            didiDetailList.forEachIndexed { index, didiEntity ->
                add(
                    LinkedList<Cell>().apply {
                        add(TextCell("${index + 1}", cellDataTextProperties, serialNumberCellWidth))
                        add(
                            TextCell(didiEntity.address, cellDataTextProperties, dataCellWidth)
                        )
                        add(
                            TextCell(didiEntity.name, cellDataTextProperties, dataCellWidth)
                        )
                        add(
                            TextCell(didiEntity.guardianName, cellDataTextProperties, dataCellWidth)
                        )
                        add(
                            TextCell(didiEntity.cohortName, cellDataTextProperties, dataCellWidth)
                        )
                        add(
                            TextCell(didiEntity.castName, cellDataTextProperties, dataCellWidth))
                        add(
                            TextCell(SHGFlag.fromInt(didiEntity.shgFlag).toString(), cellDataTextProperties, dataCellWidth))
                    }
                )
            }
        }

        simplyPdfDocument.table.draw(rows, TableProperties().apply {
            borderColor = "#000000"
            borderWidth = 1
            drawBorder = true
        })

        simplyPdfDocument.insertEmptyLines(1)

        val success = simplyPdfDocument.finish()
        return success

    }

    private fun getSimplePdfDocument(context: Context, villageEntity: VillageEntity, fileName: String): SimplyPdfDocument {
        return SimplyPdf.with(context, getPdfPath(context = context, formName = fileName, villageEntity.name))
            .colorMode(DocumentInfo.ColorMode.COLOR)
            .paperSize(PrintAttributes.MediaSize.ISO_A4)
            .margin(Margin(margins, margins, margins, marginBottom))
            .paperOrientation(DocumentInfo.Orientation.LANDSCAPE)
            .build()
    }

    fun getPdfPath(context: Context, formName: String, villageName: String): File {
        return File("${context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)?.absolutePath}/${formName}_${villageName}.pdf")
    }

    private fun getTextPropertiesForCell(): TextProperties {
        return TextProperties().apply {
            textSize = 8
            textColor = "#000000"
            typeface = Typeface.DEFAULT
            alignment = Layout.Alignment.ALIGN_CENTER
        }
    }

    private fun getTitleTextProperties(): TextProperties {
        return TextProperties().apply {
            textSize = 13
            textColor = "#000000"
            typeface = Typeface.DEFAULT_BOLD
            alignment = Layout.Alignment.ALIGN_CENTER

        }
    }

    private fun getSubTitleTextProperties(): TextProperties {
        return TextProperties().apply {
            textSize = 10
            textColor = "#000000"
            typeface = Typeface.DEFAULT
            alignment = Layout.Alignment.ALIGN_CENTER

        }
    }

    private fun getContentTextProperties(): TextProperties {
        return TextProperties().apply {
            textSize = 8
            textColor = "#000000"
            typeface = Typeface.DEFAULT
            alignment = Layout.Alignment.ALIGN_NORMAL

        }
    }
}