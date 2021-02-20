package com.example.covidtracerapp

import java.util.*

object Utils {
    const val A = 70.93558162
    const val B = 0.1968682809
    const val C = -75.73089564
    const val T = -51
    const val ALL_DEVICES_TOPIC = "all_devices"
    fun generateUidNamespace(): String {
        val randomUUID: String = UUID.randomUUID().toString()
        return randomUUID.subSequence(0, 8).toString() + randomUUID.substring(24, 36)
    }
}
