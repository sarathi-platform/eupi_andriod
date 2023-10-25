package com.patsurvey.nudge.activities.ui.selectlanguage

import com.nhaarman.mockitokotlin2.whenever
import com.patsurvey.nudge.database.LanguageEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class LanguageViewModelTest {

    @Mock
    lateinit var languageRepository: LanguageRepository
    private lateinit var languageViewModel: LanguageViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        languageViewModel = LanguageViewModel(languageRepository)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun fetchLanguageListEmpty() {
        whenever(languageRepository.getAllLanguages()).thenReturn(null)
        languageViewModel.fetchLanguageList()
    }

    @ExperimentalCoroutinesApi
    @Test
    fun fetchLanguageListNotEmpty() {
        val languageEntity = LanguageEntity(1, 1, "", "", "")
        whenever(languageRepository.getAllLanguages()).thenReturn(listOf(languageEntity))
        languageViewModel.fetchLanguageList()
    }

    @ExperimentalCoroutinesApi
    @Test(expected = Exception::class)
    fun fetchLanguageListException() {
        Mockito.doThrow(Exception()).`when`(languageRepository).getAllLanguages()
        languageViewModel.fetchLanguageList()
    }

    @ExperimentalCoroutinesApi
    @Test
    fun updateSelectedVillage() {
        languageViewModel.updateSelectedVillage(1) { -> }
    }

    @ExperimentalCoroutinesApi
    @Test(expected = Exception::class)
    fun updateSelectedVillageException() {
        Mockito.doThrow(Exception()).`when`(languageRepository).fetchVillageDetailsForLanguage(1)
        languageViewModel.updateSelectedVillage(1) {}
    }

    @After
    fun tearDown() {
    }
}