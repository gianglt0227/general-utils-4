/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dts.util.api;

import com.google.common.base.Splitter;
import com.google.gson.JsonObject;
import java.util.List;

/**
 *
 * @author giang
 */
public class ApiError {

    private Integer code;
    private String message;

    public ApiError(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static ApiError fromResultString(String resultString) {
        List<String> tokens = Splitter.on('|').trimResults().splitToList(resultString);
        return new ApiError(Integer.valueOf(tokens.get(0)), (tokens.get(1)));
    }

    public JsonObject toJsonObject() {
        JsonObject jo = new JsonObject();
        jo.addProperty("code", this.code);
        jo.addProperty("message", this.message);
        return jo;
    }
}
