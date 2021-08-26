package pl.polsl.drogi.sensors

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat


@RequiresApi(Build.VERSION_CODES.P)
class LocalizationSensor(context: Context) : LocationListener {
    private val locationManager: LocationManager

     companion object {
         private const val MIN_TIME_BETWEEN_UPDATES: Long = 1000
         private const val MIN_DIST_BETWEEN_UPDATES = 2f
     }

     var lastLocation:Location? = null

     init {
         locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
         if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

             // TODO: Consider calling
             //    ActivityCompat#requestPermissions

             // here to request the missing permissions, and then overriding
             //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
             //                                          int[] grantResults)
             // to handle the case where the user grants the permission. See the documentation
             // for ActivityCompat#requestPermissions for more details.

         }
         lastLocation= locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)

     }

    fun start() {

        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    MIN_TIME_BETWEEN_UPDATES,
                    MIN_DIST_BETWEEN_UPDATES, this)
        } catch (e: SecurityException) {
            e.printStackTrace()

        }
    }

    fun stop() {
        locationManager.removeUpdates(this)
    }


    override fun onLocationChanged(location: Location) {
        this.lastLocation = location

    }

    override fun onStatusChanged(s: String?, i: Int, bundle: Bundle?) {}



}