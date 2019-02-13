/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dts.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author GiangLT
 */
public class CommonUtil {

    public static final String MPS_DATE_PATTERN = "yyyyMMddHHmmss";

    public static final Date parseTime(String date) throws ParseException {
        return new SimpleDateFormat(MPS_DATE_PATTERN).parse(date);
    }
}
