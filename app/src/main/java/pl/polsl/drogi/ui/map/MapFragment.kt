package pl.polsl.drogi.ui.map

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import org.json.JSONArray
import pl.polsl.drogi.BackgroundManager
import pl.polsl.drogi.R
import java.lang.Exception

class MapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

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

        return root
    }

    override fun onMapReady(googleMap: GoogleMap) {

        map = googleMap

        var myPlace = LatLng(0.0, 0.0)
        if (lastLocation != null) {
            myPlace = LatLng(lastLocation!!.latitude, lastLocation!!.longitude)
        }
        map.addMarker(MarkerOptions().position(myPlace).title("My location"))
        map.moveCamera(CameraUpdateFactory.newLatLng(myPlace))

        //TODO change url
        getJson(BackgroundManager.serverUrl + "/Coordinates")

        map.uiSettings.isZoomControlsEnabled = true
        map.setOnMarkerClickListener(this)

        setUpMap()
    }

    private fun getJson(url: String) {

        try {
            val queue = Volley.newRequestQueue(BackgroundManager.context)
            val stringRequest = JsonArrayRequest(
                com.android.volley.Request.Method.GET, url, null,
                { response ->
                    okResponse(response)
                },
                { response ->
                    errorResponse(response)
                })

            queue.add(stringRequest)

        } catch (e: Exception) {
            Toast.makeText(
                BackgroundManager.context,
                "Error occurred during sending request",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun okResponse(response: JSONArray) {

        for (i in 0 until response.length()) {
            val id = response.getJSONObject(i).getString("id")
            var lat = response.getJSONObject(i).getString("latitude").toDoubleOrNull()
            var long = response.getJSONObject(i).getString("longtitude").toDoubleOrNull()
            var score = response.getJSONObject(i).getString("score").toDoubleOrNull()

            if (lat == null || long == null) {
                lat = 52.0
                long = 21.0

            }
            map.addMarker(MarkerOptions().position(LatLng(lat, long)))
        }
    }

    private fun errorResponse(err: VolleyError?) {
        Toast.makeText(BackgroundManager.context, "Server timeout", Toast.LENGTH_LONG).show()
    }

    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(
                activity as Context,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        map.isMyLocationEnabled = true
    }

    override fun onMarkerClick(p0: Marker?) = false
}