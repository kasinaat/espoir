//ignorei18n_start
package com.orm.espoir.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PhoenixDBConnection {
	private PhoenixDBConnection() {
	}

	public Connection getPhoenixConnection() throws ClassNotFoundException, SQLException {
		Class.forName("org.apache.phoenix.jdbc.PhoenixDriver");
		return DriverManager.getConnection("jdbc:phoenix:localhost:2181");
	}

	private static final class InstanceHolder {
		private static final PhoenixDBConnection INSTANCE = new PhoenixDBConnection();
	}

	public static PhoenixDBConnection getInstance() {
		return InstanceHolder.INSTANCE;
	}
}
//ignorei18n_end