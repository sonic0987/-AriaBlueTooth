package com.norma.abc.ui.home

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.norma.abc.MainActivity
import com.norma.abc.R
import com.norma.abc.module.BTClientViewModel
import com.norma.abc.module.ariaChatModule.ARIAMSGType
import com.norma.abc.module.ariaChatModule.AriaMSGViewModel
import com.norma.abc.module.dto.MSGDTO
import com.norma.abc.ui.adt.ARIAChatRecyclerAdapter
import com.norma.abc.utils.WrapContentLinearLayoutManager
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var ariaMsgViewModel: AriaMSGViewModel
    private lateinit var btClientViewModel: BTClientViewModel
    private lateinit var msgAdt: ARIAChatRecyclerAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
        viewModel_initialize()
        active()
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        menu?.findItem(R.id.action_serverHost)?.isVisible = false
    }

    private fun viewModel_initialize(){
        homeViewModel = ViewModelProviders.of(activity!!).get(HomeViewModel::class.java)
        ariaMsgViewModel = ViewModelProviders.of(activity!!).get(AriaMSGViewModel::class.java)
        btClientViewModel = ViewModelProviders.of(activity!!).get(BTClientViewModel::class.java)

        val layoutManager = WrapContentLinearLayoutManager(activity!!, LinearLayoutManager.VERTICAL, true)
        layoutManager.stackFromEnd = true
        recyclerview.apply {
            setLayoutManager(layoutManager)
            msgAdt = ARIAChatRecyclerAdapter(activity!!,ariaMsgViewModel)
            adapter = msgAdt

            ariaMsgViewModel.cnt.observe(viewLifecycleOwner, Observer {
                adapter?.notifyItemChanged(it)
            })

            btClientViewModel.apply {//상대방 채팅내용 집어넣기
                readRemoteData.observe(viewLifecycleOwner,Observer{
//                    Log.e("Chat","답장옴. ->  $it")
                    chatInsert(it, nowDevice.value?.mac?:"00:00:00:00:00:00",ARIAMSGType.LeftSimpleMessage,true)
                })
            }
        }

    }

    private fun active(){
        activity!!.findViewById<View>(R.id.fab).apply{
            visibility = View.VISIBLE
        }

        pushBtn.setOnClickListener {
            if(!TextUtils.isEmpty(senderET.text)){
                if((btClientViewModel.clientThread.value == null) or (ariaMsgViewModel.secureKey.value == null)){
                    Toast.makeText(context,"상대방과 연결이 안되어 있습니다.",Toast.LENGTH_SHORT).show()
                }else{
                    btClientViewModel.clientThread.value!!.send(ariaMsgViewModel.encryptSingle(senderET.text.toString()))
                    chatInsert(senderET.text.toString(),btClientViewModel.nowDevice.value?.mac?:"00:00:00:00:00:00",ARIAMSGType.RightSimpleImage,false)
                }
            }
        }
    }

    private fun chatInsert(text: String, mac: String, type:ARIAMSGType,cryptStatus:Boolean){
        ariaMsgViewModel.chatList.apply {
            val dto = MSGDTO(text, mac,type)
            dto.cryptStatus = cryptStatus
            value?.add(0,dto)

            view?.findViewById<RecyclerView>(R.id.recyclerview)?.adapter?.notifyItemInserted(0)
            view?.findViewById<RecyclerView>(R.id.recyclerview)?.smoothScrollToPosition(0)
        }
    }
}
