package com.norma.abc

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.norma.abc.module.dto.NavigationHeaderDTO
import com.norma.abc.utils.bluetooth.model.BLEDTO

class MainActivityViewModel: ViewModel(){
    val navHeader: MutableLiveData<NavigationHeaderDTO> = MutableLiveData()
    val cryptSwitcher: MutableLiveData<Boolean> = MutableLiveData()
    val toastLive: LiveData<String>
    init {

        toastLive = MutableLiveData<String>().apply {
            value = "This is BlueTooth Fragment"
        }
        cryptSwitcher.apply {
            value = true
        }
    }

    fun onHeaderChange(dto: NavigationHeaderDTO){
        navHeader.postValue(dto)
    }


    public override fun onCleared() {
        super.onCleared()
    }
}