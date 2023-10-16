package com.patsurvey.nudge.activities.ui.splash


import com.nhaarman.mockitokotlin2.whenever
import com.patsurvey.nudge.database.LanguageEntity
import com.patsurvey.nudge.model.response.ApiResponseModel
import com.patsurvey.nudge.model.response.ConfigResponseModel
import com.patsurvey.nudge.utils.FAIL
import com.patsurvey.nudge.utils.SUCCESS
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations


class ConfigViewModelTest {

    @Mock
    lateinit var configRepo: ConfigRepository
    lateinit var configViewModel: ConfigViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        configViewModel = ConfigViewModel(configRepo)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun fetchLanguageDetailsSuccess(){
        runBlockingTest() {
            val languageEntity: LanguageEntity = LanguageEntity(1,1,"","","")
            val configResponseModel: ConfigResponseModel =ConfigResponseModel(listOf(languageEntity),
                listOf(""), listOf()
           )
            val mockCollection = ApiResponseModel<ConfigResponseModel>(SUCCESS,"",configResponseModel,"")
           whenever(configRepo.fetchLanguageFromAPI()).thenReturn(mockCollection);
           configViewModel.fetchLanguageDetails({})

            assert(mockCollection.status == SUCCESS)
            assert(!mockCollection.data?.languageList?.isEmpty()!!)
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun fetchLanguageDetailsFail(){
        runBlockingTest() {
            val mockCollection = ApiResponseModel<ConfigResponseModel>(FAIL,"",null,"")
            whenever(configRepo.fetchLanguageFromAPI()).thenReturn(mockCollection);
            configViewModel.fetchLanguageDetails({})
            assert(mockCollection.status == FAIL)
            assert(mockCollection.data == null)
        }
    }

    @After
    fun tearDown() {
    }

}