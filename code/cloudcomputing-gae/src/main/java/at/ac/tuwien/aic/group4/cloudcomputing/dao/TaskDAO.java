package at.ac.tuwien.aic.group4.cloudcomputing.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import at.ac.tuwien.aic.group4.cloudcomputing.model.Customer;
import at.ac.tuwien.aic.group4.cloudcomputing.model.Task;

@Repository
public class TaskDAO extends GenericDAO<Task> {
	
	public TaskDAO() {
		super(Task.class);
	}
	
	public Task findById(String id) {
		return getEntityManager().find(Task.class, id);
	}
	
	public List<Task> findTasksForCustomer(Customer customer) {
		return getEntityManager()
				.createQuery("SELECT t FROM Task t WHERE t.user = :user ORDER BY t.finished, t.searchPattern, t.searchStart, t.searchEnd")
				.setParameter("user", customer)
				.getResultList();
	}
	
	public List<Task> findRunningTasks() {
		return getEntityManager()
				.createQuery("SELECT t FROM Task t WHERE t.finished = false ORDER BY t.id DESC")
				.getResultList();
	}

}
