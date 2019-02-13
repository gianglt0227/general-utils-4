/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dts.util.db;

import com.dts.util.db.annotation.Column;
import com.dts.util.db.annotation.Id;
import com.dts.util.db.annotation.Table;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author GiangLT
 * @param <T>
 */
public class EntityReflection<T> {

    private final Class<T> clazz;
    private final String tableName;
    private final String sequenceName;
    private String idFieldName;
    private final Map<String, Method> allGettersMap = new HashMap<>();
    private final Map<String, Method> allSettersMap = new HashMap<>();
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public EntityReflection(Class<T> clazz) {
        this.clazz = clazz;
        tableName = clazz.getAnnotation(Table.class).tableName();
        sequenceName = clazz.getAnnotation(Table.class).sequenceName();
        init();
    }

    private void init() {
        Field[] fields = clazz.getFields();
        for (Field field : fields) {
            try {
                Id idAnnotation = field.getAnnotation(Id.class);
                if (idAnnotation != null) {
                    idFieldName = field.getAnnotation(Column.class).columnName();
                }
                Column columnAnnotation = field.getAnnotation(Column.class);
                if (columnAnnotation != null) {
                    String columnName = field.getAnnotation(Column.class).columnName();
                    String fieldName = field.getName();
                    String firstChar = String.valueOf(fieldName.charAt(0));
                    Method getterMethod = clazz.getMethod("get" + fieldName.replaceFirst(firstChar, firstChar.toUpperCase()));
                    allGettersMap.put(columnName, getterMethod);
                    
                    Method setterMethod = clazz.getMethod("set" + fieldName.replaceFirst(firstChar, firstChar.toUpperCase()));
                    allSettersMap.put(columnName, setterMethod);
                }
            } catch (Exception ex) {
                logger.error("", ex);
            }
        }
    }

    public Object getFieldValue(T t, String columnName) throws Exception {
        Method getterMethod = this.allGettersMap.get(columnName);
        return getterMethod.invoke(t);
    }
    
    public Object setFieldValue(T t, String columnName, Object value) throws Exception {
        Method setterMethod = this.allSettersMap.get(columnName);
        return setterMethod.invoke(t, value);
    }

    public Class<T> getClazz() {
        return clazz;
    }

    public String getTableName() {
        return tableName;
    }

    public String getIdFieldName() {
        return idFieldName;
    }
    
    public Set<String> getColumnNames(){
        return allGettersMap.keySet();
    }

    public String getSequenceName() {
        return sequenceName;
    }

    public Map<String, Method> getAllGettersMap() {
        return allGettersMap;
    }
}
