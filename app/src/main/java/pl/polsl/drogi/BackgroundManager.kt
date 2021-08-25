package pl.polsl.drogi

import android.content.Context
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import org.json.JSONObject
import pl.polsl.drogi.sensors.AccelerometerSensor
import pl.polsl.drogi.utils.Vector3D
import java.lang.Exception

object BackgroundManager {

    lateinit var accelerometerSensor: AccelerometerSensor
    var odpowiedz = ""
    var context :Context? = null
    private val accSubscribers=  mutableListOf<(Vector3D) -> Unit>()

    fun notifyAccSubs(vector3D: Vector3D) {
        accSubscribers.forEach {
            it.invoke(vector3D)
        }
    }

    fun subscribeAcc(func:(Vector3D)->Unit) {
        accSubscribers.add(func)
    }

    init {

    }

    fun veryLateInit(context:Context) {
        this.context = context
        accelerometerSensor = AccelerometerSensor(context);
        accelerometerSensor.start()

//        this.subscribeAcc { sendRequest(it) }
    }

    fun sendRequest(v:Vector3D) {
        try{
            val queue = Volley.newRequestQueue(context)
            val url = "https://workmanagementsystemtab.azurewebsites.net/Authorization/login"

            val xd:JSONObject = JSONObject()
            xd.put("email","string")
            xd.put("password","string")
// Request a string response from the provided URL.
            val stringRequest = JsonObjectRequest(
                Request.Method.POST, url, xd
                ,
                Response.Listener<JSONObject> { response ->
                    xx(v.y.toString() + response.toString())
                },
                Response.ErrorListener { response -> xx(response = response.toString()) })

// Add the request to the RequestQueue.
            queue.add(stringRequest)
        }catch (e:Exception){
            odpowiedz= e.toString()
        }
    }

    fun xx(response:String) {
        try{
            odpowiedz = "Response is: ${response.substring(0, 500)}"
        }catch (e:Exception) {
            odpowiedz = e.toString()
        }
    }
}