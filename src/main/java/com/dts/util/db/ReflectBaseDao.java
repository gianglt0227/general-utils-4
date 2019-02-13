/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dts.util.db;

import com.dts.util.db.resultsethandler.LongHandler;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.GenerousBeanProcessor;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

/**
 *
 * @author GiangLT
 * @param <T>
 */
public class ReflectBaseDao<T> extends SimpleDao {

    protected final EntityReflection<T> entityReflection;

    public ReflectBaseDao(Class<T> clazz) {
        this.entityReflection = new EntityReflection<>(clazz);
    }

    public Long getNextSequence() throws SQLException {
        String sql = "SELECT " + entityReflection.getSequenceName() + ".nextval FROM dual";
        return getSingleFieldResult(Long.class, sql);
    }

    protected T getSingleResult(String sql, Object... params) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            QueryRunner qr = new QueryRunner();
            ResultSetHandler<T> resultSetHandler
                    = new BeanHandler<>(entityReflection.getClazz(), new BasicRowProcessor(new GenerousBeanProcessor()));
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
                    = new BeanListHandler<>(entityReflection.getClazz(), new BasicRowProcessor(new GenerousBeanProcessor()));
            return qr.query(conn, sql, resultSetHandler, params);
        } finally {
            DbUtils.closeQuietly(conn);
        }
    }

    public List<T> findAll() throws SQLException {
        String sql = "SELECT * FROM " + entityReflection.getTableName();
        return getResultList(sql);
    }

    public Long countAll() throws SQLException {
        String sql = "SELECT count(" + entityReflection.getIdFieldName() + ") FROM " + entityReflection.getTableName();
        return getSingleFieldResult(Long.class, sql);
    }

    public Long count(Map<String, Object> criteria) throws SQLException {
        Set<String> criteriaColumnNames = criteria.keySet();
        String sql = getCountSql(criteriaColumnNames);
        Connection conn = null;
        try {
            conn = getConnection();
            QueryRunner qr = new QueryRunner();
            return qr.query(conn, sql, new LongHandler(), criteria.values().toArray());
        } finally {
            DbUtils.closeQuietly(conn);
        }
    }

    public int insert(T t) throws Exception {
        Connection conn = null;
        try {
            Set<String> columnNames = entityReflection.getColumnNames();
            List<Object> fieldValues = getParams(columnNames, t);
            String sql = getInsertSql(columnNames);
            conn = getConnection();
            QueryRunner qr = new QueryRunner();
            return qr.update(conn, sql, fieldValues.toArray());
        } finally {
            DbUtils.commitAndCloseQuietly(conn);
        }
    }

    public int[] insert(List<T> list) throws Exception {
        Connection conn = null;
        try {
            Object[][] params = new Object[list.size()][];
            Set<String> columnNames = entityReflection.getColumnNames();
            int i = 0;
            for (T t : list) {
                List<Object> fieldValues = getParams(columnNames, t);
                params[i++] = fieldValues.toArray();
            }

            String sql = getInsertSql(columnNames);
            conn = getConnection();
            QueryRunner qr = new QueryRunner();
            return qr.batch(conn, sql, params);
        } finally {
            DbUtils.commitAndCloseQuietly(conn);
        }
    }

    public int update(T t) throws Exception {
        Connection conn = null;
        try {
            Set<String> columnNames = entityReflection.getColumnNames();
            List<Object> fieldValues = getParams(columnNames, t);
            String sql = getUpdateSql(columnNames);
            conn = getConnection();
            QueryRunner qr = new QueryRunner();
            return qr.update(conn, sql, fieldValues.toArray());
        } finally {
            DbUtils.commitAndCloseQuietly(conn);
        }
    }

    public int[] update(List<T> list) throws Exception {
        Connection conn = null;
        try {
            Object[][] params = new Object[list.size()][];
            Set<String> columnNames = entityReflection.getColumnNames();
            int i = 0;
            for (T t : list) {
                List<Object> fieldValues = getParams(columnNames, t);
                params[i++] = fieldValues.toArray();
            }

            String sql = getUpdateSql(columnNames);
            conn = getConnection();
            QueryRunner qr = new QueryRunner();
            return qr.batch(conn, sql, params);
        } finally {
            DbUtils.commitAndCloseQuietly(conn);
        }
    }

    public int delete(Object id) throws Exception {
        Connection conn = null;
        try {
            String sql = "DELETE FROM " + entityReflection.getTableName() + " WHERE " + entityReflection.getIdFieldName() + " = ?";
            logger.trace("Delete SQL: {}", sql);

            conn = getConnection();
            QueryRunner qr = new QueryRunner();
            return qr.update(conn, sql, id);
        } finally {
            DbUtils.commitAndCloseQuietly(conn);
        }
    }

    public int[] delete(List<Object> ids) throws Exception {
        Connection conn = null;
        try {
            String sql = "DELETE FROM " + entityReflection.getTableName() + " WHERE " + entityReflection.getIdFieldName() + " = ?";
            logger.trace("Delete SQL: {}", sql);
            Object[][] params = new Object[ids.size()][];
            int i = 0;
            for (Object id : ids) {
                Object[] row = new Object[]{id};
                params[i++] = row;
            }
            conn = getConnection();
            QueryRunner qr = new QueryRunner();
            return qr.batch(conn, sql, params);
        } finally {
            DbUtils.commitAndCloseQuietly(conn);
        }
    }

    public List<Object[]> select(List<String> resultColumnNames, Map<String, Object> criteria) throws SQLException {
        Set<String> criteriaColumnNames = criteria.keySet();
        String sql = getSelectSql(resultColumnNames, criteriaColumnNames);
        Connection conn = null;
        try {
            conn = getConnection();
            QueryRunner qr = new QueryRunner();
            return qr.query(conn, sql, new ArrayListHandler(), criteria.values().toArray());
        } finally {
            DbUtils.closeQuietly(conn);
        }
    }

    private String getCountSql(Set<String> criteriaColumnNames) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(").append(entityReflection.getIdFieldName()).append(") ");
        sql.append(" FROM ").append(entityReflection.getTableName()).append(" WHERE ");

        for (String criteriaColumnName : criteriaColumnNames) {
            sql.append(criteriaColumnName).append(" = ? AND ");
        }
        sql.delete(sql.lastIndexOf("AND"), sql.length());

        return sql.toString();
    }

    private String getSelectSql(List<String> resultColumnNames, Set<String> criteriaColumnNames) {
        StringBuilder sql = new StringBuilder("SELECT ");
        for (String resultColumnName : resultColumnNames) {
            sql.append(resultColumnName).append(',');
        }
        sql.deleteCharAt(sql.lastIndexOf(","));
        sql.append(" FROM ").append(entityReflection.getTableName()).append(" WHERE ");

        for (String criteriaColumnName : criteriaColumnNames) {
            sql.append(criteriaColumnName).append(" = ? AND ");
        }
        sql.delete(sql.lastIndexOf("AND"), sql.length());

        return sql.toString();
    }

    private String getInsertSql(Set<String> columnNames) {
        StringBuilder columnStr = new StringBuilder("(");
        StringBuilder valueStr = new StringBuilder(" VALUES (");
        for (String columnName : columnNames) {
            columnStr.append(columnName).append(',');
            valueStr.append("?,");
        }
        columnStr.deleteCharAt(columnStr.lastIndexOf(","));
        valueStr.deleteCharAt(valueStr.lastIndexOf(","));
        columnStr.append(')');
        valueStr.append(')');
        String sql = "INSERT INTO " + entityReflection.getTableName() + columnStr.toString() + valueStr.toString();
        logger.trace("Insert SQL: {}", sql);

        return sql;
    }

    private List<Object> getParams(Set<String> columnNames, T t) throws Exception {
        List<Object> fieldValues = new ArrayList<>(columnNames.size());
        for (String columnName : columnNames) {
            if (!columnName.equals(entityReflection.getIdFieldName())) {
                fieldValues.add(entityReflection.getFieldValue(t, columnName));
            }
        }

        Object idValue = entityReflection.getFieldValue(t, entityReflection.getIdFieldName());
        if (idValue == null) {
            idValue = getNextSequence();
            entityReflection.setFieldValue(t, entityReflection.getIdFieldName(), idValue);
            fieldValues.add(idValue);
        }
        return fieldValues;
    }

    private String getUpdateSql(Set<String> columnNames) {
        StringBuilder sqlSetString = new StringBuilder(" SET ");
        for (String columnName : columnNames) {
            if (!columnName.equals(entityReflection.getIdFieldName())) {
                sqlSetString.append(columnName).append(" = ?,");
            }
        }
        sqlSetString.deleteCharAt(sqlSetString.lastIndexOf(","));
        String sql = "UPDATE "
                + entityReflection.getTableName()
                + sqlSetString.toString()
                + " WHERE "
                + entityReflection.getIdFieldName() + " = ?";
        logger.trace("Update SQL: {}", sql);
        return sql;
    }
}
