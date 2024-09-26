package com.zoho.rmp.espoir.dml.models;

import com.zoho.rmp.espoir.connection.PhoenixDBConnection;
import com.zoho.rmp.espoir.dml.ops.QueryOps;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractRepository<K, V> implements Repository<K, V> {

	@Override
	public void save(K entity) {
		String query = QueryOps.buildUpsertQuery(entity);
		Logger.getAnonymousLogger().log(Level.INFO, "Going to execute PreparedStatement -> " + query);
		try (Connection connection = PhoenixDBConnection.getInstance().getPhoenixConnection()) {
			connection.setAutoCommit(true);
			try (PreparedStatement stmt = connection.prepareStatement(query)) {
				setParameters(stmt, entity);
				int[] c = stmt.executeBatch();
				Logger.getAnonymousLogger().log(Level.INFO, "PreparedStatement Result -> " + c);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void saveAll(List<K> entityList) {
		String query = QueryOps.buildUpsertQuery(entityList.get(0));
		Logger.getAnonymousLogger().log(Level.INFO, "Going to execute PreparedStatement -> {0}", query);
		try (Connection connection = PhoenixDBConnection.getInstance().getPhoenixConnection()) {
			try (PreparedStatement stmt = connection.prepareStatement(query)) {
				int batchSize = 0;
				for (K entity : entityList) {
					setParameters(stmt, entity);
					stmt.addBatch();
					batchSize++;
					if (batchSize % 500 == 0) {
						stmt.executeBatch();
						batchSize = 0;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setParameters(PreparedStatement stmt, K entity) throws SQLException, IllegalAccessException {
		Field[] fields = entity.getClass().getDeclaredFields();
		int index = 1;

		for (Field field : fields) {
			field.setAccessible(true);
			stmt.setObject(index++, field.get(entity));
		}
		stmt.addBatch();
	}
}
