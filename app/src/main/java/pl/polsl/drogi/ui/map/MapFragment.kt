package pl.polsl.drogi.ui.map

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import pl.polsl.drogi.BackgroundManager
import pl.polsl.drogi.R
import java.io.IOException
import okhttp3.*



class MapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private val client = OkHttpClient()
    lateinit var result: String

    private lateinit var map: GoogleMap
    private var lastLocation: Location? = null

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_map, container, false)
        lastLocation = BackgroundManager.localizationSensor.lastLocation

        val mapFragment = childFragmentManager?.findFragmentById(R.id.map)
                as SupportMapFragment
        mapFragment.getMapAsync(this)

//            val mMapFragment = SupportMapFragment.newInstance()
//            val fragmentTransaction: FragmentTransaction = childFragmentManager.beginTransaction()
//            fragmentTransaction.add(R.id.map, mMapFragment)
//            fragmentTransaction.commit()
//            mMapFragment.getMapAsync(this)
        return root
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        var myPlace = LatLng(0.0, 0.0)
        if (lastLocation != null) {
            myPlace = LatLng(lastLocation!!.latitude, lastLocation!!.longitude)
        }
        map.addMarker(MarkerOptions().position(myPlace).title("My Favorite City"))
        map.moveCamera(CameraUpdateFactory.newLatLng(myPlace))

        getJson("https://reqbin.com/echo/get/json")
        addPoints()

        map.uiSettings.isZoomControlsEnabled = true
        map.setOnMarkerClickListener(this)

        setUpMap()
    }

    private fun addPoints() {
        var latitude = 370.0
        val regex = "latitude\": -*[0-9]*.[0-9]*|longtitude\": -*[0-9]*.[0-9]*".toRegex()

        val match = regex.findAll(result)
        for (cord in match) {
            println(cord.value)
            val negative = "-".toRegex()
            val pos = "[0-9]+.[0-9]+".toRegex()

            var value = pos.find(cord.value)!!.value.toDouble()
            if (negative.find(cord.value)?.value == "-") {
                value = -value
            }

            if (latitude == 370.0) {
                latitude = value
            }
            else {
                map.addMarker((MarkerOptions().position(LatLng(latitude, value))))
                latitude = 370.0
            }
        }
    }

    private fun getJson(url: String) {
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}
            override fun onResponse(call: Call, response: Response) = assign(response.body()?.string())
        })
    }

    private fun assign(s: String?) {
        result = s!!
        }

    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(activity as Context,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }

        map.isMyLocationEnabled = true
    }

    override fun onMarkerClick(p0: Marker?) = false
}