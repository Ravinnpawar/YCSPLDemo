package com.ravindra.ycspldemo.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.ravindra.ycspldemo.R
import com.ravindra.ycspldemo.databinding.ActivityMapsBinding
import com.ravindra.ycspldemo.viewmodels.MapViewModel


class MapsActivity : AppCompatActivity(), OnMapReadyCallback, OnMarkerClickListener,
    GoogleMap.OnMarkerDragListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: com.ravindra.ycspldemo.databinding.ActivityMapsBinding
    private lateinit var lastLocation: Location
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val bottomSheetView by lazy { findViewById<ConstraintLayout>(R.id.bottomSheet) }
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var mapViewModel: MapViewModel
    lateinit var strPropertyName: String
    lateinit var strCoordinates: String
    lateinit var propertyCoordinates: AppCompatEditText
    lateinit var propertyName: AppCompatEditText

    companion object {
        private const val LOCATION_REQUEST_CODE = 1
    }

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        mapViewModel = ViewModelProvider(this).get(MapViewModel::class.java)
        propertyCoordinates = findViewById(R.id.proprty_coordinates)
        propertyName = findViewById(R.id.proprty_val)
        val submitButton: AppCompatButton = findViewById(R.id.submit_button)
        if (bottomSheetView != null) {
            bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetView)
        }
        binding.fab.setOnClickListener(View.OnClickListener {
            setBottomSheetVisibility(true)
            it.visibility = View.GONE
            fusedLocationProviderClient.lastLocation.addOnSuccessListener(this) { location ->
                if (location != null) {
                    lastLocation = location
                    val currentLatLong = LatLng(location.latitude, location.longitude)
                    placeMarkerOnMap(currentLatLong)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLong, 15f))
                    mMap.uiSettings.setZoomControlsEnabled(false);
                    mMap.uiSettings.setMyLocationButtonEnabled(false);
                    propertyCoordinates.setText(currentLatLong.latitude.toString()+ "," + currentLatLong.longitude.toString() )

                }
            }
        })
        binding.fabClose.setOnClickListener(OnClickListener {
            mMap.clear()
            clearFields()
            setBottomSheetVisibility(false)

            //code to get details from the db
            /*mapViewModel.getPropertyDetails(this)!!.observe(this, Observer {
                if (it==null)
                {
                    Log.e("MapActivity","Data Not Found")

                }else{

                    for (item in it){
                       // Toast.makeText(this, item.propertName, Toast.LENGTH_SHORT).show()
                    }
                }
            })*/

        })
        submitButton.setOnClickListener(OnClickListener {
            strPropertyName = propertyName.text.toString().trim()
            strCoordinates = propertyCoordinates.text.toString().trim()
            if (strPropertyName.isEmpty()) {
                propertyName.error = "Please Enter Property Name"
            } else if (strCoordinates.isEmpty()) {
                propertyCoordinates.error = "Please Enter Coordinates"
            } else {
                val coords=strCoordinates.split(",")
                val latitude:Double=coords.get(0).toDouble()
                val longitude:Double=coords.get(1).toDouble()
                mapViewModel.insertData(this, strPropertyName, latitude,longitude)

                Toast.makeText(this, "Inserted Successfully", Toast.LENGTH_SHORT).show()
            }
            clearFields()
        })
        setBottomSheetVisibility(false)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    Log.e("MapsActivity", BottomSheetBehavior.STATE_COLLAPSED.toString())
                    binding.fab.visibility = VISIBLE
                    binding.fabClose.visibility = GONE
                    mMap.clear()
                }
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    Log.e("MapsActivity", BottomSheetBehavior.STATE_EXPANDED.toString())
                    binding.fabClose.visibility = VISIBLE
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                //Here code will go for if bottom sheet is down or vice versa
            }

        })
        val screenOrientation=resources.configuration.orientation
        if (screenOrientation==Configuration.ORIENTATION_PORTRAIT){
            //Required if any screen orientation changes handle rapal
        }
        else if (screenOrientation==Configuration.ORIENTATION_LANDSCAPE){
            propertyCoordinates.setLines(1)
            propertyName.setLines(1)
        }

    }

    private fun clearFields() {
        propertyName.setText("")
        propertyCoordinates.setText("")
    }

    private fun setBottomSheetVisibility(isVisible: Boolean) {
        val updatedState =
            if (isVisible) BottomSheetBehavior.STATE_EXPANDED else BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBehavior.state = updatedState
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.setOnMarkerClickListener(this)
        setupMap()
        mMap.setOnMarkerDragListener(this)
    }

    private fun setupMap() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_REQUEST_CODE
            )
            return
        }
        mMap.isMyLocationEnabled = true
        fusedLocationProviderClient.lastLocation.addOnSuccessListener(this) { location ->
            if (location != null) {
                lastLocation = location
                val currentLatLong = LatLng(location.latitude, location.longitude)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLong, 15f))
                mMap.uiSettings.setZoomControlsEnabled(false);
                mMap.uiSettings.isMyLocationButtonEnabled=false


            }
        }
    }

    private fun placeMarkerOnMap(currentLatLong: LatLng) {
        val markerOptions = MarkerOptions().position(currentLatLong)
        markerOptions.title("$currentLatLong")
        mMap.addMarker(markerOptions)?.isDraggable = true
    }

    override fun onMarkerClick(p0: Marker) = false
    var exitOpened = false
    override fun onBackPressed() {
        if (isTaskRoot && !exitOpened) {
            exitOpened = true
            setBottomSheetVisibility(false)
            Toast.makeText(this, "Please press back again to exit", Toast.LENGTH_SHORT).show()
            return
        }
        super.onBackPressed()

    }

    override fun onMarkerDrag(marker: Marker) {
        //Implement any additional code required for marker location changed

    }

    override fun onMarkerDragEnd(marker: Marker) {
        clearFields()
        propertyCoordinates.setText(marker.position.latitude.toString()+","+marker.position.longitude.toString())
    }

    override fun onMarkerDragStart(p0: Marker) {
        //If required to add any changes marker started dragging

    }
}