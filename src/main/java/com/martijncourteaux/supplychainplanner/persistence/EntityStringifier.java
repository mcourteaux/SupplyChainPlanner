/*
 * Copyright (C) 2017 martijn.
 *
 * For license details, see LICENSE.txt and README.txt.
 * This project is licensed under Creative Commons NC-NC-ND.
 */
package com.martijncourteaux.supplychainplanner.persistence;

import java.lang.reflect.Field;

/**
 *
 * @author martijn
 */
public class EntityStringifier {

    public static String entityToString(Object o) {
        StringBuilder sb = new StringBuilder();
        sb.append(o.getClass().getSimpleName());
        sb.append(" {\n");

        try {
            Class cl = o.getClass();
            while (cl != null && cl.isAnnotationPresent(Entity.class)) {
                Field[] flds = cl.getDeclaredFields();
                for (int i = 0; i < flds.length; ++i) {
                    Field f = flds[i];
                    if (f.isAnnotationPresent(Column.class)) {
                        sb.append("    ");
                        sb.append(f.getName());
                        for (int k = f.getName().length(); k < 20; ++k) {
                            sb.append(' ');
                        }
                        sb.append(": ");
                        sb.append(f.get(o));
                        sb.append('\n');
                    }
                }
                cl = cl.getSuperclass();
            }
        } catch (IllegalAccessException | IllegalArgumentException | SecurityException e) {
            e.printStackTrace(System.err);
            System.exit(1);
        }

        sb.append('}');

        return sb.toString();
    }
}
