package com.imagi.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import timber.log.Timber
import kotlin.properties.Delegates

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    private lateinit var latitude: String
    private lateinit var longitude: String

    private var myLocationPermissionGrated by Delegates.notNull<Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        if(intent.extras != null)
        {
            Log.d("LOG_MAP", "PARSHING SUCCESS")
            val bundle = intent.extras
            if (bundle != null) {
                Log.d("LOG_MAP", "ADA BUNDLE")
                Timber.d("DATA LATITUDE ${bundle.getString("latitude").toString()}")
                if(bundle.containsKey("latitude")){
                    this.latitude = bundle.getString("latitude").toString()
                }
                if(bundle.containsKey("longitude")){
                    this.longitude = bundle.getString("longitude").toString()
                }
            }
        }else{
            Timber.d("FAIL_GET_DATA")
        }


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map1) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val zoomLevel = 16.0f
        // Add a marker in Sydney and move the camera
        var sydney = LatLng(-34.0, 151.0)
        try{
            sydney = LatLng(latitude.toDouble(), longitude.toDouble())
        }catch (e:Exception){

        }

        Log.d("LOG_MAP_LATITUDE", "$latitude")
        Log.d("LOG_MAP_LONGITUDE", "$longitude")
        mMap.addMarker(MarkerOptions().position(sydney).title(""))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, zoomLevel))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, zoomLevel))
    }

    private fun getLocationPermission() {
        myLocationPermissionGrated = false;
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            myLocationPermissionGrated = true
        }else{
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        myLocationPermissionGrated = false
       when(requestCode){
           1 -> {
               if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                   if ((ContextCompat.checkSelfPermission(
                           this,
                           Manifest.permission.ACCESS_FINE_LOCATION
                       ) == PackageManager.PERMISSION_GRANTED)
                   ) {
                       Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                       myLocationPermissionGrated = true
                   }
               } else {
                   Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
               }
           }
       }
    }

}