package com.udacity.project4.locationreminders.savereminder

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.PointOfInterest
import com.udacity.project4.R
import com.udacity.project4.base.BaseViewModel
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import kotlinx.coroutines.launch

class SaveReminderViewModel(app: Application, private val dataSource: ReminderDataSource) : BaseViewModel(app) {

    val reminderTitle = MutableLiveData<String>()
    val reminderDescription = MutableLiveData<String>()
    val reminderSelectedLocationStr = MutableLiveData<String>()
    val selectedPOI = MutableLiveData<PointOfInterest>()
    var latitude = MutableLiveData<Double>()
    var longitude = MutableLiveData<Double>()
    //var count = 1
    //var denyAndDontAskeAgain = false

    /**
     * Clear the live data objects to start fresh next time the view model gets called
     */
    fun onClear() {
        reminderTitle.value = null
        reminderDescription.value = null
        reminderSelectedLocationStr.value = null
        selectedPOI.value = null
        latitude.value = null
        longitude.value = null
    }

    /*fun checkIfDenied(): Boolean {
        Log.i("num", "the count is $count and $denyAndDontAskeAgain")
        if (count >= 2) denyAndDontAskeAgain = true
        return denyAndDontAskeAgain
    }*/

    /**
     * Validate the entered data then saves the reminder data to the DataSource
     */
    fun validateAndSaveReminder(reminderData: ReminderDataItem) {
        if (validateEnteredData(reminderData)) {
            saveReminder(reminderData)
        }
    }

    /*fun verifyReminderIsOk(reminderCheck: ReminderDataItem): Boolean {
        return validateEnteredData(reminderCheck)
    }*/

    /**
     * Save the reminder to the data source
     */
    private fun saveReminder(reminderData: ReminderDataItem) {
        showLoading.value = true
        viewModelScope.launch {
            dataSource.saveReminder(
                ReminderDTO(
                    reminderData.title,
                    reminderData.description,
                    reminderData.location,
                    reminderData.latitude,
                    reminderData.longitude,
                    reminderData.id
                )
            )
            /*if (denyAndDontAskeAgain) {
                showSnackBar.value = "Geofence not added. To use this geofence " +
                        "feature, check your app info and allow \"Location\"."
            }*/
            showLoading.value = false
            navigationCommand.value = NavigationCommand.Back
            showToast.value = getApplication<Application>().getString(R.string.reminder_saved)    //R.string.reminder_saved.toString()
        }
    }

    /**
     * Validate the entered data and show error to the user if there's any invalid data
     */
    fun validateEnteredData(reminderData: ReminderDataItem): Boolean {
        if (reminderData.title.isNullOrEmpty()) {
            showSnackBarInt.value = R.string.err_enter_title
            return false
        }

        if (reminderData.location.isNullOrEmpty()) {
            showSnackBarInt.value = R.string.err_select_location
            return false
        }
        return true
    }

    companion object{
        internal const val ACTION_GEOFENCE_EVENT = ""
    }
}