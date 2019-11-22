package com.norma.abc.ui.adt

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup

import androidx.recyclerview.widget.RecyclerView
import com.norma.abc.R
import com.norma.abc.module.ariaChatModule.ARIAMSGType
import com.norma.abc.module.ariaChatModule.AriaMSGViewModel
import com.norma.abc.module.ariaChatModule.holder.MSGHolder
import com.norma.abc.module.dto.MSGDTO

class ARIAChatRecyclerAdapter(private val ctx: Context,private val vm: AriaMSGViewModel) :
    RecyclerView.Adapter<MSGHolder>() {

    override fun getItemViewType(position: Int): Int {
        vm.chatList.apply {
            return when(value?.get(position)?.chatType){
                ARIAMSGType.LeftSimpleMessage ->
                    1
                ARIAMSGType.RightSimpleImage ->
                    2
                else ->
                    0
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MSGHolder {
        val viewHolder: RecyclerView.ViewHolder

        viewHolder = when (viewType) {
            1 -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_left_tv, parent, false)
                MSGHolder(view)
            }
            2 -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_right_tv, parent, false)
                MSGHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_head_tv, parent, false)
                MSGHolder(view)
            }
        }

        return viewHolder
    }

    override fun onBindViewHolder(holder: MSGHolder, position: Int) {
        vm.chatList.apply {
            var dto = value?.get(position)
            holder.tv.text = dto?.msg ?: ""
        }
    }

    override fun onViewRecycled(holder: MSGHolder) {
        super.onViewRecycled(holder)
    }

    override fun getItemCount(): Int {
        Log.e("adtSize","${vm.chatList.value?.size}")
        return vm.chatList.value?.size ?: 0
    }
}
