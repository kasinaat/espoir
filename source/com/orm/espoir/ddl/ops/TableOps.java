//ignorei18n_start
package com.orm.espoir.ddl.ops;

import com.orm.espoir.connection.PhoenixDBConnection;
import com.orm.espoir.ddl.exception.DDLException;
import com.orm.espoir.ddl.models.Column;
import com.orm.espoir.ddl.models.PrimaryKey;
import com.orm.espoir.ddl.models.Table;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TableOps {
	public static <T> String createTableQuery(Class<T> clazz) throws DDLException {
		StringBuilder query = new StringBuilder();
		boolean primaryKeyFound = false;
		if (clazz.isAnnotationPresent(Table.class)) {
			Table table = clazz.getAnnotation(Table.class);
			query.append("CREATE TABLE IF NOT EXISTS ").append(table.name()).append(" (");

			Field[] fields = clazz.getDeclaredFields();

			for (Field field : fields) {
				Column column = field.getAnnotation(Column.class);
				if (field.isAnnotationPresent(Column.class)) {
					query.append(column.name()).append(" ").append(column.type()).append(",");
					if (field.isAnnotationPresent(PrimaryKey.class) && !primaryKeyFound) {
						primaryKeyFound = true;
						query.deleteCharAt(query.length() - 1);
						query.append(" ").append("NOT NULL PRIMARY KEY").append(",");
					}
				}
			}
			if (!primaryKeyFound) {
				throw new DDLException("No @PrimaryKey annotation found on class " + clazz.getName());
			}
			query.deleteCharAt(query.length() - 1);
			query.append(")");
		} else {
			throw new DDLException("No @Table annotation found on class " + clazz.getName());
		}
		return query.toString();
	}

	public static boolean findAndCreateTables(String packageName) throws SQLException, ClassNotFoundException, DDLException {
		try (Connection connection = PhoenixDBConnection.getInstance().getPhoenixConnection()) {
			try (Statement createStatement = connection.createStatement()) {
				for (Class<?> clazz : getClassesWithTableAnnotation(packageName)) {
					createStatement.addBatch(TableOps.createTableQuery(clazz));
				}
				createStatement.executeBatch();
			}
		} catch (DDLException e) {
			throw new DDLException(e);
		}

		return true;
	}

	public static Set<Class<?>> getClassesWithTableAnnotation(String packageName) {
		Reflections reflections = new Reflections(packageName, Scanners.TypesAnnotated);
		return reflections.getTypesAnnotatedWith(Table.class);
	}
}
//ignorei18n_end