package com.udacity.project4.util

import androidx.test.espresso.idling.CountingIdlingResource

object EspressoIdlingResource {

    //when counter is > 0, app is working
    //when counter is < 0, it is considered idle
    private const val RESOURCE = "GLOBAL"

    @JvmField
    val countingIdlingResource = CountingIdlingResource(RESOURCE)

    fun increment() {
        countingIdlingResource.increment()
    }

    fun decrement() {
        if (!countingIdlingResource.isIdleNow) {
            countingIdlingResource.decrement()
        }
    }


}

//this method is helpful so we don't have to worry about constantly incrementing and decrementing
//the idling source
inline fun <T> wrapEspressoIdlingResource(function: () -> T): T {
    EspressoIdlingResource.increment() //set app as busy
    return try {
        function()
    } finally {
        EspressoIdlingResource.decrement() //set app as idle
    }
}