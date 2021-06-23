package pl.polsl.drogi.utils

import kotlin.math.abs


class Vector3D {
    private val x: Float
    private val y: Float
    private val z: Float

    constructor() {
        x = 0f
        y = 0f
        z = 0f
    }

    constructor(_x: Float, _y: Float, _z: Float) {
        x = _x
        y = _y
        z = _z
    }

    fun add(v: Vector3D): Vector3D {
        return Vector3D(x + v.x, y + v.y, z + v.z)
    }

    fun subtract(v: Vector3D): Vector3D {
        return Vector3D(x - v.x, y - v.y, z - v.z)
    }

    private fun dot(v: Vector3D): Float {
        return x * v.x + y * v.y + z * v.z
    }

    fun greaterThanAny(v: Vector3D): Boolean {
        return abs(x) > v.x || abs(y) > v.y || abs(z) > v.z
    }

    fun project(onToVector: Vector3D): Float {
        return dot(onToVector) / onToVector.dot(onToVector)
    }
}