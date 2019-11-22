package com.norma.abc

import android.os.Bundle
import android.service.autofill.Validators.not
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import com.norma.abc.module.BTClientViewModel
import com.norma.abc.module.ServerBluetooth
import com.norma.abc.module.ariaChatModule.AriaMSGViewModel
import com.norma.abc.ui.btConn.BTConnViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import com.norma.abc.utils.BackPressExitHandler
import com.norma.abc.utils.NotificationDialog
import com.norma.abc.utils.SecureKeyChainDialog
import kotlinx.android.synthetic.main.nav_header_main.view.*


class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var viewModel: MainActivityViewModel
    private lateinit var ariaMsgViewModel: AriaMSGViewModel
    private lateinit var clientViewModel: BTClientViewModel
    private lateinit var connectViewModel: BTConnViewModel

    private lateinit var secServer:ServerBluetooth
    private lateinit var insecServer:ServerBluetooth

    private var navController: NavController? = null
    private var backPressExitHandler: BackPressExitHandler? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        active()
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController!!.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun active(){
        backPressExitHandler = BackPressExitHandler(this)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_bt_scan
            ), drawer_layout
        )

        navController = findNavController(R.id.nav_host_fragment)
        setupActionBarWithNavController(navController!!, appBarConfiguration)
        nav_view.apply{
            itemIconTintList=null
            setupWithNavController(navController!!)
        }

        viewModel = ViewModelProviders.of(this).get(MainActivityViewModel::class.java)
        ariaMsgViewModel = ViewModelProviders.of(this).get(AriaMSGViewModel::class.java)
        clientViewModel = ViewModelProviders.of(this).get(BTClientViewModel::class.java)
        connectViewModel = ViewModelProviders.of(this).get(BTConnViewModel::class.java)

        viewModel.apply {
            navHeader.observe(this@MainActivity, Observer {dto ->
                nav_view.getHeaderView(0).apply{
                    imageView.setImageDrawable(ContextCompat.getDrawable(this@MainActivity,dto.getStatusImage))
                    nav_head.text = dto.getHead
                    nav_sub.text = dto.getStatus
                }
            })
        }
        clientViewModel.apply {
            mReason.observe(this@MainActivity,Observer{
                if(it == BTClientViewModel.STATE_PAIR_REFUSED)
                    Snackbar.make(findViewById(android.R.id.content), "Socket 연결이 끊어졌습니다.", Snackbar.LENGTH_SHORT).show()
                else if(it == BTClientViewModel.STATE_LISTEN_END){
                    if(!(connectState.value as Boolean))
                        Snackbar.make(findViewById(android.R.id.content), "서버를 종료합니다.", Snackbar.LENGTH_SHORT).show()
                }else if(it == BTClientViewModel.STATE_CONNECTED){//연결되었는데, Server실행중일경우 Server 제거
                    if(::secServer.isInitialized && ::insecServer.isInitialized ){
                        if(secServer.isAlive)
                            secServer.cancel()
                        if(insecServer.isAlive)
                            insecServer.cancel()
                    }
                }
            })
        }
        ariaMsgViewModel.secureKey.observe(this@MainActivity,Observer{
            SecureKeyChainDialog.Builder(this@MainActivity).setByteKey(ariaMsgViewModel.key).setOnButtonClickListener(object :
                SecureKeyChainDialog.OnClickListener {
                override fun submit(dialog: SecureKeyChainDialog) {
                    if(clientViewModel.imServer.value as Boolean){//서버일경우
                        clientViewModel.clientThread.value?.secureKeySender(clientViewModel.onSecureKeyEvent.value!!)

                        NotificationDialog.Builder(this@MainActivity).setTitle("성공").setMessage("SecureKey Table을 전송했습니다.").setOnButtonClickListener(
                            object : NotificationDialog.OnClickListener {
                                override fun submit(dialog: NotificationDialog) {
                                    dialog.dismiss()
                                }
                            }).build()!!.show()

                    }else//클라이언트일경우
                        Snackbar.make(findViewById(android.R.id.content), "SecureKey Table 교환 성공", Snackbar.LENGTH_SHORT).show()

                    dialog.dismiss()
                }

                override fun cancel(dialog: SecureKeyChainDialog) {
                    if(!clientViewModel.imServer.value!!){
                        Snackbar.make(findViewById(android.R.id.content), "SecureKey Table 교환 성공", Snackbar.LENGTH_SHORT).show()
                    }
                    dialog.dismiss()
                }
            }).build()!!.show()
        })
        fab.setOnClickListener { view ->
            viewModel.cryptSwitcher.apply {
                if(ariaMsgViewModel.chatList.value?.size!!>0){
                    val txt:String
                    if(value as Boolean){
                        ariaMsgViewModel.encrypt()
                        txt = "암호화 되었습니다."
                        value = false
                    } else{
                        ariaMsgViewModel.decrypt()
                        txt = "복호화 되었습니다."
                        value = true
                    }
                    Snackbar.make(view,txt, Snackbar.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this@MainActivity,"암/복호화를 위해 1개 이상의 채팅이 필요합니다.",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.action_serverHost->{

                if(!::secServer.isInitialized){
                    connectViewModel.onStopDiscovery()
                    secServer = ServerBluetooth(this,clientViewModel,true)
                    secServer.start()
//                    insecServer = ServerBluetooth(this,clientViewModel,false)
//                    insecServer.start()
                } else{
                    if(secServer.isAlive)
                        secServer.cancel()
                    else{
                        connectViewModel.onStopDiscovery()
                        secServer = ServerBluetooth(this,clientViewModel,true)
                        secServer.start()
                    }

//                    if(insecServer.isAlive)
//                        insecServer.cancel()
//                    else{
//                        connectViewModel.onStopDiscovery()
//                        insecServer = ServerBluetooth(this,clientViewModel,false)
//                        insecServer.start()
//                    }
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onBackPressed() {
        backPressExitHandler!!.onBackPressed()
    }
}
