package com.tothenew.android_starter_project.network.interfaces

interface SuccessAPICallback<T> {

    /**
     * This method is used for the callback
     *
     * @param t response result object
     */
    fun onResponse(t: T)
}
