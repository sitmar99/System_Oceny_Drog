package pl.polsl.drogi.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import pl.polsl.drogi.utils.Vector3D
import kotlin.math.abs


abstract class AccelerometerSensor(context: Context) : SensorEventListener {
    private val sensorManager: SensorManager
    private val accelerometerSensor: Sensor
    private val gravitySensor: Sensor
    private var sensorNeedsToSettle = true
    private var timeWhenSensorIsSettled: Long = 0
    private var acceleration: Vector3D
    private var gravity: Vector3D
    private var MOTION_TRIGGER: Vector3D

    fun start() {
        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(this, gravitySensor, SensorManager.SENSOR_DELAY_NORMAL)
        sensorNeedsToSettle = true
        val TEN_SECONDS_IN_MILLIS = (10 * 1000).toLong()
        timeWhenSensorIsSettled = System.currentTimeMillis() + TEN_SECONDS_IN_MILLIS
    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }

    fun significantMotionDetected(): Boolean {
        return abs(acceleration.project(gravity) - 1) > PROJECTION_SCALING_FACTOR_TRIGGER &&
                sensorSettled()
    }

    private fun sensorSettled(): Boolean {
        if (sensorNeedsToSettle) {
            val waitTimePassed = System.currentTimeMillis() > timeWhenSensorIsSettled
            if (waitTimePassed) {
                sensorNeedsToSettle = false
                Log.d(TAG, "sensorSettled: Wait time has passed")
            }
            return waitTimePassed
        }
        return true
    }

    override fun onSensorChanged(sensorEvent: SensorEvent) {
        val mySensor = sensorEvent.sensor
        if (mySensor.type == Sensor.TYPE_ACCELEROMETER) {
            val x = sensorEvent.values[0]
            val y = sensorEvent.values[1]
            val z = sensorEvent.values[2]
            setAcceleration(x, y, z)
        } else if (mySensor.type == Sensor.TYPE_GRAVITY) {
            val x = sensorEvent.values[0]
            val y = sensorEvent.values[1]
            val z = sensorEvent.values[2]
            setGravity(x, y, z)
        }
        onUpdate(acceleration, gravity)
    }

    override fun onAccuracyChanged(sensor: Sensor, i: Int) {}
    abstract fun onUpdate(a: Vector3D?, g: Vector3D?)

    // Getters and setters
    fun getAcceleration(): Vector3D {
        return acceleration
    }

    fun setAcceleration(acceleration: Vector3D) {
        this.acceleration = acceleration
    }

    private fun setAcceleration(x: Float, y: Float, z: Float) {
        acceleration = Vector3D(x, y, z)
    }

    fun getGravity(): Vector3D {
        return gravity
    }

    fun setGravity(gravity: Vector3D) {
        this.gravity = gravity
    }

    private fun setGravity(x: Float, y: Float, z: Float) {
        gravity = Vector3D(x, y, z)
    }

    open fun setTriggerLimit(limit: Float) {
        MOTION_TRIGGER = Vector3D(limit, limit, limit)
    }

    open fun getTriggerLimit(): Vector3D? {
        return MOTION_TRIGGER
    }

    companion object {
        private const val TAG = "AccelerometerSensor"
        private const val DEFAULT_MOTION_TRIGGER_LIMIT = 2f
        private const val PROJECTION_SCALING_FACTOR_TRIGGER = 0.2f
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
}