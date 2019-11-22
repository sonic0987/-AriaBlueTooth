package com.norma.abc.module

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.norma.abc.module.BTClientViewModel.Companion.STATE_CONNECTED
import com.norma.abc.module.BTClientViewModel.Companion.STATE_CONNECTING
import com.norma.abc.module.BTClientViewModel.Companion.STATE_CONNECTION_FAILED
import com.norma.abc.module.BTClientViewModel.Companion.STATE_PAIR_REFUSED
import com.norma.abc.module.ariaChatModule.AriaMSGViewModel
import com.norma.abc.utils.bluetooth.model.BLEDTO
import java.io.*
import java.nio.charset.StandardCharsets
import java.util.*
import kotlin.properties.Delegates



open class BTClient() : Thread() {
    // Unique UUID for this application
    private val MY_UUID_SECURE = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66")
    private val MY_UUID_INSECURE = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66")

    private lateinit var btClientViewModel: BTClientViewModel
    private lateinit var ariaMsgViewModel: AriaMSGViewModel

    private lateinit var inputStream: InputStream
    private lateinit var outputStream: OutputStream
    private lateinit var  socket:BluetoothSocket

    private var connectionType by Delegates.notNull<Int>()

    constructor(context:Context, device: BLEDTO,secType:Boolean) : this() {//클라이언트
        connectionType = 1
        btClientViewModel = ViewModelProviders.of(context as AppCompatActivity).get(BTClientViewModel::class.java)
        ariaMsgViewModel  = ViewModelProviders.of(context).get(AriaMSGViewModel::class.java)
        btClientViewModel.listener(STATE_CONNECTING)
        btClientViewModel.imServer.postValue(false)
        socket = createBluetoothSocket(device.bleDevice,secType)
    }
    constructor(context:Context, socket: BluetoothSocket) : this() {//서버
        connectionType = 2
        btClientViewModel = ViewModelProviders.of(context as AppCompatActivity).get(BTClientViewModel::class.java)
        ariaMsgViewModel  = ViewModelProviders.of(context).get(AriaMSGViewModel::class.java)
        btClientViewModel.listener(STATE_CONNECTING)
        btClientViewModel.imServer.postValue(true)
        this.socket = socket
    }

    override fun run() {
        try {
            if(btClientViewModel.mReason.value != STATE_CONNECTED){
                if(connectionType==1){
                    socket.connect()
                    sleep(500)
                }
                btClientViewModel.connectState.postValue(true)

                btClientViewModel.listener(STATE_CONNECTED)
                connected()
            }else
                Log.e("BTClient:56","연결 문제?")
        } catch (e: IOException) {
            e.printStackTrace()
            btClientViewModel.listener(STATE_CONNECTION_FAILED)
        } catch (e: InterruptedException){
            e.printStackTrace()
            btClientViewModel.listener(STATE_CONNECTION_FAILED)
        }

    }
    fun secureKeySender(secKeyChain:ByteArray){
        send(secKeyChain)
    }
    private fun connected(){
        try {
            inputStream = socket.inputStream
            outputStream = socket.outputStream
            val buffer = ByteArray(2048)

            if(connectionType==2){//서버 클라이언트 측이면,
                sleep(1250)
                btClientViewModel.onSecureKeyEvent.postValue(ariaMsgViewModel.secureKeyEnroll())
            }else{//접속된 클라이언트 측이면,
                // SecureKeyTable 응답대기
                val buf = String(buffer,0,inputStream.read(buffer),StandardCharsets.UTF_8)
                val secKey = ariaMsgViewModel.setKey(buf)
                ariaMsgViewModel.secureKey.postValue(secKey)
            }

            while(socket.isConnected){  // 연결 끊기기 전까지 계속 Listen
                val buf = String(buffer,0,inputStream.read(buffer),StandardCharsets.UTF_8)
                btClientViewModel.readRemoteData.postValue(buf)

//                Log.e("BTClient","SecKey[bytes]: ${ariaMsgViewModel.secureKey.value?.contentToString()}")
            }


        } catch (e: IOException) {
            e.printStackTrace()
            close()
            btClientViewModel.listener(STATE_PAIR_REFUSED)  // 연결끊김
        }
    }

    fun send(dataBytes :ByteArray){ // 전송
        try{
            outputStream.write(dataBytes)
        }catch (e: IOException){
            e.printStackTrace()
            close()
            btClientViewModel.listener(STATE_PAIR_REFUSED)  // 연결끊김
        }
    }

    fun close(){
        if(btClientViewModel.mReason.value == STATE_CONNECTED) {
            btClientViewModel.connectState.postValue(false)

            inputStream.close()
            outputStream.close()
            socket.close()
        }
    }

    @Throws(IOException::class)
    private fun createBluetoothSocket(device: BluetoothDevice,secType:Boolean): BluetoothSocket {
        val type = if(secType)MY_UUID_SECURE else MY_UUID_INSECURE
            try {
                val m = device.javaClass.getMethod("createInsecureRfcommSocketToServiceRecord", *arrayOf<Class<*>>(UUID::class.java))
                return m.invoke(device, type) as BluetoothSocket
            } catch (e: Exception) {
                e.printStackTrace()
            }

        return device.createRfcommSocketToServiceRecord(type)
    }
}