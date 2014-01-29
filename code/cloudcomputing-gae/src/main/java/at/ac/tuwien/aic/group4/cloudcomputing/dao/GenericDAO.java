package at.ac.tuwien.aic.group4.cloudcomputing.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.transaction.annotation.Transactional;

import com.google.appengine.api.datastore.Key;

@Transactional
public abstract class GenericDAO<T> {
	
	@PersistenceContext
	private EntityManager entityManager;
	
	private Class<T> type;
	
	public GenericDAO(Class<T> type) {
		this.type = type;
	}

	public void persist(T object) {
		getEntityManager().persist(object);
	}
	
	public void merge(T object) {
		getEntityManager().merge(object);
	}
	
	public T findById(Key id) {
		return getEntityManager().find(type, id);
	}
	
	public List<T> findAll() {
		return getEntityManager().createQuery("SELECT e FROM " + type.getName() + " e").getResultList();
	}
	

	public EntityManager getEntityManager() {
		return entityManager;
	}
}
