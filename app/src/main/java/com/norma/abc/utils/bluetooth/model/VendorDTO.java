package com.norma.abc.utils.bluetooth.model;

public class VendorDTO {
    public String mac;
    public String vendor;

    @Override
    public String toString() {
        return "VendorDTO{" +
                "mac='" + mac + '\'' +
                ", vendor='" + vendor + '\'' +
                '}';
    }
}
