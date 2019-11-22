package com.norma.abc.utils.bluetooth;

import android.bluetooth.BluetoothClass;

public enum BluetoothServiceType {

    AUDIO(BluetoothClass.Service.AUDIO),
    CAPTURE(BluetoothClass.Service.CAPTURE),
    INFORMATION(BluetoothClass.Service.INFORMATION),
    LIMITED_DISCOVERABILITY(BluetoothClass.Service.LIMITED_DISCOVERABILITY),
    NETWORKING(BluetoothClass.Service.NETWORKING),
    OBJECT_TRANSFER(BluetoothClass.Service.OBJECT_TRANSFER),
    POSITIONING(BluetoothClass.Service.POSITIONING),
    RENDER(BluetoothClass.Service.RENDER),
    TELEPHONY(BluetoothClass.Service.TELEPHONY);

    private int code;

    BluetoothServiceType(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }
}
