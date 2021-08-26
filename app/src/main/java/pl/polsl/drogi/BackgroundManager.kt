package pl.polsl.drogi

import android.annotation.SuppressLint
import android.content.Context
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import pl.polsl.drogi.sensors.AccelerometerSensor
import pl.polsl.drogi.sensors.LocalizationSensor
import java.lang.Exception
typealias scoreType = Float
@SuppressLint("StaticFieldLeak")
object BackgroundManager {

    const val serverPostUrl = "https://webhook.site/03a1d307-a37a-463d-bd8f-c5b49543e988"

    lateinit var accelerometerSensor: AccelerometerSensor
    lateinit var localizationSensor: LocalizationSensor
    var context :Context? = null
    private val accSubscribers=  mutableListOf<(scoreType) -> Unit>()
    private var statusChangedSubscribers = mutableListOf<(Boolean) -> Unit>()
    var status:Boolean = false
    init {}

    fun start() {
        accelerometerSensor.start()
        localizationSensor.start()
        status = true
    }

    fun stop() {
        localizationSensor.stop()
        accelerometerSensor.stop()
        status = false
    }

    fun veryLateInit(context:Context) {
        this.context = context
        accelerometerSensor = AccelerometerSensor(context);
        localizationSensor = LocalizationSensor(context)
        this.subscribeAcc { sendRequest(it) }
    }

    fun notifyAccSubs(score:scoreType) {
        accSubscribers.forEach {
            it.invoke(score)
        }
    }

    fun notifyStatusSubs(activeStatus:Boolean) {
        statusChangedSubscribers.forEach{
            it.invoke(activeStatus)
        }
    }

    fun subscribeAcc(func:(scoreType)->Unit) {
        accSubscribers.add(func)
    }

    fun subscribeStatusChanged(func:(Boolean)->Unit) {
        statusChangedSubscribers.add(func)
    }

    fun sendRequest(score:scoreType) {
        val localization = localizationSensor.lastLocation ?: return

        try{
            val queue = Volley.newRequestQueue(context)
            val jsonObject:JSONObject = JSONObject()
            jsonObject.put("latitude",localization.latitude)
            jsonObject.put("longitude",localization.longitude)
            jsonObject.put("score",score)

            val stringRequest = JsonObjectRequest(
                Request.Method.POST, serverPostUrl, jsonObject
                ,
                Response.Listener<JSONObject> { response ->
                    okResponse(response)
                },
                Response.ErrorListener { response -> errorResponse(response) })

            queue.add(stringRequest)
        }
        catch (e:Exception){
            val yyy = e.toString()
        }
    }

    private fun okResponse(s:JSONObject?) {
        //TODO
        val xxxxx = s
    }

    private  fun errorResponse(s:VolleyError?) {
        //TODO
        val x=2
    }

}