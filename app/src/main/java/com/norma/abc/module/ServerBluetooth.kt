package com.norma.abc.module

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import java.io.IOException
import java.util.*

class ServerBluetooth(private val context: Context, private val clientViewModel: BTClientViewModel, secure:Boolean) : Thread() {
    private val MY_UUID_SECURE = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66")
    private val MY_UUID_INSECURE = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66")

    private var serverSocket: BluetoothServerSocket? = null

    init{
        try {
            serverSocket = if(secure)
                                BluetoothAdapter.getDefaultAdapter()?.listenUsingRfcommWithServiceRecord("Server_sec", MY_UUID_SECURE)
                            else
                                BluetoothAdapter.getDefaultAdapter()?.listenUsingInsecureRfcommWithServiceRecord("Server_insec", MY_UUID_INSECURE)
            clientViewModel.mReason.value = BTClientViewModel.STATE_LISTENING

//            clientViewModel.apply {
//                mReason.observe(context as LifecycleOwner, androidx.lifecycle.Observer {
//                    if(it == BTClientViewModel.STATE_CONNECTED)
//                        cancel()
//                })
//            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun run() {
        var socket:BluetoothSocket? = null
        while ((socket == null) and (clientViewModel.mReason.value != BTClientViewModel.STATE_CONNECTED)) {
            try {
                Log.e(currentThread().name,"Server 접속 대기중")
                socket = serverSocket?.accept()!!
                Log.e(currentThread().name,"Server 접속됨")
                val client = BTClient(context,socket)
                clientViewModel.clientThread.postValue(client)
                client.start()
//                clientViewModel.mReason.postValue(BTClientViewModel.STATE_CONNECTED)
            } catch (e: IOException) {
                e.printStackTrace()
                break
            }
        }
        Log.e(currentThread().name,"Server 종료")
    }

    fun cancel() {
        try {
            serverSocket?.close()
            clientViewModel.mReason.value = BTClientViewModel.STATE_LISTEN_END
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
