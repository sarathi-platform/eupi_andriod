package com.patsurvey.nudge.network

object BaseNetworkConstants {
    const val DOMAIN = "https://run.mocky.io/"

    const val API_VERSION = "v3"

    const val API_ORDERS_URI = "/b6a30bb0-140f-4966-8608-1dc35fa1fadc"

    const val API_ORDERS = API_VERSION + API_ORDERS_URI

    const val RANDOM_IMAGE_URL = "https://picsum.photos/200/300?random="

    const val CODE_INVALID = -1
    const val CODE_SUCCESS = 0
    const val CODE_NO_NETWORK = -100
    const val CODE_SERVER_ERROR = 500
}