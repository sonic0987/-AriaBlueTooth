package com.norma.abc.ui.btConn

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.*
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.norma.abc.MainActivityViewModel
import com.norma.abc.R
import com.norma.abc.module.BTClientViewModel
import com.norma.abc.module.BTClientViewModel.Companion.STATE_ALREADY_CONNECTED
import com.norma.abc.module.BTClientViewModel.Companion.STATE_CONNECTED
import com.norma.abc.module.BTClientViewModel.Companion.STATE_CONNECTING
import com.norma.abc.module.BTClientViewModel.Companion.STATE_CONNECTION_FAILED
import com.norma.abc.module.BTClientViewModel.Companion.STATE_LISTENING
import com.norma.abc.module.BTClientViewModel.Companion.STATE_LISTEN_END
import com.norma.abc.module.dto.NavigationHeaderDTO
import com.norma.abc.ui.adt.BluetoothConnAdapter
import com.norma.abc.ui.adt.BluetoothScanAdapter
import com.norma.abc.utils.LayoutUtils
import com.norma.abc.utils.NotificationManager
import com.norma.abc.utils.bluetooth.model.BLEDTO
import com.norma.abc.utils.rxpermission.RxPermission
import kotlinx.android.synthetic.main.fragment_btconn.*
import java.util.*



class BTConnFragment : Fragment() {
    private lateinit var btViewModel: BTConnViewModel
    private lateinit var btClientViewModel: BTClientViewModel
    private lateinit var activityViewModel: MainActivityViewModel
    private lateinit var rxPermissions: RxPermission


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_btconn, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        active()
        viewmodelInitialize()
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        menu?.findItem(R.id.action_serverHost)?.isVisible = true
    }

    private fun active(){
        rxPermissions = RxPermission(childFragmentManager)
        rxPermissions.apply {
            setLogging(true)
        }

        if (!BluetoothAdapter.getDefaultAdapter().isEnabled) {
            val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableIntent, 1)
        }else{
            val stateFilter = IntentFilter()
            stateFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED) //BluetoothAdapter.ACTION_STATE_CHANGED : 블루투스 상태변화 액션
            stateFilter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)
            stateFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED) //BT 연결 확인
            stateFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED) //BT 연결 끊김 확인
            stateFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
            stateFilter.addAction(BluetoothDevice.ACTION_FOUND)    //기기 검색됨
            stateFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)   //기기 검색 시작
            stateFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)  //기기 검색 종료
            stateFilter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST)  //페어링
            context?.registerReceiver(discoveryReceiver, stateFilter)

        }
    }

    private fun viewmodelInitialize(){
        Log.e(tag,"viewmodelInitialize")
        activity!!.findViewById<View>(R.id.fab).apply{
            visibility = View.GONE
        }
        activityViewModel = ViewModelProviders.of(activity!!).get(MainActivityViewModel::class.java)

        btViewModel = ViewModelProviders.of(activity!!).get(BTConnViewModel::class.java)
        btViewModel.apply {
            btScanListView.adapter = BluetoothScanAdapter(this@BTConnFragment.activity!!,this)

            btConnList.observe(this@BTConnFragment.activity!!, Observer {
                val datas = ArrayList<BLEDTO>()
                for(device in it){
                    Log.e("paired Device",device.address)
                    datas.add(BLEDTO(context,device,0,null))
                }
                with(btConnListView){
                    if(adapter != null)
                        (adapter as BluetoothConnAdapter).setSource(null)
                    adapter = BluetoothConnAdapter(this@BTConnFragment.activity!!)
                    (adapter as BluetoothConnAdapter).setSource(datas)
                    LayoutUtils.setListViewHeightBasedOnChildren(btConnListView)
                }
                if(btConnRefreshLayout.isRefreshing)
                    btConnRefreshLayout.isRefreshing = false
            })

            btConnRefreshLayout.setOnRefreshListener {
                onRefreshBluetoothData()
            }
        }

        btClientViewModel = ViewModelProviders.of(context as AppCompatActivity).get(BTClientViewModel::class.java)
        btClientViewModel.apply {
            this@BTConnFragment.activity?.let { it -> nowDevice
                mReason.observe(it, Observer{it1->
                    run {
//                        if(it.lifecycle.currentState == Lifecycle.State.RESUMED){
                            when (it1) {
                                STATE_LISTENING ->
                                    this@BTConnFragment.view?.let { it1 -> Snackbar.make(it1, "연결대기중...", Snackbar.LENGTH_SHORT).show() }
                                STATE_CONNECTING -> {
                                    this@BTConnFragment.view?.let { it1 -> Snackbar.make(it1, "연결중.", Snackbar.LENGTH_SHORT).show() }
                                }
                                STATE_CONNECTED ->{
                                    this@BTConnFragment.view?.let { it1 -> Snackbar.make(it1, "연결되었습니다.", Snackbar.LENGTH_SHORT).show() }
                                    NotificationManager.sendNotification(context,0,NotificationManager.Channel.MESSAGE,"Paired","연결되었습니다.")
                                    val vib = it.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        vib.vibrate(VibrationEffect.createWaveform(longArrayOf(100,100,100,100),VibrationEffect.DEFAULT_AMPLITUDE))
                                    } else {
                                        vib.vibrate(longArrayOf(100,100),VibrationEffect.DEFAULT_AMPLITUDE)
                                    }
                                }
                                STATE_CONNECTION_FAILED -> {
                                    this@BTConnFragment.view?.let { it1 -> Snackbar.make(it1, "연결실패!", Snackbar.LENGTH_SHORT).show() }
                                    nowDevice.value = null
                                    clientThread.value?.close()
                                    clientThread.value?.interrupt()
                                }
                                STATE_ALREADY_CONNECTED ->
                                    this@BTConnFragment.view?.let { it1 -> Snackbar.make(it1, "이미 연결된 Bluetooth 입니다.", Snackbar.LENGTH_SHORT).show() }
                                STATE_LISTEN_END ->{ }
                            }

                            nowConnecterLayout.observe(it, Observer { it2 ->
                                when (it1) {
                                    STATE_CONNECTING ->{
                                        it2.findViewById<View>(R.id.conn_progress).visibility = View.VISIBLE
                                    }
                                    STATE_CONNECTED -> {
                                        it2.findViewById<View>(R.id.conn_progress).visibility = View.GONE
                                        it2.setBackgroundColor(Color.parseColor("#cccccc"))
                                    }
                                    STATE_CONNECTION_FAILED ->{
                                        it2.findViewById<View>(R.id.conn_progress).visibility = View.GONE
                                        it2.setBackgroundColor(Color.parseColor("#ffffff"))
                                    }
                                }
                            })
//                        }
                    }
                })
                nowDevice.observe(it, Observer {
                    val data: NavigationHeaderDTO =
                        if(it == null)
                            NavigationHeaderDTO(null)
                        else
                            NavigationHeaderDTO(it)
                    activityViewModel.onHeaderChange(data)
                })
            }

        }
    }

    private fun onPermissionChanged(granted: Boolean?) {
        Log.i("RxPermissions", "Permissions" + (if (granted!!) "" else " not") + " Granted")
        if (granted) {
            // All requested permissions are granted

        } else {
            // At least one permission is denied
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        btClientViewModel.apply {
            mReason.value = 0
        }
        btViewModel.apply {
            onStopDiscovery()
        }
        context?.unregisterReceiver(discoveryReceiver)
    }

    private val discoveryReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action   //입력된 action
            Log.w("받은 액션", action)
            val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
            val rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, java.lang.Short.MIN_VALUE)

            val title: String
            //입력된 action에 따라서 함수를 처리한다
            when (action) {
                BluetoothAdapter.ACTION_STATE_CHANGED //블루투스의 연결 상태 변경
                -> {
                    when (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)) {
                        BluetoothAdapter.STATE_OFF -> { }
                        BluetoothAdapter.STATE_TURNING_OFF -> { }
                        BluetoothAdapter.STATE_ON -> { }
                        BluetoothAdapter.STATE_TURNING_ON -> { }
                    }
                }
                BluetoothDevice.ACTION_ACL_CONNECTED  //블루투스 기기 연결
                -> {
//                    title = if (device.name == null)
//                                device.address
//                            else
//                                device.name

//                    BTController.getInstance().setNowPairedDevice(BLEDTO(getContext(), device, rssi, null))
//                    Toast.makeText(context, String.format(getContext()!!.resources.getString(R.string.pairedThis), title), Toast.LENGTH_SHORT).show()
//                    if (BTController.getInstance().getOnPairingEventListener() != null)
//                        BTController.getInstance().getOnPairingEventListener().connected()
                }
                BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED // 블루투스기기 연결취소 요청
                -> {
//                    Toast.makeText(context, String.format(getContext()!!.resources.getString(R.string.requestDisconnect), BTController.getInstance().getNowPairedDevice().getSSID()), Toast.LENGTH_SHORT).show()
                }
                BluetoothDevice.ACTION_BOND_STATE_CHANGED -> {
                }
                BluetoothDevice.ACTION_ACL_DISCONNECTED   //블루투스 기기 끊어짐
                -> {
//                    Toast.makeText(context, String.format(getContext()!!.resources.getString(R.string.disconnectPair), BTController.getInstance().getNowPairedDevice().getSSID()),Toast.LENGTH_SHORT).show()
//                    if (BTController.getInstance().getOnPairingEventListener() != null)
//                        BTController.getInstance().getOnPairingEventListener().disconnected()
                }

                BluetoothAdapter.ACTION_DISCOVERY_STARTED //블루투스 기기 검색 시작
                -> {
                }
                BluetoothDevice.ACTION_FOUND  //블루투스 기기 검색 됨, 블루투스 기기가 근처에서 검색될 때마다 수행됨
                -> {
                    Log.e("BLE ACTION_FOUND","btScanAdtCount: ${(btScanListView.adapter as BluetoothScanAdapter).count}, btViewModelCount: ${btViewModel.scanBtList.value?.size}")
                    btViewModel.addScanBt(context,device,rssi)
                    (btScanListView.adapter as BluetoothScanAdapter).notifyDataSetChanged()
                    LayoutUtils.setListViewHeightBasedOnChildren(btScanListView)
                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED // 블루투스 기기 검색 종료
                -> Log.d("Bluetooth", "Call Discovery finished")
                BluetoothDevice.ACTION_PAIRING_REQUEST -> { // 연결요청
//                    Toast.makeText(context, String.format(getContext()!!.resources.getString(R.string.requestConnecting), BTController.getInstance().getNowPairedDevice().getSSID()), Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

}
