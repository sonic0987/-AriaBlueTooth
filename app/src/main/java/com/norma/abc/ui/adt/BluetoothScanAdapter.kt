package com.norma.abc.ui.adt

import android.bluetooth.BluetoothClass
import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

import com.norma.abc.R
import com.norma.abc.module.BTClient
import com.norma.abc.module.BTClientViewModel
import com.norma.abc.module.BTClientViewModel.Companion.STATE_ALREADY_CONNECTED
import com.norma.abc.ui.btConn.BTConnViewModel
import com.norma.abc.utils.LayoutUtils
import com.norma.abc.utils.bluetooth.model.BLEDTO

internal class BluetoothScanAdapter(private val ctx: Context, vm: BTConnViewModel) : BaseAsyncUIAdapter<BLEDTO>(ctx, R.layout.layout_bluetooth,vm.scanBtList.value) {
    private val btClientViewModel: BTClientViewModel
    private lateinit var client:BTClient
    init {
        btClientViewModel = ViewModelProviders.of(ctx as AppCompatActivity).get(BTClientViewModel::class.java)
    }

    override fun onBindView(view: View, item: BLEDTO) {
        (view.findViewById<View>(R.id.name_saved) as TextView).text = item.ssid
        if (item.majorType == BluetoothClass.Device.Major.PHONE)
            view.findViewById<View>(R.id.image).visibility = View.VISIBLE
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val retView = super.getView(position, convertView, parent)

        retView.findViewById<View>(R.id.motherLayout)
            .setOnClickListener {
                val item = getItemPosition(position)

                if(btClientViewModel.nowDevice.value?.mac != item.mac){
                    if(::client.isInitialized) {
                        Log.e(client.toString(),"isInitialized!.. - > close!")
                        client.close()
                    }
                    client = BTClient(ctx,item,true)
                    client.start()
                    btClientViewModel.selectLayout(it,client,item)
                }else
                    btClientViewModel.listener(STATE_ALREADY_CONNECTED)
            }
        return retView
    }
}
