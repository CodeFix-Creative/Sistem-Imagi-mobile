package com.imagi.app

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.imagi.app.model.UserLocation
import com.imagi.app.network.DbServices
import com.imagi.app.ui.base.CoreViewModel
import com.imagi.app.ui.home.MapsFragment
import com.imagi.app.ui.merchent.HomeFragment
import com.imagi.app.ui.merchent.StoreMerchant
import com.imagi.app.util.Constant
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import timber.log.Timber
import javax.inject.Inject
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity(), HasSupportFragmentInjector {

    private var myLocationPermissionGrated by Delegates.notNull<Boolean>()

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return fragmentInjector
    }

    @Inject
    lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: CoreViewModel
    lateinit var dbServices: DbServices

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbServices = DbServices(this)
        dbServices.mContext = this
        AndroidInjection.inject(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        setContentView(R.layout.activity_main)
//        setContentView(R.layout.activity_main)
        getPosition()

        addFragment(MapsFragment())

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNav.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

    }

    private fun getPosition(){
        Timber.d("TRY_GET_POSITION")
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    Timber.d("ONSTART")
                    Timber.d("GET_LAST_LOCATION : ${location.latitude}")
                    Timber.d("GET_LAST_LOCATION : ${location.longitude}")
                    getSharedPreferences(Constant.SP_NAME, MODE_PRIVATE).edit()
                        .putString(Constant.SP_LOCATION, Gson().toJson(UserLocation(latitude = location.latitude.toString(), longitude = location.longitude.toString())))
                        .apply()
                }
            }

        if (checkPermission()) {

        } else {
            requestPermission();
        }
    }

    private fun checkPermission(): Boolean {
        return if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            false
        } else if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            false
        }else if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            false
        } else{
            true
        }
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            200
        )
    }

    override fun onBackPressed() {
        finishAffinity()
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

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.nav_home -> {
                getPosition()
                val fragment = MapsFragment()
                addFragment(fragment)
                return@OnNavigationItemSelectedListener true
            }

            R.id.nav_merchant -> {
                getPosition()
                val fragment = MarketFragment()
                addFragment(fragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_profile -> {
                getPosition()
                val fragment = ProfilePage.newInstance()
                addFragment(fragment)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    private fun addFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
//            .setCustomAnimations(R.anim.design_bottom_sheet_slide_in, R.anim.design_bottom_sheet_slide_out)
            .replace(R.id.content, fragment, fragment.javaClass.getSimpleName())
            .addToBackStack(null)
            .commit()
    }

}