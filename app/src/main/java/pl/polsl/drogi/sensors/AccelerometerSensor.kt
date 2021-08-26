//package pl.polsl.drogi.sensors
//
//import android.content.Context
//import android.hardware.Sensor
//import android.hardware.SensorEvent
//import android.hardware.SensorEventListener
//import android.hardware.SensorManager
//import pl.polsl.drogi.BackgroundManager
//
//
//import pl.polsl.drogi.utils.Vector3D
//import java.util.*
//import kotlin.concurrent.schedule
//
//
//class AccelerometerSensor(context: Context) : SensorEventListener {
//    private enum class CoordinateEnum{
//        X,Y,Z
//    }
//    private val sensorManager: SensorManager
//    private val gravitySensor: Sensor
//    private val accelerometerSensor: Sensor
//    private var sensorNeedsToSettle = true
//    private var timeWhenSensorIsSettled: Long = 0
//    private var gravity: Vector3D
//    private var MOTION_TRIGGER: Vector3D
//
//    private var lastSignificantValue: Vector3D = Vector3D()
//    private var settledValue: Vector3D = Vector3D()
//    private var chosenCoordinate:CoordinateEnum = CoordinateEnum.Z;
//
//    companion object {
//        private const val TAG = "AccelerometerSensor"
//        private const val DEFAULT_MOTION_TRIGGER_LIMIT = 2f
//        private const val PROJECTION_SCALING_FACTOR_TRIGGER = 0.2f
//        private const val TIME_START_BIAS = 3000L
//        private const val TIME_BIAS_AFTER_CHANGE = 500L
//        private const val G = 9.81f
//
//    }
//
//    private fun getCoordinate():CoordinateEnum{
//        val subAbs = (settledValue.abs() - Vector3D(G,G,G)).abs()
//        var tmpCoordinate:CoordinateEnum = CoordinateEnum.X;
//        var currentMin = subAbs.x
//
//        if(subAbs.y < currentMin) {
//            tmpCoordinate = CoordinateEnum.Y
//            currentMin = subAbs.y
//        }
//        if(subAbs.z < currentMin) {
//            tmpCoordinate = CoordinateEnum.Z
//        }
//        return tmpCoordinate
//    }
//
//    private fun getScore():Float  {
//        return when(chosenCoordinate) {
//            CoordinateEnum.X -> kotlin.math.abs(gravity.x - settledValue.x)
//            CoordinateEnum.Y -> kotlin.math.abs(gravity.y - settledValue.y)
//            CoordinateEnum.Z -> kotlin.math.abs(gravity.z - settledValue.z)
//        }
//    }
//
//    init {
//        gravity = Vector3D()
//        MOTION_TRIGGER = Vector3D(
//                DEFAULT_MOTION_TRIGGER_LIMIT,
//                DEFAULT_MOTION_TRIGGER_LIMIT,
//                DEFAULT_MOTION_TRIGGER_LIMIT
//        )
//
//        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
//        gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)
//        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
//    }
//
//    fun start() {
//
//        sensorNeedsToSettle = true
//        timeWhenSensorIsSettled = System.currentTimeMillis() + TIME_START_BIAS
//
//        Timer().schedule(TIME_START_BIAS) {
//            registerAfterSettle()
//            Timer().schedule(TIME_BIAS_AFTER_CHANGE){
//                afterInitSettle()
//            }
//        }
//    }
//
//    fun stop() {
//        sensorManager.unregisterListener(this)
//    }
//
//    private fun registerAfterSettle() {
//        sensorManager.registerListener(this, gravitySensor, SensorManager.SENSOR_DELAY_NORMAL)
//        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL)
//    }
//
//    private fun afterInitSettle() {
//        settledValue = gravity
//        chosenCoordinate = getCoordinate()
//
//    }
//
//    private fun significantMotionDetected(): Boolean {
//
//        return sensorSettled() && getScore() > 1.5f
//                //&&!lastSignificantValue.epsilonEquals(gravity,2.5f)
//    }
//
//    private fun sensorSettled(): Boolean {
//        if (sensorNeedsToSettle) {
//            if (System.currentTimeMillis() > timeWhenSensorIsSettled) {
//                sensorNeedsToSettle = false
//                lastSignificantValue = gravity
//                return true
//            }
//            return false
//        }
//        return true
//    }
//
//    override fun onSensorChanged(sensorEvent: SensorEvent) {
//        val mySensor = sensorEvent.sensor
//
//        if (mySensor.type == Sensor.TYPE_ACCELEROMETER) {
//            val x = sensorEvent.values[0]
//            val y = sensorEvent.values[1]
//            val z = sensorEvent.values[2]
//
//            gravity = Vector3D(x,y,z)
//        }
//        BackgroundManager.notifyAccSubs(gravity)
//
//        if(significantMotionDetected()) {
//            lastSignificantValue = gravity
//            sensorNeedsToSettle = true;
//            timeWhenSensorIsSettled = System.currentTimeMillis() + TIME_BIAS_AFTER_CHANGE
//        }
//    }
//
//
//
//    override fun onAccuracyChanged(sensor: Sensor, i: Int) {}
//
//}
package pl.polsl.drogi.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import pl.polsl.drogi.BackgroundManager


import pl.polsl.drogi.utils.Vector3D
import java.util.*
import kotlin.concurrent.schedule
import kotlin.math.abs


class AccelerometerSensor(context: Context) : SensorEventListener {
    private val sensorManager: SensorManager
    private val accelerometerSensor: Sensor
    private val gravitySensor: Sensor
    private var sensorSettled = true
    private var acceleration: Vector3D
    private var gravity: Vector3D
    private var MOTION_TRIGGER: Vector3D

    private var lastAccValue: Vector3D = Vector3D()

    companion object {
        private const val TAG = "AccelerometerSensor"
        private const val DEFAULT_MOTION_TRIGGER_LIMIT = 2f
        private const val PROJECTION_SCALING_FACTOR_TRIGGER = 0.2f
        private const val TIME_START_BIAS = 3000L
        private const val TIME_BIAS_AFTER_CHANGE = 1000L
    }

    init {
        acceleration = Vector3D()
        gravity = Vector3D()
        MOTION_TRIGGER = Vector3D(
                DEFAULT_MOTION_TRIGGER_LIMIT,
                DEFAULT_MOTION_TRIGGER_LIMIT,
                DEFAULT_MOTION_TRIGGER_LIMIT
        )

        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)
    }

    fun start() {
        Timer().schedule(TIME_START_BIAS) {
            registerAfterSettle()
        }
    }

    private fun registerAfterSettle() {
        sensorManager.registerListener(this, gravitySensor, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }

    fun significantMotionDetected(): Boolean {
        return abs(acceleration.project(gravity) - 1) > PROJECTION_SCALING_FACTOR_TRIGGER
                && sensorSettled
    }

    override fun onSensorChanged(sensorEvent: SensorEvent) {
        val mySensor = sensorEvent.sensor
        if (mySensor.type == Sensor.TYPE_ACCELEROMETER) {
            val x = sensorEvent.values[0]
            val y = sensorEvent.values[1]
            val z = sensorEvent.values[2]
            acceleration = Vector3D(x, y, z)
        } else if (mySensor.type == Sensor.TYPE_GRAVITY) {
            val x = sensorEvent.values[0]
            val y = sensorEvent.values[1]
            val z = sensorEvent.values[2]
            gravity = Vector3D(x, y, z)
        }
        onUpdate()
    }

    override fun onAccuracyChanged(sensor: Sensor, i: Int) {}

    private fun onUpdate() {
        if(significantMotionDetected() && !acceleration.epsilonEquals(lastAccValue,1f)) {
            lastAccValue = acceleration
            BackgroundManager.notifyAccSubs(acceleration)
            sensorSettled = false

            Timer().schedule(TIME_BIAS_AFTER_CHANGE) {
                sensorSettled = true
            }
        }
    }

}