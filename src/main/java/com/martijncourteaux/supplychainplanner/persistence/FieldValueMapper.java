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
 * Utility class to automatically extract data from a ResultSet and put it into
 * a <code>@Entity</code> annotated class object. It will load
 * <code>@Column</code> annotated fields.
 *
 * @author martijn
 * @param <T> The Entity class.
 */
public class FieldValueMapper<T> {

    private final Class<T> theClass;
    private List<Column> columns;
    private List<Field> fields;
    private List<Integer> indices;

    /**
     * Pass in the Class object for the Entity class.
     *
     * @param theClass
     */
    public FieldValueMapper(Class<T> theClass) {
        this.theClass = theClass;
    }

    /**
     * Prepare extraction from the ResultSet.
     *
     * @param rs The result set from which to extract data.
     * @throws SQLException
     */
    public void prepare(ResultSet rs) throws SQLException {
        columns = new ArrayList<>();
        fields = new ArrayList<>();
        indices = new ArrayList<>();

        Class cl = theClass;
        while (cl != null && cl.isAnnotationPresent(Entity.class)) {
            Entity entity = (Entity) cl.getAnnotation(Entity.class);
            Field[] flds = cl.getDeclaredFields();
            for (int i = 0; i < flds.length; ++i) {
                Field f = flds[i];
                if (f.isAnnotationPresent(Column.class)) {
                    Column c = f.getAnnotation(Column.class);
                    String columnName;
                    if (c.exact_column().isEmpty()) {
                        columnName = c.column();
                        if (columnName.isEmpty()) {
                            columnName = f.getName();
                        }
                        columnName = entity.prefix() + columnName;
                    } else {
                        columnName = c.exact_column();
                    }
                    try {
                        int index = rs.findColumn(columnName);
                        System.out.printf("Field %s found as column %s at index %d%n", f.getName(), columnName, index);
                        columns.add(c);
                        fields.add(f);
                        indices.add(index);
                    } catch (SQLException e) {
                        System.out.printf("Field %s *NOT* found as column %s%n", f.getName(), columnName);
                    }

                }
            }
            cl = cl.getSuperclass();
        }
    }

    /**
     * Extract the current row in the result set into the <code>out</code>
     * object. Before extraction can be done, the <code>prepare()</code> method
     * must be called first.
     *
     * @param rs The ResultSet from which to extract data.
     * @param out The object into which to store the data.
     *
     * @throws SQLException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
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
