/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dts.util.api;

/**
 *
 * @author giang
 */
public class UserFriendlyException extends Exception {

    /**
     * Creates a new instance of <code>UserFriendlyException</code> without
     * detail message.
     */
    public UserFriendlyException() {
    }
    private String title;

    /**
     * Constructs an instance of <code>UserFriendlyException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public UserFriendlyException(String msg) {
        super(msg);
    }

    public UserFriendlyException(String title, String message) {
        super(message);
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
