package com.phucchinh.dogomynghe.service;

import org.apache.commons.codec.digest.HmacUtils;

public class VNPayUtils {

    public static String hmacSHA512(String key, String data) {
        return HmacUtils.hmacSha512Hex(key, data);
    }
}