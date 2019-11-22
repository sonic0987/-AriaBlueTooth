package com.norma.abc.utils.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;

import com.norma.abc.R;
import com.norma.abc.utils.bluetooth.model.BLEDTO;
import com.norma.abc.utils.bluetooth.model.VendorDTO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static android.bluetooth.BluetoothDevice.DEVICE_TYPE_CLASSIC;
import static android.bluetooth.BluetoothDevice.DEVICE_TYPE_DUAL;
import static android.bluetooth.BluetoothDevice.DEVICE_TYPE_LE;
import static android.bluetooth.BluetoothDevice.DEVICE_TYPE_UNKNOWN;

public class BTSupporter {
    public static String[] FLAGS = new String[]{"LE and BR/ERD Capable(Host)","LE and BR/ERD Capable(Controller)","BR/EDR Not Supported","LE General Discoverable Mode","LE Limited Discoverable Mode"};
    public static Set<BluetoothServiceType> getBluetoothDeviceKnownSupportedServices(BluetoothDevice device) {
        Set<BluetoothServiceType> mServiceSet;
        final Set<BluetoothServiceType> serviceSet = new HashSet<>();
        for (final BluetoothServiceType service : BluetoothServiceType.values()) {
            if (device.getBluetoothClass().hasService(service.getCode())) {
                serviceSet.add(service);
            }
        }
        mServiceSet = Collections.unmodifiableSet(serviceSet);

        return mServiceSet;
    }

    public static String vendorScan(String mac, ArrayList<VendorDTO> vendorList){
        String scanningMAC = mac.replaceAll(":","").substring(0, 6).toUpperCase();
        for(VendorDTO dto : vendorList){
            if(dto.mac.equals(scanningMAC)){
                return dto.vendor;
            }
        }
        return null;
    }


    public static String getDeviceType(Context ctx, int type){
        if(type == DEVICE_TYPE_UNKNOWN) {
            return ctx.getResources().getString(R.string.unknown);
        }else if(type == DEVICE_TYPE_CLASSIC) {
            return "Classic";
        }else if(type == DEVICE_TYPE_LE) {
            return "LE";
        }else if(type == DEVICE_TYPE_DUAL) {
            return "Classic & LE";
        }else
            return ctx.getResources().getString(R.string.notBT);
    }

    public static void isMacDuplicate(ArrayList<BLEDTO> btDto , String findMac){
        int[] duplite = new int[btDto.size()];
        for(int i=0; i<btDto.size(); i++){
            if(btDto.get(i).getMac().equals(findMac)) {
                duplite[i] += 1;
                Log.e("BT_Duplite","SSID: "+btDto.get(i).getSSID()+", MAC: "+btDto.get(i).getMac()+", FindMAC: "+findMac);
                break;
            }
        }
    }

    //https://distriqt.github.io/ANE-Bluetooth/asdocs/com/distriqt/extension/bluetooth/BluetoothClass.html
    public static String getMajorDeviceClass(int type){
        if(type == 1024)
            return "AUDIO_VIDEO";
        else if(type == 1076)
            return "CAMCORDER";
        else if(type == 1056)
            return "CAR_AUDIO";
        else if(type == 1032)
            return "HANDSFREE";
        else if(type == 1048)
            return "HEADPHONES";
        else if(type == 1064)
            return "HIFI_AUDIO";
        else if(type == 1044)
            return "LOUDSPEAKER";
        else if(type == 1040)
            return "MICROPHONE";
        else if(type == 1052)
            return "PORTABLE_AUDIO";
        else if(type == 1060)
            return "SET_TOP_BOX";
        else if(type == 1072)
            return "VIDEO_CAMERA";
        else if(type == 1068)
            return "VIDEO_VCR";
        else if(type == 1088)
            return "VIDEO_CONFERENCING";
        else if(type == 1084)
            return "VIDEO_DISPLAY_AND_LOUDSPEAKER";
        else if(type == 1096)
            return "VIDEO_GAMING_TOY";
        else if(type == 1080)
            return "VIDEO_MONITOR";
        else if(type == 1028)
            return "WEARABLE_HEADSET";
        else if(type == 256)
            return "COMPUTER";
        else if(type == 260)
            return "DESKTOP";
        else if(type == 272)
            return "HANDHELD_PC_PDA";
        else if(type == 268)
            return "LAPTOP";
        else if(type == 276)
            return "COMPUTER_PALM_SIZE_PC_PDA";
        else if(type == 264)
            return "COMPUTER_SERVER";
        else if(type == 280)
            return "COMPUTER_WEARABLE";
        else if(type == 2304)
            return "HEALTH";
        else if(type == 2308)
            return "HEALTH_BLOOD_PRESSURE";
        else if(type == 2332)
            return "HEALTH_DATA_DISPLAY";
        else if(type == 2320)
            return "HEALTH_GLUCOSE";
        else if(type == 2324)
            return "HEALTH_PULSE_OXIMETER";
        else if(type == 2328)
            return "HEALTH_PULSE_RATE";
        else if(type == 2312)
            return "HEALTH_THERMOMETER";
        else if(type == 2316)
            return "HEALTH_WEIGHING";
        else if(type == 1536)
            return "IMAGING";
        else if(type == 0)
            return "MISC";
        else if(type == 768)
            return "NETWORKING";
        else if(type == 1280)
            return "PERIPHERAL";
        else if(type == 512)
            return "PHONE";
        else if(type == 516)
            return "PHONE_CELLULAR";
        else if(type == 520)
            return "PHONE_CORDLESS";
        else if(type == 532)
            return "PHONE_ISDN";
        else if(type == 528)
            return "PHONE_MODEM_OR_GATEWAY";
        else if(type == 524)
            return "PHONE_SMART";
        else if(type == 2048)
            return "TOY";
        else if(type == 2064)
            return "TOY_CONTROLLER";
        else if(type == 2060)
            return "TOY_DOLL_ACTION_FIGURE";
        else if(type == 2068)
            return "TOY_GAME";
        else if(type == 2052)
            return "TOY_ROBOT";
        else if(type == 2056)
            return "TOY_VEHICLE";
        else if(type == 7936)
            return "UNCATEGORIZED";
        else if(type == 1792)
            return "WEARABLE";
        else if(type == 1812)
            return "WEARABLE_GLASSES";
        else if(type == 1808)
            return "WEARABLE_HELMET";
        else if(type == 1804)
            return "WEARABLE_JACKET";
        else if(type == 1800)
            return "WEARABLE_PAGER";
        else if(type == 1796)
            return "WEARABLE_WRIST_WATCH";
        else
            return "";
    }
    public static boolean IsPC_COD(int type){
        return type == 256 || type == 260 || type == 272 || type == 268 || type == 276 || type == 264 || type == 280;
    }
}
