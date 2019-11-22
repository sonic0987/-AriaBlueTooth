package com.norma.abc.module.dto

import androidx.annotation.DrawableRes

import com.norma.abc.R
import com.norma.abc.utils.bluetooth.model.BLEDTO

class NavigationHeaderDTO(private var bleDto: BLEDTO?) {
    val getHead: String
        get() = if (bleDto != null) bleDto!!.ssid else "Bluetooth Not Paired"

    val getStatus: String
        get() = if (bleDto != null) bleDto!!.mac else "Bluetooth에 연결해 주세요."

    val getStatusImage: Int @DrawableRes
        get() = if (bleDto != null) R.drawable.ic_pair_bt else R.drawable.ic_dispair_bt
}
