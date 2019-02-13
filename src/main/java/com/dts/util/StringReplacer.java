/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dts.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author GiangLT
 */
public class StringReplacer {

    private final Map<String, String> replacementMap = new HashMap<>();

    public StringReplacer add(String placeHolder, String replacement) {
        replacementMap.put(placeHolder, replacement);
        return this;
    }

    public String replace(String raw) {
        Set<String> keys = replacementMap.keySet();
        for (String key : keys) {
            raw = raw.replaceAll(key, replacementMap.get(key));
        }
        return raw;
    }

    public static void main(String[] args) {
//        String raw = "Quy khach da dang ky thanh cong goi nhac cho ~TEN_GOI~ (~GIA_GOI~d/tuan). Mien phi cai dat ~SO_BAI_HAT~ bai hat. Ngoai ra, Ban co co hoi so huu xe LX tri gia 70 trieu dong khi tham gia chuong trinh game trung thuong [Vui showbiz vit tay ga] hoan toan MIEN PHI trong 03 ngay dau tien . L/H: 19008198(200d/p). Tran trong.";
//        String result = new StringReplacer()
//                .add("~TEN_GOI~", "Danh cho FA")
//                .add("~GIA_GOI~", "2000")
//                .add("~SO_BAI_HAT~", "5")
//                .replace(raw);
//        System.out.println(result);
//
//        String result2 = raw.replaceAll("~TEN_GOI~", "Danh cho FA")
//                .replaceAll("~GIA_GOI~", "2000")
//                .replaceAll("~SO_BAI_HAT~", "5");
//        System.out.println(result2);
        String string = "hkfsdfs";
        StringReplacer replacer = new StringReplacer();
        Class type = string.getClass();
        System.out.println(type);
        System.out.println(type.getName());
        if (type.getName().equals("java.lang.")) {
            System.out.println("true");
        }
//        System.out.println(clazz);
    }
}
