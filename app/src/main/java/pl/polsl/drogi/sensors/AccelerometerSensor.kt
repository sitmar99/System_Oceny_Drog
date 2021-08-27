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

    private enum class CoordinateEnum {
        X, Y, Z
    }

    private val sensorManager: SensorManager
    private val accelerometerSensor: Sensor
    private val gravitySensor: Sensor
    private var sensorSettled = false
    private var acceleration: Vector3D
    private var gravity: Vector3D
    private var MOTION_TRIGGER: Vector3D

    private var lastAccValue: Vector3D = Vector3D()
    private var settledGravityValue: Vector3D = Vector3D()
    private var chosenCoordinate: CoordinateEnum = CoordinateEnum.Z;
    private var actitvationGuard: Boolean = true;

    public companion object {
        private const val DEFAULT_MOTION_TRIGGER_LIMIT = 2f
        private const val PROJECTION_SCALING_FACTOR_TRIGGER = 0.2f
        private const val G = 9.81f
        var TIME_START_BIAS = 3000L
        var TIME_AFTER_CHANGE_BIAS = 3000L
        var ACC_BIAS = 0.5f
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
        actitvationGuard = true;
        Timer().schedule(TIME_START_BIAS) {
            registerAfterSettle()
        }
    }

    private fun registerAfterSettle() {
        if (actitvationGuard) {

            sensorManager.registerListener(this, gravitySensor, SensorManager.SENSOR_DELAY_NORMAL)
            sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL)
            BackgroundManager.notifyStatusSubs(true)
            Timer().schedule(TIME_AFTER_CHANGE_BIAS) {
                afterInitSettle()
            }
        }

    }

    fun stop() {
        actitvationGuard = false;
        sensorManager.unregisterListener(this)
        BackgroundManager.notifyStatusSubs(false)
    }

    private fun afterInitSettle() {
        settledGravityValue = gravity
        chosenCoordinate = getCoordinate()
        sensorSettled = true
    }

    private fun getCoordinate(): CoordinateEnum {
        val subAbs = (settledGravityValue.abs() - Vector3D(G, G, G)).abs()
        var tmpCoordinate: CoordinateEnum = CoordinateEnum.X;
        var currentMin = subAbs.x

        if (subAbs.y < currentMin) {
            tmpCoordinate = CoordinateEnum.Y
            currentMin = subAbs.y
        }
        if (subAbs.z < currentMin) {
            tmpCoordinate = CoordinateEnum.Z
        }
        return tmpCoordinate
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
        if (significantMotionDetected() && !acceleration.epsilonEquals(lastAccValue, ACC_BIAS)) {
            lastAccValue = acceleration
            sensorSettled = false
            Timer().schedule(TIME_AFTER_CHANGE_BIAS) {
                sensorSettled = true
            }
            BackgroundManager.notifyAccSubs(evaluateScore())
        }
    }

    fun evaluateScore(): Float {
        return when (chosenCoordinate) {
            CoordinateEnum.X -> kotlin.math.abs(gravity.x - settledGravityValue.x)
            CoordinateEnum.Y -> kotlin.math.abs(gravity.y - settledGravityValue.y)
            CoordinateEnum.Z -> kotlin.math.abs(gravity.z - settledGravityValue.z)
        }
    }
}