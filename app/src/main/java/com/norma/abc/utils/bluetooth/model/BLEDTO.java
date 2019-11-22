package com.norma.abc.utils.bluetooth.model;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import androidx.annotation.NonNull;
import com.norma.abc.utils.bluetooth.BTSupporter;
import com.norma.abc.utils.bluetooth.BluetoothLeDevice;
import com.norma.abc.utils.bluetooth.BluetoothServiceType;
import com.norma.abc.utils.bluetooth.Signal;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;

public class BLEDTO extends BluetoothLeDevice implements Comparable<BLEDTO>{
    private final UUID MY_UUID = UUID.fromString("8ce255c0-223a-11e0-ac64-0803450c9a66");

    private final int MAX_SSID_LENGTH = 25;// MAX_SSID_LENGTH 를 넘어가면 SSID ...으로 대체

    protected String ssid;  //Device Name
    private String mac;     //MAC Address
    protected Signal rssi;  //신호세기 상중하
    private Set<BluetoothServiceType> supportService;  //Device 지원 서비스
    private int btTypeInteger;//BT 타입 숫자형
    private String btType;    //BT 타입
    private String majorDevice; //majorDevice 타입
    private int majorDevice2Integer; //majorDevice 숫자형
    private int majorBtClass;   //기기타입
    private int warnLevel;
    private BluetoothSocket socket;

    private boolean isRSSIBigInstance,isFarBT,isDN_duplicate,isDN_dupAndCOD,is_PC;

    public BLEDTO(Context ctx, BluetoothDevice bleDevice, int rssi, byte[] scanRecord) {
        super(bleDevice,rssi,scanRecord,System.currentTimeMillis());

        rssi = Math.abs(rssi);

        this.ssid = initialDN(bleDevice.getName());
        this.mac = bleDevice.getAddress();
        this.rssi = rssi<50?Signal.STRONG:rssi<60?Signal.HIGH:rssi<70?Signal.MIDDLE: Signal.LOW;
        this.supportService = BTSupporter.getBluetoothDeviceKnownSupportedServices(bleDevice);
        this.btType = BTSupporter.getDeviceType(ctx,bleDevice.getType());
        this.btTypeInteger = bleDevice.getType();
        this.majorDevice = BTSupporter.getMajorDeviceClass(bleDevice.getBluetoothClass().getMajorDeviceClass());
        this.majorDevice2Integer = bleDevice.getBluetoothClass().getMajorDeviceClass();
        this.majorBtClass = bleDevice.getBluetoothClass().getMajorDeviceClass();
        try {
            socket = bleDevice.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public BLEDTO(){
        super(null,0,null,0);
    }


    private String initialDN(String dn){
        if(dn!=null){
            return dn;
        }else
            return "Unknown Name";
    }

    public BluetoothDevice getBleDevice() {
        return mDevice;
    }

    public String getSSID(){
        return ssid.length()<MAX_SSID_LENGTH?ssid:ssid.substring(0,MAX_SSID_LENGTH)+"...";
    }

    public void setSsid(String ssid) {
        this.ssid = initialDN(ssid);
    }

    public Set<BluetoothServiceType> getSupportService() {
        return supportService;
    }

    public String getBtType() {
        return btType;
    }

    public String getMac() {
        return mac;
    }

    public int getBtTypeInteger() {
        return btTypeInteger;
    }

    public String getMajorDevice() {
        return majorDevice;
    }

    public int getMajorDevice2Integer() {
        return majorDevice2Integer;
    }

    public void setMajorDevice(BluetoothDevice bluetoothDevice) {
        this.majorDevice = BTSupporter.getMajorDeviceClass(bluetoothDevice.getBluetoothClass().getMajorDeviceClass());
        this.majorDevice2Integer = bluetoothDevice.getBluetoothClass().getMajorDeviceClass();
//        deviceWarnCheck(mDevice);
    }

    public BluetoothSocket getSocket() {
        return socket;
    }

    public void setRssi(int rssi) {
        rssi = Math.abs(rssi);
        this.mCurrentRssi = rssi;
        this.rssi = rssi<50?Signal.STRONG:rssi<60?Signal.HIGH:rssi<70?Signal.MIDDLE:Signal.LOW;
    }

    public int getMajorType() {
        return majorBtClass;
    }

    public int getRSSI() {
        return mCurrentRssi;
    }

    public int getWarnLevel() {
        return warnLevel;
    }

    public void setWarnLevel(int warnLevel) {
        this.warnLevel += warnLevel;
    }

    public boolean isDN_duplicate() {
        return isDN_duplicate;
    }

    public boolean isDN_dupAndCOD() {
        return isDN_dupAndCOD;
    }

    public void setDN_duplicate(boolean DN_duplicate) {
        isDN_duplicate = DN_duplicate;
    }

    public void setDN_dupAndCOD(boolean DN_dupAndCOD) {
        isDN_dupAndCOD = DN_dupAndCOD;
    }

    @Override
    public String toString() {
        return "BLEDTO{" +
                "MAX_SSID_LENGTH=" + MAX_SSID_LENGTH +
                ", ssid='" + ssid + '\'' +
                ", mac='" + mac + '\'' +
                ", rssi=" + rssi +
                ", originRSSI=" + mCurrentRssi +
                ", supportService=" + supportService +
                ", btTypeInteger=" + btTypeInteger +
                ", btType='" + btType + '\'' +
                ", majorDevice='" + majorDevice + '\'' +
                ", majorDevice2Integer=" + majorDevice2Integer +
                ", bleDevice=" + mDevice +
                ", warnLevel=" + warnLevel +
                ", isRSSIBigInstance=" + isRSSIBigInstance +
                ", isFarBT=" + isFarBT +
                ", isDN_duplicate=" + isDN_duplicate +
                ", isDN_dupAndCOD=" + isDN_dupAndCOD +
                '}';
    }

    @Override
    public int compareTo(@NonNull BLEDTO o) {
        return 0;
    }
}
