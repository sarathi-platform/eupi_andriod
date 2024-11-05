package com.patsurvey.nudge.activities.survey

/*@OptIn(ExperimentalCoroutinesApi::class)
@LargeTest
@RunWith(androidx.test.runner.AndroidJUnit4::class)
class ConfigViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = TestCoroutineDispatcher()

    private val testScope = TestCoroutineScope(testDispatcher)

    @Mock
    private lateinit var prefRepo: PrefRepo

    @Mock
    private lateinit var apiInterface: ApiService

    @Mock
    private lateinit var languageListDao: LanguageListDao

    @Mock
    private lateinit var casteListDao: CasteListDao

    @Mock
    private lateinit var bpcScorePercentageDao: BpcScorePercentageDao

    @Mock
    private lateinit var context: Context

    private lateinit var viewModel: ConfigViewModel

    @Mock
    private lateinit var  configRepository: ConfigRepository

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        viewModel = ConfigViewModel(configRepository)
        launchActivity<MainActivity>().use { scenario ->
            scenario.moveToState(Lifecycle.State.CREATED)
        }
    }

    @Test
    fun testIsLoggedIn() {
        // Mock the behavior of prefRepo
        `when`(prefRepo.getAccessToken()).thenReturn("your_access_token")

        val isLoggedIn = viewModel.isLoggedIn()

        assert(isLoggedIn)
    }

    @Test
    fun testIsLoggedInFailed() {
        // Mock the behavior of prefRepo
        Mockito.`when`(prefRepo.getAccessToken()).thenReturn("")

        val isLoggedIn = viewModel.isLoggedIn()

        assert(!isLoggedIn)
    }

    @Test
    fun testFetchLanguageDetailsSuccess() = testScope.runBlockingTest {
        // Mock API response
        val questionImageUrlList = listOf("https://uat.eupi-sarthi.in/write-api/file/question-image/Section2_TotalProductiveAsset.webp",
            "https://uat.eupi-sarthi.in/write-api/file/question-image/Section1_2wheeler.webp",
            "https://uat.eupi-sarthi.in/write-api/file/question-image/Section2_AgricultureLand.webp",
            "https://uat.eupi-sarthi.in/write-api/file/question-image/Section2_DistressedMigration.webp",
            "https://uat.eupi-sarthi.in/write-api/file/question-image/Section1_ASHAworker.webp",
            "https://uat.eupi-sarthi.in/write-api/file/question-image/Section2_Witchhunt.webp",
            "https://uat.eupi-sarthi.in/write-api/file/question-image/Section1and2_AdultFemale_WomanHeaded.webp")
        val languageList = listOf<LanguageEntity>(
            LanguageEntity(
                id = 2,
                language = "English",
                langCode = "en",
                orderNumber = 1,
                localName = "English"
            ),
            LanguageEntity(
                id = 1,
                language = "Hindi",
                langCode = "hn",
                orderNumber = 2,
                localName = "हिंदी"
            ),
            LanguageEntity(
                id = 3,
                language = "Bengali",
                langCode = "bn",
                orderNumber = 3,
                localName = "বাংলা"
            ),
            LanguageEntity(
                id = 4,
                language = "Assamese",
                langCode = "as",
                orderNumber = 4,
                localName = "অসমীয়া"
            ),
        )
        val bpcSuveryPercentage = listOf<BpcScorePercentageResponse>(
            BpcScorePercentageResponse(
                percentage = 70,
                name = "RAJASTHAN",
                id = 27
            ),
            BpcScorePercentageResponse(
                percentage = 70,
                name = "TRIPURA",
                id = 31
            ),
            BpcScorePercentageResponse(
                percentage = 70,
                name = "ASSAM",
                id = 4
            ),
        )
        val responseBody: ApiResponseModel<ConfigResponseModel> = ApiResponseModel(
            SUCCESS,
            "Success",
            ConfigResponseModel(
                bpcSurveyPercentage = bpcSuveryPercentage,
                languageList = languageList,
                image_profile_link = questionImageUrlList
            )
        )

        `when`(apiInterface.configDetails()).thenReturn(responseBody)

        val actualResponse = apiInterface.configDetails()
        assert(actualResponse.status == SUCCESS)
        assert(!actualResponse.data?.languageList?.isEmpty()!!)
        assert(!actualResponse.data?.bpcSurveyPercentage?.isEmpty()!!)
        assert(!actualResponse.data?.image_profile_link?.isEmpty()!!)

    }

    @Test
    fun testFetchLanguageDetailsFailure() = testScope.runBlockingTest {
        // Mock API response for failure
        val responseBody: ApiResponseModel<ConfigResponseModel> = ApiResponseModel(FAIL, "Failure", null)

        `when`(apiInterface.configDetails()).thenReturn(responseBody)

        val actualResponse = apiInterface.configDetails()
        assert(actualResponse.status == FAIL)
        assert(actualResponse.data == null)

    }
}*/
