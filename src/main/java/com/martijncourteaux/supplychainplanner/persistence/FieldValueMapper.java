/*
 * Copyright (C) 2017 martijn.
 *
 * For license details, see LICENSE.txt and README.txt.
 * This project is licensed under Creative Commons NC-NC-ND.
 */
package com.martijncourteaux.supplychainplanner.persistence;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author martijn
 * @param <T>
 */
public class FieldValueMapper<T> {

    private final Class<T> theClass;
    private List<Column> columns;
    private List<Field> fields;
    private List<Integer> indices;

    public FieldValueMapper(Class<T> theClass) {
        this.theClass = theClass;
    }

    public void prepare(ResultSet rs) throws SQLException {
        columns = new ArrayList<>();
        fields = new ArrayList<>();
        indices = new ArrayList<>();

        Class cl = theClass;
        while (cl != null && cl.isAnnotationPresent(Entity.class)) {
            Field[] flds = cl.getDeclaredFields();
            for (int i = 0; i < flds.length; ++i) {
                Field f = flds[i];
                if (f.isAnnotationPresent(Column.class)) {
                    Column c = f.getAnnotation(Column.class);
                    String columnName = c.column();
                    if (columnName.isEmpty()) {
                        columnName = f.getName();
                    }
                    try {
                        int index = rs.findColumn(columnName);
                        System.out.printf("Field found: %s as column %s at index %d%n", f.getName(), columnName, index);
                        columns.add(c);
                        fields.add(f);
                        indices.add(index);
                    } catch (SQLException e) {
                        System.out.printf("Field *NOT* found: %s as column %s%n", f.getName(), columnName);
                    }

                }
            }
            cl = cl.getSuperclass();
        }
    }

    public void extract(ResultSet rs, T out) throws SQLException, IllegalArgumentException, IllegalAccessException {
        int size = fields.size();
        for (int i = 0; i < size; ++i) {
            Field f = fields.get(i);
            int index = indices.get(i);
            Object value = rs.getObject(index);
            f.set(out, value);
        }
    }

}
