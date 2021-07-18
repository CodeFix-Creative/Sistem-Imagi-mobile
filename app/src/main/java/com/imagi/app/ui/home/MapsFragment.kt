package com.imagi.app.ui.home

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.imagi.app.DetailMarket
import com.imagi.app.R
import com.imagi.app.model.LocalMarker
import com.imagi.app.network.DbServices
import com.imagi.app.ui.base.CoreViewModel
import com.imagi.app.util.AppUtils
import dagger.android.support.AndroidSupportInjection
import timber.log.Timber
import javax.inject.Inject

class MapsFragment : Fragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: CoreViewModel
    private lateinit var dbServices: DbServices
    lateinit var progress : ProgressBar
    lateinit var currentView: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_maps, container, false)
        currentView = view
        dbServices.mContext  = context
        dbServices = DbServices(getContext())
        progress = view.findViewById(R.id.progressBarHome)
        return view
    }

    private val callback = OnMapReadyCallback { googleMap ->
        Timber.d("PANGGIL_STORE_")
        Timber.d("PANGGIL_STORE_${viewModel.localMarkerLiveData.value?.size}")
        val zoomLevel = 14.0f
        viewModel.data?.forEach { localMarker ->
            val marker = localMarker.latitude?.let { it1 -> localMarker.longitude?.let { it2 -> LatLng(it1, it2) } }
            googleMap.addMarker(MarkerOptions().position(marker).title("${localMarker.name}"))
            Timber.d("PANGGIL_STORE_${localMarker.name}")
            googleMap.setOnMarkerClickListener(markerClick(localMarker))
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker, zoomLevel))
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker, zoomLevel))
        }
    }

    private fun markerClick(localMarker : LocalMarker): GoogleMap.OnMarkerClickListener? {
        return GoogleMap.OnMarkerClickListener {
            Timber.d("ID_TOKO_${localMarker.id}")
            val bundle = Bundle()
            bundle.putString("id", localMarker.id)
            val intent = Intent(activity, DetailMarket::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
            true
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        if(viewModel.markerLiveData.value.isNullOrEmpty()){
        Timber.d("PANGGIL_MAP")
//        }
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(CoreViewModel::class.java)
        dbServices = DbServices(getContext())

        try{
            viewModel.getStoreByMap(dbServices.findBearerToken())
        }catch (e:Exception){
            Timber.d("${e.message}")
        }

        observerViewModel()

    }

    private fun observerViewModel(){

        viewModel.isShowLoader.observe(this, {
            if (it) {
                progress.visibility = View.VISIBLE
            } else {
                progress.visibility = View.GONE
            }
        })

        viewModel.errorMessage.observe(this, {
            activity?.let { it1 -> AppUtils.showAlert(it1, it) };
        })

        viewModel.storeLiveData.observe(this, {
            val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
            mapFragment?.getMapAsync(callback)
        })

    }


}