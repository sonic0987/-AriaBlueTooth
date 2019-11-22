package com.norma.abc.module.ariaChatModule

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.norma.abc.module.dto.MSGDTO
import com.norma.abc.utils.ARIACBC
import com.norma.abc.utils.AriaCryptUtils
import com.norma.abc.utils.Utils
import kotlin.collections.ArrayList

class AriaMSGViewModel : ViewModel(){
    private var cbcSize = 192 + 16
    private val aria:ARIACBC = ARIACBC()
    var secureKey = MutableLiveData<ByteArray>()
    val chatList = MutableLiveData<MutableList<MSGDTO>>()
    val cnt = MutableLiveData<Int>()

    val key = byteArrayOf(// aria 알고리즘에서 사용 될 key, 암/복호화를 수행하기 위한 비밀키 정보
        0x01, 0x02, 0x03, 0x04,
        0x05, 0x06, 0x07, 0x08,
        0x09, 0x0A, 0x0B, 0x0C,
        0x0D, 0x0E, 0x0F, 0x10,
        0x01, 0x02, 0x03, 0x04,
        0x05, 0x06, 0x07, 0x08,
        0x09, 0x0A, 0x0B, 0x0C,
        0x0D, 0x0E, 0x0F, 0x11
    )
    private val iv = byteArrayOf(// aria 알고리즘에서 사용 될 iv, CBC운영모드에서 사용되는 정보
        0x01, 0x01, 0x01, 0x01,
        0x01, 0x01, 0x01, 0x01,
        0x01, 0x01, 0x01, 0x01,
        0x01, 0x01, 0x01, 0x01
    )

    init{
        chatList.value = ArrayList()
        secureKey =  MutableLiveData()

//        val rand = Random()
//        key.forEachIndexed { idx, _->
//            key[idx] = Utils.toByte(Integer.toHexString( rand.nextInt(0x01) + 0xFF))
//        }
    }

    fun encrypt(){
        chatList.apply {
            value?.forEachIndexed { index, it ->
                run {
                    if(!it.cryptStatus){
                        val cipherText = ByteArray(cbcSize)
                        val plainText = it.msg.toByteArray() // 암호화 하려는 메시지를 byte array 형식으로 가져옴
                        aria.CBC_192ENCRYPT(secureKey.value, iv, plainText, 0, plainText.size, cipherText, 0)

                        it.cryptStatus = true
                        it.chiper = it.msg
                        it.msg = AriaCryptUtils.byteArrayToHexString(cipherText)
                    }
                    cnt.value = index
                }
            }
        }
    }
    fun encryptSingle(msg:String):ByteArray{
        val cipherText = ByteArray(cbcSize)
        val plainText = msg.toByteArray() // 암호화 하려는 메시지를 byte array 형식으로 가져옴
        aria.CBC_192ENCRYPT(secureKey.value, iv, plainText, 0, plainText.size, cipherText, 0)

        return AriaCryptUtils.byteArrayToHexString(cipherText).toByteArray()
    }

    fun decrypt(){
        chatList.apply {
            value?.forEachIndexed { index, it ->
                run {
                    if(it.cryptStatus){
                        val decryptedText = ByteArray(cbcSize)
                        val cipherText = AriaCryptUtils.hexStringToByteArray(it.msg) // 복호화 하려는 메시지를 byte array 형식으로 가져옴
                        aria.CBC_192DECRYPT(secureKey.value, iv, cipherText, 0, cipherText.size, decryptedText, 0)

                        it.cryptStatus = false
                        it.msg = String(decryptedText).split("[\u000B\u0004\u0002�\u0010\u0005\\f\u0007\u0003\u001F\u000F\u000E]".toRegex()).toTypedArray()[0]
    //                    it.msg = String(decryptedText).trim()

    //                    val match = Pattern.compile("[\t\n\r\\v]").matcher(it.msg)
    //                    if(match.find())
    //                        Log.e("공백시작 번호","msg: ${it.msg}\n공백시작 위치:${match.start()}")
                    }
                    cnt.value = index
                }
            }
        }
    }

    fun setKey(keyChainStr: String): ByteArray {
        Log.e("SECKEY",keyChainStr)
        val keyChain = Utils.dec(keyChainStr)?.toByteArray()
        Log.e("SECKEY[Decryption]",keyChain?.contentToString())
        key.forEachIndexed { idx, _->
            key[idx] = keyChain?.get(idx)!!
        }
        return key
    }

    fun secureKeyEnroll():ByteArray{//키 교환 및, 키 등록
        // CBC key 그냥 던지면 글자 깨져서 디코딩 불가
        // 53784ae4e1f77b4a87ca0ff7ab78c9e3ddfc2d72183b31016664976ba609a5b1ddfc2d72183b31016664976ba609a5b1ddfc2d72183b31016664976ba609a5b1d9480eaf26901922c256e29fda351982d9480eaf26901922c256e29fda351982d9480eaf26901922c256e29fda351982d9480eaf26901922c256e29fda351982d9480eaf26901922c256e29fda351982d9480eaf26901922c256e29fda351982d9480eaf26901922c256e29fda351982d9480eaf26901922c256e29fda351982d9480eaf26901922c256e29fda351982b06cba6091d2b333a8d7ffcc73e1887b
        Log.e("SECKEY",key.contentToString())
        val keyChain = Utils.enc(key)!!.toByteArray()
        Log.e("SECKEY[Encryption]",keyChain.contentToString())
        secureKey.postValue(key)
        return keyChain
    }


}