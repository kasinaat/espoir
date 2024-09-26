package com.orm.espoir.ddl.exception;

public class DDLException extends Exception {
	public DDLException(String message) {
		super(message);
	}

	public DDLException(Throwable throwable) {
		super(throwable);
	}
}
