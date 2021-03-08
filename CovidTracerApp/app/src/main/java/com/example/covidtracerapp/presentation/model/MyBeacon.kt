package com.example.covidtracerapp.presentation.model

import com.example.covidtracerapp.Utils
import com.example.covidtracerapp.Utils.A
import com.example.covidtracerapp.Utils.B
import com.example.covidtracerapp.Utils.C
import com.example.covidtracerapp.Utils.T
import org.altbeacon.beacon.Identifier
import org.altbeacon.beacon.Beacon
import kotlin.math.pow

class MyBeacon(
    var distance: Double,
    var bluetoothAddress: String?,
    var bluetoothName: String?,
    var id1: Identifier
){
    override fun equals(other: Any?): Boolean {
        return id1 == (other as MyBeacon).id1
    }

    override fun hashCode(): Int {
        return id1.hashCode()
    }
}

fun Beacon.toMyBeacon() : MyBeacon {
    return MyBeacon(
        distance = findDistance(),
        bluetoothAddress = this.bluetoothAddress,
        bluetoothName = this.bluetoothName,
        id1 = this.id1
    )
}

fun Beacon.findDistance() : Double {
    return A * (rssi/T.toDouble()).pow(B) + C
}