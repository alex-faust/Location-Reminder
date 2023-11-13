package com.udacity.project4.locationreminders.savereminder

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.material.snackbar.Snackbar
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.databinding.FragmentSaveReminderBinding
import com.udacity.project4.locationreminders.geofence.GeofenceBroadcastReceiver
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel.Companion.ACTION_GEOFENCE_EVENT
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import org.koin.android.ext.android.inject

class SaveReminderFragment : BaseFragment() {

    // Get the view model this time as a single to be shared with another fragment
    override val _viewModel: SaveReminderViewModel by inject()
    private lateinit var binding: FragmentSaveReminderBinding
    private lateinit var geofencingClient: GeofencingClient
    private var locationSettingsEnabled = false
    private val runningQOrLater = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
    private lateinit var contxt: Context

    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(requireContext(), GeofenceBroadcastReceiver::class.java)
        intent.action = ACTION_GEOFENCE_EVENT
        var intentFlagType = PendingIntent.FLAG_UPDATE_CURRENT
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            intentFlagType = PendingIntent.FLAG_MUTABLE
        }
        PendingIntent.getBroadcast(requireContext(), 0, intent, intentFlagType)
    }

    private val requestForegroundPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                Log.i(TAG, "foreground permission granted")
            } else {
                // if permission denied then check whether never ask again is selected or not by making use of
                !ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION
                )
                Log.i(TAG, "foreground permission denied")
            }
        }

    @RequiresApi(Build.VERSION_CODES.Q)
    private val requestBackgroundPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                Log.i(TAG, "background permission granted")
                //saveReminder()
            } else {
                // if permission denied then check whether never ask again is selected or not by making use of
                !ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(), Manifest.permission.ACCESS_BACKGROUND_LOCATION
                )
                Log.i(TAG, "background permission denied")
                //saveReminder()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val layoutId = R.layout.fragment_save_reminder
        binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        setDisplayHomeAsUpEnabled(true)
        binding.viewModel = _viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        geofencingClient = LocationServices.getGeofencingClient(requireContext())


        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this


        binding.selectLocation.setOnClickListener {
            checkForegroundPermissions()
            val directions = SaveReminderFragmentDirections
                .actionSaveReminderFragmentToSelectLocationFragment()
            _viewModel.navigationCommand.value = NavigationCommand.To(directions)
        }

        binding.saveReminder.setOnClickListener {

            val title = _viewModel.reminderTitle.value?.trim()
            val description = _viewModel.reminderDescription.value?.trim()
            val location = _viewModel.reminderSelectedLocationStr.value
            val latitude = _viewModel.latitude.value
            val longitude = _viewModel.longitude.value

            val reminder = ReminderDataItem(
                title,
                description,
                location,
                latitude,
                longitude
            )

            if (_viewModel.validateEnteredData(reminder)) {
                //checkBackgroundPermission()
                addGeofenceForReminder(reminder)
            } else {
                Log.i(TAG, "Reminder is missing info")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun saveReminder(reminder: ReminderDataItem) {

        /*if (isForegroundPermissionsGranted() && !isBackgroundPermissionGranted() && !locationSettingsEnabled) {
            Log.i(
                TAG,
                "outcome 1: Foreground is ${isForegroundPermissionsGranted()} and background is ${isBackgroundPermissionGranted()} and locationSetttings enabled is $locationSettingsEnabled"
            )
            //addGeofenceForReminder(reminder)
            _viewModel.validateAndSaveReminder(reminder)
        } else if (isForegroundPermissionsGranted() && isBackgroundPermissionGranted() && !locationSettingsEnabled) {
            Log.i(
                TAG,
                "outcome 2: Foreground is ${isForegroundPermissionsGranted()} and background is ${isBackgroundPermissionGranted()} and locationSetttings enabled is $locationSettingsEnabled"
            )
            //addGeofenceForReminder(reminder)
            _viewModel.validateAndSaveReminder(reminder) //on the for the second outcome, it shows geofence added but there is no notification of it so i'm assuming we aren't adding it.
        } else if (!isForegroundPermissionsGranted() && !isBackgroundPermissionGranted() && locationSettingsEnabled) {
            Log.i(
                TAG,
                "outcome 3: Foreground is ${isForegroundPermissionsGranted()} and background is ${isBackgroundPermissionGranted()} and locationSetttings enabled is $locationSettingsEnabled"
            )
            //addGeofenceForReminder(reminder)
            _viewModel.validateAndSaveReminder(reminder)// on the for the second outcome, it shows geofence added but there is no notification of it so i'm assuming we aren't adding it.
        } else if (!isForegroundPermissionsGranted() && !isBackgroundPermissionGranted() && !locationSettingsEnabled) {
            Log.i(
                TAG,
                "outcome 4: Foreground is ${isForegroundPermissionsGranted()} and background is ${isBackgroundPermissionGranted()} and locationSetttings enabled is $locationSettingsEnabled"
            )
            //addGeofenceForReminder(reminder)
            _viewModel.validateAndSaveReminder(reminder)
        } else {
            Log.i(
                TAG,
                "outcome 5: Foreground is ${isForegroundPermissionsGranted()} and background is ${isBackgroundPermissionGranted()} and locationSetttings enabled is $locationSettingsEnabled"
            )
            //addGeofenceForReminder(reminder)
            //_viewModel.validateAndSaveReminder(reminder)
        }*/
        _viewModel.validateAndSaveReminder(reminder)
    }

    private fun checkForegroundPermissions() {
        if (isForegroundPermissionsGranted()) {
            checkDeviceLocationSettings()
        } else {
            requestForegroundPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun checkBackgroundPermission() {
        if (isBackgroundPermissionGranted()) {
            checkDeviceLocationSettings()
        } else {
            requestBackgroundPermission.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }
    }

    private fun isForegroundPermissionsGranted(): Boolean {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
            contxt,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun isBackgroundPermissionGranted(): Boolean {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
            contxt,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        )
    }

    private fun checkDeviceLocationSettings(resolve: Boolean = true) {
        val timeInterval: Long = 5000
        val locationRequest = LocationRequest
            .Builder(Priority.PRIORITY_LOW_POWER, timeInterval).build()
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val settingsClient = LocationServices.getSettingsClient(contxt)
        val locationSettingsResponseTask = settingsClient.checkLocationSettings(builder.build())

        locationSettingsResponseTask.addOnFailureListener { exception ->
            if (exception is ResolvableApiException && resolve) {
                try {
                    startIntentSenderForResult(
                        exception.resolution.intentSender,
                        REQUEST_CODE_DEVICE_LOCATION_SETTINGS,
                        null,
                        0,
                        0,
                        0,
                        null
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    Log.i(TAG, "Error getting location settings resolution: $sendEx.message")
                    locationSettingsEnabled = false
                }
            } else {
                Snackbar.make(
                    binding.root,
                    R.string.location_required_error, Snackbar.LENGTH_INDEFINITE
                ).setAction(android.R.string.ok) {
                    checkDeviceLocationSettings()
                }.show()
            }
        }
        locationSettingsResponseTask.addOnCompleteListener {
            if (it.isSuccessful) {
                locationSettingsEnabled = true
                Log.i(
                    TAG,
                    "location settings response complete, locationSettings set to true? $locationSettingsEnabled"
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Make sure to clear the view model after destroy, as it's a single view model.
        _viewModel.onClear()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("MissingPermission")
    private fun addGeofenceForReminder(reminder: ReminderDataItem) {

        val geofence = Geofence.Builder()
            .setRequestId(reminder.id)
            .setCircularRegion(
                reminder.latitude!!,
                reminder.longitude!!,
                GEOFENCE_RADIUS_IN_METERS
            )
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
            .build()

        val geofencingRequest = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence).build()

        checkForegroundPermissions()
        checkBackgroundPermission()
        if (isForegroundPermissionsGranted() && isBackgroundPermissionGranted() && locationSettingsEnabled) {
            geofencingClient.addGeofences(geofencingRequest, geofencePendingIntent).run {
                addOnSuccessListener {
                    _viewModel.showToast.value =
                        "Geofence location added"
                    Log.i(TAG, "Geofence added")
                    saveReminder(reminder)
                }
                addOnFailureListener {
                    _viewModel.showToast.value =
                        contxt.getString(R.string.geofences_not_added) + "You may need to allow Location permission in your settings"
                    Log.e(TAG, "Geofence not added")
                    /*

                    _viewModel.navigationCommand.value = NavigationCommand.Back

                       I want to add  navigation to next screen here but we don't want
                       to add a reminder to the database if it doesn't have a geofence, which
                       was stated in my audit. According to android app best practices, we don't
                       want the user in a state where they are unable to use the app but it is
                       okay to remove some features. If they don't want to add background location,
                       the user is not able to add geofence at all so do they just stay at this
                       screen?

                        Knowing this, if there is no geofence added, and the user cant navigate
                        to the next screen, how do you have no background location and still add
                        a geofence? The 5 outcomes shown in the gifs, some of them do not have
                        background location granted but geofence is still added? This confuses me.
                        */
                }
            }
        }
    }

    @SuppressLint("NewApi")
    override fun onStart() {
        super.onStart()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        contxt = context
    }


    companion object {
        const val GEOFENCE_RADIUS_IN_METERS = 100f
        private const val REQUEST_CODE_DEVICE_LOCATION_SETTINGS = 20
        private const val TAG = "Save Reminder"
    }
}
