package com.sky.media.image.core.extra

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.abs

class Accelerometer(context: Context) {
    private val mAccListener: SensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(sensorEvent: SensorEvent) {
            if (sensorEvent.sensor.type == 1) {
                val f = sensorEvent.values[0]
                val f2 = sensorEvent.values[1]
                if (abs(f) <= 3.0f && abs(f2) <= 3.0f) {
                    return
                }
                sRotation = if (abs(f) > abs(f2)) {
                    if (f > 0.0f) {
                        CLOCKWISE_ANGLE.Deg0
                    } else {
                        CLOCKWISE_ANGLE.Deg180
                    }
                } else if (f2 > 0.0f) {
                    CLOCKWISE_ANGLE.Deg90
                } else {
                    CLOCKWISE_ANGLE.Deg270
                }
            }
        }

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
    }
    private val mDefaultSensor: Sensor
    var isHasStart = false
        private set
    private var mSensorManager: SensorManager? = null

    enum class CLOCKWISE_ANGLE(val value: Int) {
        Deg0(0), Deg90(1), Deg180(2), Deg270(3);

    }

    fun start() {
        if (!isHasStart) {
            isHasStart = true
            mSensorManager!!.registerListener(mAccListener, mDefaultSensor, 3)
        }
    }

    fun stop() {
        if (isHasStart) {
            isHasStart = false
            mSensorManager!!.unregisterListener(mAccListener)
        }
    }

    companion object {
        private var sRotation = CLOCKWISE_ANGLE.Deg0
        val direction: Int
            get() = sRotation.value
    }

    init {
        mSensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mDefaultSensor = mSensorManager!!.getDefaultSensor(1)
        sRotation = CLOCKWISE_ANGLE.Deg0
    }
}