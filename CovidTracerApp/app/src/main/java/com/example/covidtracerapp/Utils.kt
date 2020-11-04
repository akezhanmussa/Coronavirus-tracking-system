package com.example.covidtracerapp

import java.util.*

object Utils {
    fun generateUidNamespace(): String {
        val randomUUID: String = UUID.randomUUID().toString()
        return randomUUID.subSequence(0, 8).toString() + randomUUID.substring(24, 36)
    }
}