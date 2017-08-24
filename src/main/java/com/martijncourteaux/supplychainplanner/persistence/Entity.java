/*
 * Copyright (C) 2017 martijn.
 *
 * For license details, see LICENSE.txt and README.txt.
 * This project is licensed under Creative Commons NC-NC-ND.
 */
package com.martijncourteaux.supplychainplanner.persistence;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * An annotation required to be used on classes which want to support automatic
 * data fetching from SQL ResultSets using the <code>@Column</code> annotation.
 * 
 * @author martijn
 */
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Entity {
    String prefix() default "";
}
