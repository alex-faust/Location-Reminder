package com.udacity.project4.locationreminders.savereminder.selectreminderlocation

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.databinding.FragmentSelectLocationBinding
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import org.koin.android.ext.android.inject
import java.util.Locale


class SelectLocationFragment : BaseFragment(), OnMapReadyCallback {

    // Use Koin to get the view model of the SaveReminder
    override val _viewModel: SaveReminderViewModel by inject()
    private lateinit var binding: FragmentSelectLocationBinding
    private lateinit var map: GoogleMap

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

        enableMyLocation()
        //setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(true)


        // TODO: add the map setup implementation


        // TODO: zoom to the user location after taking his permission

        // TODO: add style to the map
        // TODO: put a marker to location that the user selected


        // TODO: call this function after the user confirms on the selected location
        onLocationSelected()


        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        enableMyLocation()

        val sf = LatLng(37.7921469, -122.4175258)
        val zoomLevel = 15f

        val homeLatLng = sf

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(homeLatLng, zoomLevel))
        setPoiClick(map)
        setMapLongClick(map)

        // TODO: add the map setup implementation


        // TODO: zoom to the user location after taking his permission

        // TODO: add style to the map
        // TODO: put a marker to location that the user selected


        // TODO: call this function after the user confirms on the selected location
        onLocationSelected()
        map.setOnMarkerClickListener { marker ->
            // on marker click we are getting the title of our marker
            // which is clicked and displaying it in a toast message.
            val markerName = marker.title
            val snippet = marker.snippet
            val position = marker.position
            val latLng = LatLng(position.latitude, position.longitude)
            //val latLng: LatLng = marker
            false
        }
    }

    private fun setPoiClick(map: GoogleMap) {
        map.setOnPoiClickListener { poi ->
            val poiMarker = map.addMarker(
                MarkerOptions()
                    .position(poi.latLng)
                    .title(poi.name)
            )
            poiMarker?.showInfoWindow()
        }
    }

    private fun setMapLongClick(map: GoogleMap) {
        map.setOnMapLongClickListener { latLng ->
            // A Snippet is Additional text that's displayed below the title.
            val snippet = String.format(
                Locale.getDefault(),
                "Lat: %1$.5f, Long: %2$.5f",
                latLng.latitude,
                latLng.longitude
            )
            map.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(getString(R.string.dropped_pin))
                    .snippet(snippet)
            )
        }
    }

    private fun setMapStyle(map: GoogleMap) {
        // Customize the styling of the base map using a JSON object defined
        // in a raw resource file.
        /*
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
         */
    }

    private fun onLocationSelected() {
        // TODO: When the user confirms on the selected location, (get latlng of poi from location)
        //  send back the selected location details to the view model (save all the info to the _viewModel)
        //  and navigate back to the previous fragment to save the reminder and add the geofence (after clicking save, navigate back to the list and add them to the list rv)




    }

    //Permission functions
    private fun isPermissionGranted(): Boolean {
        return ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun enableMyLocation() {
        if (isPermissionGranted()) {
            //map.isMyLocationEnabled = true
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                REQUEST_LOCATION_PERMISSION
            )
        }
    }

    @Deprecated("Deprecated in Java")
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                enableMyLocation()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onResume() {
        super.onResume()
        if (isPermissionGranted()) {
            enableMyLocation()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    companion object {
        private const val REQUEST_LOCATION_PERMISSION = 1
    }

}