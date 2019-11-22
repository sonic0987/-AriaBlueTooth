package com.norma.abc.ui.btConn

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.norma.abc.utils.bluetooth.model.BLEDTO
import io.reactivex.disposables.Disposable

class BTConnViewModel: ViewModel(){
    val btConnList: MutableLiveData<MutableSet<BluetoothDevice>> // Pair 되었었던 Bluetooth
    val scanBtList: MutableLiveData<MutableList<BLEDTO>> = MutableLiveData()// Scan 된 Bluetooth
    private val bluetoothAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

    init {
        btConnList = MutableLiveData<MutableSet<BluetoothDevice>>().apply{
            value = bluetoothAdapter.bondedDevices
            bluetoothAdapter.startDiscovery()
        }
        scanBtList.value = ArrayList()
    }

    fun onRefreshBluetoothData(){
        btConnList.apply{
            value = bluetoothAdapter.bondedDevices
            bluetoothAdapter.startDiscovery()
        }
    }

    fun addScanBt(ctx: Context, device:BluetoothDevice, rssi:Short){
        scanBtList.apply {
            value!!.add(BLEDTO(ctx, device, rssi.toInt(),null))
        }
    }

    fun onStopDiscovery(){
        if (bluetoothAdapter.isDiscovering)
            bluetoothAdapter.cancelDiscovery()
    }


    public override fun onCleared() {
        super.onCleared()
    }
}