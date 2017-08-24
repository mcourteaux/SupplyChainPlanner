/*
 * Copyright (C) 2017 martijn.
 *
 * For license details, see LICENSE.txt and README.txt.
 * This project is licensed under Creative Commons NC-NC-ND.
 */
package com.martijncourteaux.supplychainplanner.persistence;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Column annotation, used for automatic fetching from a SQL ResultSet.
 * Allows to specify a <code>column</code> string if the column name in the
 * ResultSet differs from the Java field name.
 * 
 * @author martijn
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {
    String column() default "";
    String exact_column() default "";
}
