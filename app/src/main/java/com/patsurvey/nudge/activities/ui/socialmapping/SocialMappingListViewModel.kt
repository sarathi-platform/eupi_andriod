package com.patsurvey.nudge.activities.ui.socialmapping

import androidx.lifecycle.viewModelScope
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.model.dataModel.DidiRankedModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SocialMappingListViewModel @Inject constructor(
    prefRepo: PrefRepo
):BaseViewModel() {
    private val _expandedCardIdsList = MutableStateFlow(listOf<Int>())
    private val _cards = MutableStateFlow(listOf<DidiRankedModel>())
    val expandedCardIdsList: StateFlow<List<Int>> get() = _expandedCardIdsList
    val cards: StateFlow<List<DidiRankedModel>> get() = _cards
    init {
        createDidisRankList()
    }
    fun createDidisRankList(){

        viewModelScope.launch {
            withContext(Dispatchers.Default){
                val testList = arrayListOf<DidiRankedModel>()
                repeat(20) { testList += DidiRankedModel(id = it, name = "Anamika Devi1", rank = "Poor",
                    pic = "https://user-images.githubusercontent.com/24237865/75088202-0d720480-5542-11ea-85f3-8726e69a9a26.jpg"
                , isOpen = false) }
                _cards.emit(testList)
            }
        }

    }


    fun onCardArrowClicked(cardId: Int) {
        _expandedCardIdsList.value = _expandedCardIdsList.value.toMutableList().also { list ->
            if (list.contains(cardId)) list.remove(cardId) else list.add(cardId)
        }
    }


}