package com.orm.espoir.ddl.models;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Column {
	String name();

	HbaseColumnDataType type();
}

