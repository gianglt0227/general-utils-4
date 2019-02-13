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
 * @author GiangLT
 */
public class StringListHandler implements ResultSetHandler<List<String>> {

    @Override
    public List<String> handle(ResultSet rs) throws SQLException {
        List<String> result = new ArrayList<>();
        while (rs.next()) {
            result.add(rs.getString(1));
        }
        return result;
    }

}
