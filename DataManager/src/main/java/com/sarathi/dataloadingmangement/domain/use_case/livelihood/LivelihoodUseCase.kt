package com.sarathi.dataloadingmangement.domain.use_case.livelihood

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sarathi.dataloadingmangement.model.response.LivelihoodResponse
import com.sarathi.dataloadingmangement.repository.liveihood.CoreLivelihoodRepositoryImpl
import javax.inject.Inject

class LivelihoodUseCase @Inject constructor(
    private val coreLivelihoodRepositoryImpl: CoreLivelihoodRepositoryImpl
) {
    suspend operator fun invoke() {
        val responseData = "[\n" +
                "        {\n" +
                "            \"livelihood\": {\n" +
                "                \"id\": 9,\n" +
                "                \"name\": \"Goetry\",\n" +
                "                \"status\": 1,\n" +
                "                \"image\": \"Image1.png\",\n" +
                "                \"languages\": [\n" +
                "                    {\n" +
                "                        \"languageCode\": \"en\",\n" +
                "                        \"name\": \"Goetry\",\n" +
                "                        \"id\": 9\n" +
                "                    },\n" +
                "                    {\n" +
                "                        \"languageCode\": \"bn\",\n" +
                "                        \"name\": \"গোয়েট্রি\",\n" +
                "                        \"id\": 9\n" +
                "                    }\n" +
                "                ]\n" +
                "            },\n" +
                "            \"assets\": [\n" +
                "                {\n" +
                "                    \"id\": 28,\n" +
                "                    \"name\": \"Adult Female\",\n" +
                "                    \"status\": 1,\n" +
                "                    \"languages\": [\n" +
                "                        {\n" +
                "                            \"languageCode\": \"en\",\n" +
                "                            \"name\": \"Adult Female\",\n" +
                "                            \"id\": 28\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"languageCode\": \"bn\",\n" +
                "                            \"name\": \"প্রাপ্তবয়স্ক মহিলা\",\n" +
                "                            \"id\": 28\n" +
                "                        }\n" +
                "                    ],\n" +
                "                    \"value\": 0.0\n" +
                "                },\n" +
                "                {\n" +
                "                    \"id\": 27,\n" +
                "                    \"name\": \"Adult Male\",\n" +
                "                    \"status\": 1,\n" +
                "                    \"languages\": [\n" +
                "                        {\n" +
                "                            \"languageCode\": \"en\",\n" +
                "                            \"name\": \"Adult Male\",\n" +
                "                            \"id\": 27\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"languageCode\": \"bn\",\n" +
                "                            \"name\": \"প্রাপ্তবয়স্ক পুরুষ\",\n" +
                "                            \"id\": 27\n" +
                "                        }\n" +
                "                    ],\n" +
                "                    \"value\": 0.0\n" +
                "                }\n" +
                "            ],\n" +
                "            \"products\": [\n" +
                "                {\n" +
                "                    \"id\": 23,\n" +
                "                    \"name\": \"Baby Girl\",\n" +
                "                    \"status\": 1,\n" +
                "                    \"languages\": [\n" +
                "                        {\n" +
                "                            \"languageCode\": \"en\",\n" +
                "                            \"name\": \"Baby Girl\",\n" +
                "                            \"id\": 23\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"languageCode\": \"bn\",\n" +
                "                            \"name\": \"বাচ্চা মেয়ে\",\n" +
                "                            \"id\": 23\n" +
                "                        }\n" +
                "                    ]\n" +
                "                },\n" +
                "                {\n" +
                "                    \"id\": 22,\n" +
                "                    \"name\": \"Baby Boy\",\n" +
                "                    \"status\": 1,\n" +
                "                    \"languages\": [\n" +
                "                        {\n" +
                "                            \"languageCode\": \"en\",\n" +
                "                            \"name\": \"Baby Boy\",\n" +
                "                            \"id\": 22\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"languageCode\": \"bn\",\n" +
                "                            \"name\": \"ছেলে শিশু\",\n" +
                "                            \"id\": 22\n" +
                "                        }\n" +
                "                    ]\n" +
                "                }\n" +
                "            ],\n" +
                "            \"events\": [\n" +
                "                {\n" +
                "                    \"id\": 10,\n" +
                "                    \"name\": \"Birth\",\n" +
                "                    \"status\": 1,\n" +
                "                    \"type\": null,\n" +
                "                    \"languages\": [\n" +
                "                        {\n" +
                "                            \"languageCode\": \"en\",\n" +
                "                            \"name\": \"Birth\",\n" +
                "                            \"id\": 10\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"languageCode\": \"bn\",\n" +
                "                            \"name\": \"জন্ম\",\n" +
                "                            \"id\": 10\n" +
                "                        }\n" +
                "                    ]\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"livelihood\": {\n" +
                "                \"id\": 10,\n" +
                "                \"name\": \"Agriculture\",\n" +
                "                \"status\": 1,\n" +
                "                \"image\": \"Image1.png\",\n" +
                "                \"languages\": [\n" +
                "                    {\n" +
                "                        \"languageCode\": \"en\",\n" +
                "                        \"name\": \"Agriculture\",\n" +
                "                        \"id\": 10\n" +
                "                    },\n" +
                "                    {\n" +
                "                        \"languageCode\": \"bn\",\n" +
                "                        \"name\": \"কৃষি\",\n" +
                "                        \"id\": 10\n" +
                "                    }\n" +
                "                ]\n" +
                "            },\n" +
                "            \"assets\": [\n" +
                "                {\n" +
                "                    \"id\": 29,\n" +
                "                    \"name\": \"Hand Pump\",\n" +
                "                    \"status\": 1,\n" +
                "                    \"languages\": [\n" +
                "                        {\n" +
                "                            \"languageCode\": \"en\",\n" +
                "                            \"name\": \"Hand Pump\",\n" +
                "                            \"id\": 29\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"languageCode\": \"bn\",\n" +
                "                            \"name\": \"হাত চাপা\",\n" +
                "                            \"id\": 29\n" +
                "                        }\n" +
                "                    ],\n" +
                "                    \"value\": 0.0\n" +
                "                },\n" +
                "                {\n" +
                "                    \"id\": 30,\n" +
                "                    \"name\": \"Tube Well\",\n" +
                "                    \"status\": 1,\n" +
                "                    \"languages\": [\n" +
                "                        {\n" +
                "                            \"languageCode\": \"en\",\n" +
                "                            \"name\": \"Tube Well\",\n" +
                "                            \"id\": 30\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"languageCode\": \"bn\",\n" +
                "                            \"name\": \"টিউব ওয়েল\",\n" +
                "                            \"id\": 30\n" +
                "                        }\n" +
                "                    ],\n" +
                "                    \"value\": 0.0\n" +
                "                }\n" +
                "            ],\n" +
                "            \"products\": [\n" +
                "                {\n" +
                "                    \"id\": 26,\n" +
                "                    \"name\": \"Brinjal\",\n" +
                "                    \"status\": 1,\n" +
                "                    \"languages\": [\n" +
                "                        {\n" +
                "                            \"languageCode\": \"en\",\n" +
                "                            \"name\": \"Brinjal\",\n" +
                "                            \"id\": 26\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"languageCode\": \"bn\",\n" +
                "                            \"name\": \"বেগুন\",\n" +
                "                            \"id\": 26\n" +
                "                        }\n" +
                "                    ]\n" +
                "                },\n" +
                "                {\n" +
                "                    \"id\": 25,\n" +
                "                    \"name\": \"Potato\",\n" +
                "                    \"status\": 1,\n" +
                "                    \"languages\": [\n" +
                "                        {\n" +
                "                            \"languageCode\": \"en\",\n" +
                "                            \"name\": \"Potato\",\n" +
                "                            \"id\": 25\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"languageCode\": \"bn\",\n" +
                "                            \"name\": \"আলু\",\n" +
                "                            \"id\": 25\n" +
                "                        }\n" +
                "                    ]\n" +
                "                },\n" +
                "                {\n" +
                "                    \"id\": 24,\n" +
                "                    \"name\": \"Tomato\",\n" +
                "                    \"status\": 1,\n" +
                "                    \"languages\": [\n" +
                "                        {\n" +
                "                            \"languageCode\": \"en\",\n" +
                "                            \"name\": \"Tomato\",\n" +
                "                            \"id\": 24\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"languageCode\": \"bn\",\n" +
                "                            \"name\": \"টমেটো\",\n" +
                "                            \"id\": 24\n" +
                "                        }\n" +
                "                    ]\n" +
                "                }\n" +
                "            ],\n" +
                "            \"events\": [\n" +
                "                {\n" +
                "                    \"id\": 11,\n" +
                "                    \"name\": \"Events\",\n" +
                "                    \"status\": 1,\n" +
                "                    \"type\": null,\n" +
                "                    \"languages\": [\n" +
                "                        {\n" +
                "                            \"languageCode\": \"en\",\n" +
                "                            \"name\": \"Events\",\n" +
                "                            \"id\": 11\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"languageCode\": \"bn\",\n" +
                "                            \"name\": \"ঘটনা\",\n" +
                "                            \"id\": 11\n" +
                "                        }\n" +
                "                    ]\n" +
                "                }\n" +
                "            ]\n" +
                "        }\n" +
                "    ]"
        val type = object : TypeToken<List<LivelihoodResponse?>?>() {}.type
        val livelihood = Gson().fromJson<List<LivelihoodResponse>>(
            responseData,
            type
        )
        livelihood.forEach { livelihoodResponse ->
            livelihoodResponse.livelihood?.let { livelihood ->
                coreLivelihoodRepositoryImpl.saveLivelihoodItemToDB(livelihood)
            }
            livelihoodResponse.assets?.let { assets ->
                coreLivelihoodRepositoryImpl.saveLivelihoodItemListToDB(assets)
            }
            livelihoodResponse.products?.let { products ->
                coreLivelihoodRepositoryImpl.saveLivelihoodItemListToDB(products)
            }
            livelihoodResponse.events?.let { events ->
                coreLivelihoodRepositoryImpl.saveLivelihoodItemListToDB(events)
            }

        }


    }
}