package com.nudge.core.utils

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Environment
import android.print.PrintAttributes
import android.text.Layout
import com.nudge.core.model.CoreAppDetails
import com.wwdablu.soumya.simplypdf.SimplyPdf
import com.wwdablu.soumya.simplypdf.SimplyPdfDocument
import com.wwdablu.soumya.simplypdf.composers.properties.TableProperties
import com.wwdablu.soumya.simplypdf.composers.properties.TextProperties
import com.wwdablu.soumya.simplypdf.composers.properties.cell.Cell
import com.wwdablu.soumya.simplypdf.composers.properties.cell.TextCell
import com.wwdablu.soumya.simplypdf.document.DocumentInfo
import com.wwdablu.soumya.simplypdf.document.Margin
import com.wwdablu.soumya.simplypdf.document.PageModifier
import java.io.File
import java.util.LinkedList

object PdfGenerator {

    private val margins: UInt = 20u
    private val marginBottom: UInt = 40u
    private val blackColor = "#000000"
    private val greyColor = "#222E50"
    suspend fun generatePdf(pdfModels: List<PdfModel>): String {
        val mSerialNumberCellWidth = 50
        val mDataCellWidth = 150
        val outputFile = getPdfPath(
            CoreAppDetails.getApplicationDetails()?.activity?.applicationContext!!,
            "Digital Form e"
        )
        val simplyPdfDocument = getSimplePdfDocument(
            CoreAppDetails.getApplicationDetails()?.activity?.applicationContext!!,
            outputFile
        )

        pdfModels.forEachIndexed { index, pdfModel ->
            simplyPdfDocument.text.write(
                text = pdfModel.pdfCenterTitle,
                properties = getTitleTextProperties(Layout.Alignment.ALIGN_CENTER)
            )
            simplyPdfDocument.text.write(
                text = pdfModel.pdfLeftTitle,
                properties = getTitleTextProperties(Layout.Alignment.ALIGN_NORMAL)
            )

            simplyPdfDocument.text.write(
                text = pdfModel.pdfRightTitle,
                properties = getTitleTextProperties(Layout.Alignment.ALIGN_NORMAL)
            )
            simplyPdfDocument.insertEmptyLines(1)

            simplyPdfDocument.text.write(
                text = pdfModel.pdfDescription,
                properties = getSubTitleTextProperties()
            )

            simplyPdfDocument.insertEmptyLines(1)
            val headerRow = LinkedList<Cell>()
            val dataRow = LinkedList<LinkedList<Cell>>()
            pdfModel.tableHeaders.forEachIndexed() { index, header ->

                headerRow.add(
                    TextCell(
                        text = header,
                        getTextPropertiesForCell(),
                        width = if (index == 0) mSerialNumberCellWidth else mDataCellWidth
                    )
                )

            }

            dataRow.add(headerRow)
            pdfModel.rows.forEach { row ->


                dataRow.add(LinkedList<Cell>().apply {
                    row.forEachIndexed { index, text ->
                        add(
                            TextCell(
                                text = text,
                                properties = getTextPropertiesForCell(),
                                width = if (index == 0) mSerialNumberCellWidth else mDataCellWidth
                            )
                        )

                    }


                })


            }

            simplyPdfDocument.table.draw(dataRow, TableProperties().apply {
                borderColor = blackColor
                borderWidth = 1
                drawBorder = true
            })
            simplyPdfDocument.insertEmptyLines(1)
            if (index < pdfModels.size - 1) {
                simplyPdfDocument.newPage()
            }

        }
        simplyPdfDocument.finish()

        return outputFile.path

    }


    private fun getSimplePdfDocument(
        context: Context,
        outputFile: File,
    ): SimplyPdfDocument {
        return SimplyPdf.with(
            context, outputFile
        )
            .colorMode(DocumentInfo.ColorMode.COLOR)
            .paperSize(PrintAttributes.MediaSize.ISO_A4)
            .margin(Margin(margins, margins, margins, marginBottom))
            .paperOrientation(DocumentInfo.Orientation.LANDSCAPE)
            .pageModifier(PageNumberModifier())
            .build()
    }

    class PageNumberModifier() : PageModifier() {
        override fun render(simplyPdfDocument: SimplyPdfDocument) {
            simplyPdfDocument.apply {
                val page = currentPageNumber + 1
                currentPage.canvas.drawText("Page $page",
                    usablePageWidth / 2.toFloat(),
                    (usablePageHeight + marginBottom.toFloat()),
                    Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        color = Color.BLACK
                    })
            }
        }
    }

    fun getPdfPath(context: Context, formName: String): File {
        return File("${context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)?.absolutePath}/${formName}.pdf")
    }

    private fun getTextPropertiesForCell(): TextProperties {
        return TextProperties().apply {
            textSize = 12
            textColor = blackColor
            typeface = Typeface.DEFAULT
            alignment = Layout.Alignment.ALIGN_CENTER
        }
    }

    private fun getTitleTextProperties(textAlignment: Layout.Alignment): TextProperties {
        return TextProperties().apply {
            textSize = 13
            textColor = blackColor
            typeface = Typeface.DEFAULT_BOLD
            alignment = textAlignment

        }
    }

    private fun getSubTitleTextProperties(): TextProperties {
        return TextProperties().apply {
            textSize = 14
            textColor = greyColor
            typeface = Typeface.DEFAULT
            alignment = Layout.Alignment.ALIGN_CENTER

        }
    }

}