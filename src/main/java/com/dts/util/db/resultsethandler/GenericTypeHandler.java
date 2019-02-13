/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dts.util.db.resultsethandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.commons.dbutils.ResultSetHandler;

/**
 *
 * @author giang
 * @param <T>
 */
public class GenericTypeHandler<T> implements ResultSetHandler<T> {

    private final Class<T> clazz;

    public GenericTypeHandler(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public T handle(ResultSet rs) throws SQLException {
        if (rs.next()) {
            return rs.getObject(1, clazz);
        } else {
            return null;
        }
    }

}
