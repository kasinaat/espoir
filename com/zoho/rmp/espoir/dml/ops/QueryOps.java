package com.zoho.rmp.espoir.dml.ops;

import com.zoho.rmp.espoir.ddl.models.Column;
import com.zoho.rmp.espoir.ddl.models.Table;

import java.lang.reflect.Field;

public class QueryOps {
	public static <T> String buildUpsertQuery(T entity) {
		StringBuilder sql = new StringBuilder("UPSERT INTO ");
		String tableName = getTableName(entity.getClass());
		sql.append(tableName).append(" (");

		StringBuilder placeholders = new StringBuilder();
		Field[] fields = entity.getClass().getDeclaredFields();

		for (Field field : fields) {
			field.setAccessible(true);
			sql.append(getColumnName(field)).append(", ");
			placeholders.append("?, ");
		}

		sql.setLength(sql.length() - 2);
		placeholders.setLength(placeholders.length() - 2);

		sql.append(") VALUES (").append(placeholders).append(")");

		return sql.toString();
	}

	private static String getTableName(Class<?> clazz) {
		if (!clazz.isAnnotationPresent(Table.class)) {
			throw new IllegalArgumentException("Entity class must have @Table annotation");
		}
		Table tableAnnotation = clazz.getAnnotation(Table.class);
		return tableAnnotation.name();
	}

	private static String getColumnName(Field field) {
		if (field.isAnnotationPresent(Column.class)) {
			Column columnAnnotation = field.getAnnotation(Column.class);
			return columnAnnotation.name();
		}
		return field.getName(); // Default to field name if no @Column annotation
	}
}
