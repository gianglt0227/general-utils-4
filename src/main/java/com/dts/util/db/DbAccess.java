/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dts.util.db;

/**
 *
 * @author giang
 */
public class DbAccess extends BaseDbAccess {

    private DbAccess() {
    }

    public synchronized static DbAccess getInstance() {
        return DbAccessHolder.INSTANCE;
    }

    private static class DbAccessHolder {

        private static final DbAccess INSTANCE = new DbAccess();
    }
}
