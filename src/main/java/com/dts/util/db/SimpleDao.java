/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dts.util.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ArrayHandler;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import org.apache.commons.dbutils.handlers.ColumnListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author GiangLT
 */
public class SimpleDao {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    protected Connection getConnection() throws SQLException {
        return DbAccess.getInstance().getConnection();
    }

    protected <T> T getSingleFieldResult(Class<T> clazz, String sql, Object... params) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            QueryRunner qr = new QueryRunner();
            return qr.query(conn, sql, new ScalarHandler<>(), params);
        } finally {
            DbUtils.closeQuietly(conn);
        }
    }

    protected <T> List<T> getSingleColumnResult(Class<T> clazz, String sql, Object... params) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            QueryRunner qr = new QueryRunner();
            return qr.query(conn, sql, new ColumnListHandler<>(), params);
        } finally {
            DbUtils.closeQuietly(conn);
        }
    }

    protected List<Object[]> getUncheckedListResult(String sql, Object... params) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            QueryRunner qr = new QueryRunner();
            return qr.query(conn, sql, new ArrayListHandler(), params);
        } finally {
            DbUtils.closeQuietly(conn);
        }
    }

    protected Object[] getUncheckedSingleResult(String sql, Object... params) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            QueryRunner qr = new QueryRunner();
            return qr.query(conn, sql, new ArrayHandler(), params);
        } finally {
            DbUtils.closeQuietly(conn);
        }
    }



}
