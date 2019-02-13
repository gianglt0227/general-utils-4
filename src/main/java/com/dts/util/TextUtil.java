/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dts.util;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author GiangLT
 */
public class TextUtil {

    public static String replaceTokens(String rawStr, Map<String, String> tokenReplaceMap) {
        Set<String> keys = tokenReplaceMap.keySet();
        for (String key : keys) {
            rawStr = rawStr.replaceAll(key, tokenReplaceMap.get(key));
        }
        return rawStr;
    }

    public static String removeAccent(String text) {
        return text == null ? null : Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .replaceAll("\u0111", "d")
                .replaceAll("\u0110", "D");
    }

    public static String normalizeMsisdn(String msisdn) {
        if (msisdn.startsWith("84")) {
            return msisdn;
        } else if (msisdn.startsWith("0")) {
            return msisdn.replaceFirst("0", "84");
        } else {
            return "84" + msisdn;
        }
    }

    public static String denormalizeMsisdn(String msisdn) {
        if (msisdn.startsWith("84")) {
            return msisdn.replaceFirst("84", "");
        } else if (msisdn.startsWith("0")) {
            return msisdn.replaceFirst("0", "");
        } else {
            return msisdn;
        }
    }

    public static boolean isValidMobifoneMsisdn(String msisdn) {
        List<String> prefixes = new ArrayList<>();
        prefixes.add("8490");
        prefixes.add("8493");
        prefixes.add("84120");
        prefixes.add("84121");
        prefixes.add("84122");
        prefixes.add("84126");
        prefixes.add("84128");
        prefixes.add("8489");
        return isValidMsisdn(msisdn, prefixes);
    }

    public static boolean isValidMsisdn(String msisdn, List<String> prefixes) {
        String normalizeMsisdn = normalizeMsisdn(msisdn);
        boolean isValid = false;
        for (String prefix : prefixes) {
            if (normalizeMsisdn.startsWith(prefix)) {
                if (normalizeMsisdn.replaceFirst(prefix, "").length() == 7) {
                    isValid = true;
                    break;
                }
            }
        }
        return isValid;
    }

    public static void main(String[] args) {
        System.out.println(TextUtil.isValidMobifoneMsisdn("01266202328"));
    }
}
