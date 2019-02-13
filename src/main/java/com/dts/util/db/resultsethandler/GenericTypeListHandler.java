/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dts.util.db.resultsethandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.dbutils.ResultSetHandler;

/**
 *
 * @author giang
 * @param <T>
 */
public class GenericTypeListHandler<T> implements ResultSetHandler<List<T>> {

    private final Class<T> clazz;

    public GenericTypeListHandler(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public List<T> handle(ResultSet rs) throws SQLException {
        List<T> result = new ArrayList<>();
        while (rs.next()) {
            result.add(rs.getObject(1, clazz));
        }
        return result;
    }

}
