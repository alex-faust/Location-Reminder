package com.udacity.project4.locationreminders.savereminder.selectreminderlocation

import android.annotation.SuppressLint
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PointOfInterest
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.databinding.FragmentSelectLocationBinding
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import org.koin.android.ext.android.inject


class SelectLocationFragment : BaseFragment(), OnMapReadyCallback {

    // Use Koin to get the view model of the SaveReminder
    override val _viewModel: SaveReminderViewModel by inject()
    private lateinit var binding: FragmentSelectLocationBinding
    private lateinit var map: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val layoutId = R.layout.fragment_select_location
        binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        binding.viewModel = _viewModel
        binding.lifecycleOwner = this
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(
            object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.map_options, menu)
                }
                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    when (menuItem.itemId) {
                        R.id.normal_map -> {
                            map.mapType = GoogleMap.MAP_TYPE_NORMAL
                        }

                        R.id.hybrid_map -> {
                            map.mapType = GoogleMap.MAP_TYPE_HYBRID
                        }

                        R.id.satellite_map -> {
                            map.mapType = GoogleMap.MAP_TYPE_SATELLITE
                        }

                        R.id.terrain_map -> {
                            map.mapType = GoogleMap.MAP_TYPE_TERRAIN
                        }
                        else -> return true
                    }
                    return true
                }
            },
            viewLifecycleOwner, Lifecycle.State.RESUMED
        )

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        setDisplayHomeAsUpEnabled(true)

        binding.saveLocationButton.setOnClickListener {
            saveLocation()
        }
        return binding.root
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        setPoiClick(map)

        fusedLocationProviderClient = LocationServices
            .getFusedLocationProviderClient(requireActivity())
        fusedLocationProviderClient.lastLocation.addOnSuccessListener {
            val location = LatLng(it.latitude, it.longitude)
            val zoomLevel = 15f
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, zoomLevel))
        }
        setMapStyle(map)
    }

    private fun setPoiClick(map: GoogleMap) {
        map.setOnPoiClickListener { poi ->
            val poiMarker = map.addMarker(
                MarkerOptions()
                    .position(poi.latLng)
                    .title(poi.name)
            )
            poiMarker?.showInfoWindow()
            onLocationSelected(poi)
        }
    }

    private fun setMapStyle(map: GoogleMap) {
        try {
             val success = map.setMapStyle(
                 MapStyleOptions.loadRawResourceStyle(
                     requireContext(), R.raw.map_style
                 )
             )
             if (!success) {
                 Log.e("TAG", "Style parsing failed")
             }
         } catch (e: Resources.NotFoundException) {
             Log.e("TAG", "Can't find style. Error: ", e)
         }
    }

    private fun onLocationSelected(poi: PointOfInterest) {
        _viewModel.reminderSelectedLocationStr.value = poi.name
        _viewModel.latitude.value = poi.latLng.latitude
        _viewModel.longitude.value = poi.latLng.longitude
        Log.i("find", "adding info")
    }

    private fun saveLocation() {
        _viewModel.navigationCommand.value = NavigationCommand.Back
        Log.i("find", "going back")
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onResume() {
        super.onResume()
    }
}