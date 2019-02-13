package com.dts.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MapUtil {

    public static String mapToQueryString(Map<String, String> map) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();
            if (stringBuilder.length() > 0) {
                stringBuilder.append("&");
            }
            stringBuilder.append((key != null ? key : ""));
            stringBuilder.append("=");
            stringBuilder.append(value != null ? value : "");
        }
        return stringBuilder.toString();
    }

    public static Map<String, String> queryStringToMap(String input) {
        Map<String, String> params = new LinkedHashMap<>();
        String[] nameValuePairs = input.split("&");
        for (String nameValuePair : nameValuePairs) {
            int pos = nameValuePair.indexOf("=");
            if (pos != -1) {
                String name = nameValuePair.substring(0, pos);
                String value = nameValuePair.substring(pos + 1);
                params.put(name, value);
            }
        }
        return params;
    }

    public static String mapToHttpQueryString(Map<String, String> map) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();
            if (stringBuilder.length() > 0) {
                stringBuilder.append("&");
            }
            try {
                stringBuilder.append((key != null ? URLEncoder.encode(key,
                        "UTF-8") : ""));
                stringBuilder.append("=");
                stringBuilder.append(value != null ? URLEncoder.encode(value,
                        "UTF-8") : "");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(
                        "This method requires UTF-8 encoding support", e);
            }
        }

        return stringBuilder.toString();
    }

    public static Map<String, String> httpQueryStringToMap(String input) {
        Map<String, String> map = new LinkedHashMap<>();

        String[] nameValuePairs = input.split("&");
        for (String nameValuePair : nameValuePairs) {
            String[] nameValue = nameValuePair.split("=");
            try {
                map.put(URLDecoder.decode(nameValue[0], "UTF-8"),
                        nameValue.length > 1 ? URLDecoder.decode(nameValue[1],
                                        "UTF-8") : "");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(
                        "This method requires UTF-8 encoding support", e);
            }
        }
        return map;
    }

    public static Map sortMap(Map unsortMap, final boolean desc) {

        List list = new LinkedList(unsortMap.entrySet());

        // sort list based on comparator
        Collections.sort(list, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                if (desc) {
                    return ((Comparable) ((Map.Entry) (o2)).getValue())
                            .compareTo(((Map.Entry) (o1)).getValue());
                } else {
                    return ((Comparable) ((Map.Entry) (o1)).getValue())
                            .compareTo(((Map.Entry) (o2)).getValue());
                }
            }
        });
        // put sorted list into map again
        // LinkedHashMap make sure order in which keys were inserted
        Map sortedMap = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }
}
