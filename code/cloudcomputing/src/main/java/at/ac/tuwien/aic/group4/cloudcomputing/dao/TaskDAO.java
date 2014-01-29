package at.ac.tuwien.aic.group4.cloudcomputing.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import at.ac.tuwien.aic.group4.cloudcomputing.model.Task;

@Repository
public class TaskDAO extends GenericDAO<Task> {
	
	public TaskDAO() {
		super(Task.class);
	}
	
	public List<Task> findTasksForUsername(String username) {
		return getCurrentSession()
				.createQuery("FROM Task task WHERE LOWER(task.user.name) = LOWER(:name) ORDER BY task.id DESC")
				.setParameter("name", username)
				.list();
	}
	
	public List<Task> findRunningTasks() {
		return getCurrentSession()
				.createQuery("FROM Task WHERE finished = false ORDER BY id DESC")
				.list();
	}

}
