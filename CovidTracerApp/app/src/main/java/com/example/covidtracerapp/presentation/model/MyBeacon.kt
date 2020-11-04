package com.example.covidtracerapp.presentation.model

import org.altbeacon.beacon.Identifier
import org.altbeacon.beacon.Beacon

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
        distance = this.distance * 100,
        bluetoothAddress = this.bluetoothAddress,
        bluetoothName = this.bluetoothName,
        id1 = this.id1
    )
}