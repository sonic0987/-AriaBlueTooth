package com.norma.abc.ui.adt

import android.bluetooth.BluetoothClass
import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders

import com.norma.abc.R
import com.norma.abc.module.BTClient
import com.norma.abc.module.BTClientViewModel
import com.norma.abc.module.BTClientViewModel.Companion.STATE_ALREADY_CONNECTED
import com.norma.abc.utils.bluetooth.model.BLEDTO

internal class BluetoothConnAdapter(private val ctx: Context) : BaseUIAdapter<BLEDTO>(ctx, R.layout.layout_bluetooth) {
    private val btClientViewModel: BTClientViewModel
    private lateinit var client:BTClient
    init {
        btClientViewModel = ViewModelProviders.of(ctx as AppCompatActivity).get(BTClientViewModel::class.java)
    }

    public override fun setSource(list: MutableList<BLEDTO>?) {
        super.setSource(list)
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
                    // 페어링
                    client = BTClient(ctx,item,true)
                    client.start()

                    //UI에 선택된것처럼 표시
                    btClientViewModel.selectLayout(it,client,item)
                }else
                    btClientViewModel.listener(STATE_ALREADY_CONNECTED)
            }
        return retView
    }
}
