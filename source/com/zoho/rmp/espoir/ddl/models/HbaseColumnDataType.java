//ignorei18n_start
package com.zoho.rmp.espoir.ddl.models;

public enum HbaseColumnDataType {
	VARCHAR("VARCHAR"), BOOL("BOOLEAN"), BIGINT("BIGINT");

	final String value;

	HbaseColumnDataType(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return value;
	}
}
//ignorei18n_end