package at.ac.tuwien.aic.group4.cloudcomputing.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public abstract class GenericDAO<T> {
	
	@Autowired
	private SessionFactory sessionFactory;
	
	private Class<T> type;
	
	public GenericDAO(Class<T> type) {
		this.type = type;
	}
	
	public void saveOrUpdate(T object) {
		getCurrentSession().saveOrUpdate(object);
	}
	
	public T findById(Long id) {
		return (T) getCurrentSession().get(type, id);
	}
	
	public List<T> findAll() {
		return getCurrentSession()
				.createCriteria(type)
				.list();
	}
	
	public Session getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}
}
