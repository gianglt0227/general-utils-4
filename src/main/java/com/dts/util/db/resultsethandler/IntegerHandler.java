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
 * @author GiangLT
 */
public class IntegerHandler implements ResultSetHandler<Integer> {

    @Override
    public Integer handle(ResultSet rs) throws SQLException {
        if (rs.next()) {
            return rs.getInt(1);
        } else {
            return null;
        }
    }

}