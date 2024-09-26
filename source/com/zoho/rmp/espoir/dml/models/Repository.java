package com.zoho.rmp.espoir.dml.models;

import java.util.List;

public interface Repository<K, V> {
	void save(K entity);

	void saveAll(List<K> entityList);

	//	void update(T entity, I id);
	//
	//	void delete(I id);
	//
	//	T findById(I id);
	//
	//	List<T> findAll();
}
