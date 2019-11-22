package com.norma.abc.module.dto

import com.norma.abc.module.ariaChatModule.ARIAMSGType

class MSGDTO(var msg: String, var mac: String, var chatType: ARIAMSGType){
    var chiper:String = ""
    var cryptStatus:Boolean = false
}
