package com.norma.abc.module

import android.bluetooth.BluetoothAdapter
import android.graphics.Color
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.norma.abc.R
import com.norma.abc.utils.bluetooth.model.BLEDTO

class BTClientViewModel : ViewModel(){
    companion object{
        val STATE_LISTENING = 1 // 서버실행 대기
        val STATE_CONNECTING = 2
        val STATE_CONNECTED = 3
        val STATE_CONNECTION_FAILED = 4
        val STATE_ALREADY_CONNECTED = 6
        val STATE_PAIR_REFUSED = 6
        val STATE_LISTEN_END = 7 // 서버종료
    }
    var mReason: MutableLiveData<Int> = MutableLiveData()
    var nowConnecterLayout: MutableLiveData<View> = MutableLiveData()
    var clientThread: MutableLiveData<BTClient> = MutableLiveData()
    var nowDevice: MutableLiveData<BLEDTO> = MutableLiveData()

    var readRemoteData: MutableLiveData<String> = MutableLiveData()
    var connectState:MutableLiveData<Boolean> = MutableLiveData()

    // presenter 써야될것같은데...
    var onSecureKeyEvent: MutableLiveData<ByteArray> = MutableLiveData()
    var imServer:MutableLiveData<Boolean> = MutableLiveData()
    init{
        connectState.postValue(false)
        imServer.postValue(false)
    }
    fun listener(listen : Int){
        mReason.postValue(listen)
    }
    fun selectLayout(item: View,btClient:BTClient,btdto:BLEDTO){
        if(nowConnecterLayout.value != null && nowConnecterLayout.value != item) {
            nowConnecterLayout.value?.setBackgroundColor(Color.parseColor("#ffffff"))
            nowConnecterLayout.value?.findViewById<View>(R.id.conn_progress)?.visibility = View.GONE
        }

        nowConnecterLayout.apply {
            value = item
        }
        clientThread.apply {
            value = btClient
        }
        nowDevice.apply {
            value = btdto
        }
    }

    public override fun onCleared() {
        super.onCleared()
        clientThread.value?.close()
        clientThread.value?.interrupt()
    }
}