/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dts.util.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.GenerousBeanProcessor;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

/**
 *
 * @author GiangLT
 * @param <T>
 */
public abstract class GenericBaseDao<T> extends SimpleDao {

    private final Class<T> clazz;

    public GenericBaseDao(Class<T> clazz, String tableName, String sequenceName) {
        this.clazz = clazz;
        this.SEQUENCE_NAME = sequenceName;
        this.TABLE_NAME = tableName;
    }

    protected final String SEQUENCE_NAME;
    protected final String TABLE_NAME;

    public Long getNextSequence() throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            String sql = "SELECT " + SEQUENCE_NAME + ".nextval FROM dual";
            QueryRunner qr = new QueryRunner();
            return qr.query(conn, sql, new ScalarHandler<>());
        } finally {
            DbUtils.closeQuietly(conn);
        }

    }

    protected T getSingleResult(String sql, Object... params) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            QueryRunner qr = new QueryRunner();
            ResultSetHandler<T> resultSetHandler
                    = new BeanHandler<>(clazz, new BasicRowProcessor(new GenerousBeanProcessor()));
            return qr.query(conn, sql, resultSetHandler, params);
        } finally {
            DbUtils.closeQuietly(conn);
        }
    }

    protected List<T> getResultList(String sql, Object... params) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            QueryRunner qr = new QueryRunner();
            ResultSetHandler<List<T>> resultSetHandler
                    = new BeanListHandler<>(clazz, new BasicRowProcessor(new GenerousBeanProcessor()));
            return qr.query(conn, sql, resultSetHandler, params);
        } finally {
            DbUtils.closeQuietly(conn);
        }
    }

    public List<T> findAll() throws SQLException {
        String sql = "SELECT * FROM " + TABLE_NAME;
        return getResultList(sql);
    }

    public Long countAll() throws SQLException {
        String sql = "SELECT count(*) FROM " + TABLE_NAME;
        return getSingleFieldResult(Long.class, sql);
    }

    public abstract Long insert(T item) throws Exception;
}
