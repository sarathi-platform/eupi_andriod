package com.patsurvey.nudge.activities.ui.digital_forms

import android.content.Context
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.print.PrintAttributes
import android.text.Layout
import androidx.compose.ui.text.font.Typeface
import androidx.lifecycle.viewModelScope
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.dao.DidiDao
import com.patsurvey.nudge.model.dataModel.DidiDetailsModel
import com.wwdablu.soumya.simplypdf.SimplyPdf
import com.wwdablu.soumya.simplypdf.composers.properties.TableProperties
import com.wwdablu.soumya.simplypdf.composers.properties.TextProperties
import com.wwdablu.soumya.simplypdf.composers.properties.cell.Cell
import com.wwdablu.soumya.simplypdf.composers.properties.cell.TextCell
import com.wwdablu.soumya.simplypdf.document.DocumentInfo
import com.wwdablu.soumya.simplypdf.document.Margin
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.LinkedList
import javax.inject.Inject

@HiltViewModel
class DigitalFormAViewModel @Inject constructor(
    val prefRepo: PrefRepo,
    val didiDao: DidiDao,
):BaseViewModel()  {
    private val _didiDetailList = MutableStateFlow(listOf<DidiEntity>())
    val didiDetailList: StateFlow<List<DidiEntity>> get() = _didiDetailList
    var villageId: Int = -1
    init {
        villageId = prefRepo.getSelectedVillage().id
        job= CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            withContext(Dispatchers.IO){
                _didiDetailList.emit(didiDao.getAllPoorDidisForVillage(villageId))
            }
        }
    }

    suspend fun pdf(context: Context){
        val margins:Int = 16
        val simplyPdfDocument = SimplyPdf.with(context, File("${Environment.getExternalStorageDirectory().absoluteFile}/Digital Form A.pdf"))
            .colorMode(DocumentInfo.ColorMode.COLOR)
            .paperSize(PrintAttributes.MediaSize.ISO_A4)
            .margin(Margin(margins.toUInt(), margins.toUInt(), margins.toUInt(), margins.toUInt()))
            .paperOrientation(DocumentInfo.Orientation.PORTRAIT)
            .build()

        simplyPdfDocument.text.write("Digital Form A", TextProperties().apply {
            textSize = 13
            textColor = "#000000"
            typeface = android.graphics.Typeface.DEFAULT_BOLD
            alignment = Layout.Alignment.ALIGN_CENTER

        })
        simplyPdfDocument.text.write("List of households categorized into Rich, Medium, and Poor in Participatory Wealth Ranking",
            TextProperties().apply {
                textSize = 10
                textColor = "#000000"
                typeface = android.graphics.Typeface.DEFAULT
                alignment = Layout.Alignment.ALIGN_CENTER

            })

        val headerRow = LinkedList<Cell>().apply {
                add(TextCell("S. No.\n" +
                        "(1)", TextProperties().apply {
                    textSize = 10
                    textColor = "#000000"
                    typeface = android.graphics.Typeface.DEFAULT
                    alignment = Layout.Alignment.ALIGN_CENTER
                }, Cell.MATCH_PARENT))
                add(TextCell("HH No.\n" +
                        "(2)", TextProperties().apply {
                    textSize = 10
                    textColor = "#000000"
                    typeface = android.graphics.Typeface.DEFAULT
                    alignment = Layout.Alignment.ALIGN_CENTER
                }, Cell.MATCH_PARENT))
                add(TextCell("Name of the Didi\n" +
                        "(3)", TextProperties().apply {
                    textSize = 10
                    textColor = "#000000"
                    typeface = android.graphics.Typeface.DEFAULT
                    alignment = Layout.Alignment.ALIGN_CENTER
                }, Cell.MATCH_PARENT))
                add(TextCell("Name of Husband/ Father\n" +
                        "(4)", TextProperties().apply {
                    textSize = 10
                    textColor = "#000000"
                    typeface = android.graphics.Typeface.DEFAULT
                    alignment = Layout.Alignment.ALIGN_CENTER
                }, Cell.MATCH_PARENT))
                add(TextCell("Name of \n" +
                        "the Hamlet/Tola/Para\n" +
                        "(5)", TextProperties().apply {
                    textSize = 10
                    textColor = "#000000"
                    typeface = android.graphics.Typeface.DEFAULT
                    alignment = Layout.Alignment.ALIGN_CENTER
                }, Cell.MATCH_PARENT))
                add(TextCell("Social Category\n" +
                        "(6)", TextProperties().apply {
                    textSize = 10
                    textColor = "#000000"
                    typeface = android.graphics.Typeface.DEFAULT
                    alignment = Layout.Alignment.ALIGN_CENTER
                }, Cell.MATCH_PARENT))
                add(TextCell("Categorized as (Rich, \n" +
                        "Medium, Poor)\n" +
                        "(7)", TextProperties().apply {
                    textSize = 10
                    textColor = "#000000"
                    typeface = android.graphics.Typeface.DEFAULT
                    alignment = Layout.Alignment.ALIGN_CENTER
                }, Cell.MATCH_PARENT))
            }

        val rows = LinkedList<LinkedList<Cell>>().apply {
            add(headerRow)

//            didiDetailList.value.forEach {
//                add(
//                    LinkedList<Cell>().apply {
//
//                    }
//                )
//            }
        }

        simplyPdfDocument.table.draw(rows, TableProperties().apply {
            borderColor = "#000000"
            borderWidth = 1
            drawBorder = true
        })

        simplyPdfDocument.finish()
    }

    fun generatePDF(context: Context) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            pdf(context)
        }
    }

}