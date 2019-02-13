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
 */
public class UncheckedListHandler implements ResultSetHandler<List<Object[]>>{

    @Override
    public List<Object[]> handle(ResultSet rs) throws SQLException {
        List<Object[]> records = new ArrayList<>();
        if (rs != null) {
            while (rs.next()) {
                int cols = rs.getMetaData().getColumnCount();
                Object[] arr = new Object[cols];
                for (int i = 0; i < cols; i++) {
                    arr[i] = rs.getObject(i + 1);
                }
                records.add(arr);
            }
        }
        return records;
    }
    
}
