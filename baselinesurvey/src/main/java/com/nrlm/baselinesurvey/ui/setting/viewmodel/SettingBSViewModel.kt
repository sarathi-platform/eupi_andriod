
                if (fileUriList.isNotEmpty()) {
                    val zipLogDbFileUri = compression.compressData(
                        BaselineCore.getAppContext(),
                        zipFileName,
                        Environment.DIRECTORY_DOCUMENTS + SARATHI_DIRECTORY_NAME + "/" + getUserMobileNumber(),
                        fileAndDbZipList,
                        getUserMobileNumber()
                    )
                    zipLogDbFileUri?.let {
                        if (it != Uri.EMPTY) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                                fileUriList.add(it)
                            else fileUriList.add(uriFromFile(context = BaselineCore.getAppContext(),
                                applicationID = BuildConfig.APPLICATION_ID,
                                file = it.toFile()))
                        }
                    }
                }

                BaselineLogger.d("SettingBSViewModel", " Share Dialog Open ${fileUriList.json()}" )
                openShareSheet(fileUriList, title, ZIP_MIME_TYPE)
                CoreSharedPrefs.getInstance(BaselineCore.getAppContext()).setFileExported(true)
                onEvent(LoaderEvent.UpdateLoaderState(false))
            } catch (exception: Exception) {
                BaselineLogger.e("Compression Exception", exception.message ?: "")
                exception.printStackTrace()
                onEvent(LoaderEvent.UpdateLoaderState(false))
            }
        }
    }

    private suspend fun getSummaryFile(): Pair<String, Uri?>? {
        val summaryFileNameWithoutExtension = "Sarathi_${
            getFirstName(settingBSUserCase.getUserDetailsUseCase.getUserName())
        }_${prefRepo.getUniqueUserIdentifier()}_summary_file"

        val summaryFileNameWithExtension = summaryFileNameWithoutExtension + LOCAL_BACKUP_EXTENSION

        return settingBSUserCase.getSummaryFileUseCase.invoke(
            userId = prefRepo.getUniqueUserIdentifier(),
            mobileNo = getUserMobileNumber(),
            fileNameWithoutExtension = summaryFileNameWithoutExtension,
            fileNameWithExtension = summaryFileNameWithExtension
        )
    }

    override fun <T> onEvent(event: T) {
        when (event) {
            is LoaderEvent.UpdateLoaderState -> {
                _loaderState.value = _loaderState.value.copy(
                    isLoaderVisible = event.showLoader
                )
            }
        }
    }

   fun getUserMobileNumber():String{
        return settingBSUserCase.getUserDetailsUseCase.getUserMobileNumber()
    }
}