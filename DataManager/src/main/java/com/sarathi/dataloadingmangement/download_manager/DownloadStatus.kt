package com.sarathi.dataloadingmangement.download_manager
enum class DownloadStatus(val value: Int) {
    UNAVAILABLE(0),
    DOWNLOADING(1),
    DOWNLOADED(2),
    DOWNLOAD_PAUSED(3);

    companion object {
        fun fromInt(downloadState: Int): DownloadStatus {
            return when (downloadState) {
                UNAVAILABLE.value -> UNAVAILABLE
                DOWNLOADING.value -> DOWNLOADING
                DOWNLOADED.value -> DOWNLOADED
                else -> DOWNLOAD_PAUSED
            }
        }
    }

}