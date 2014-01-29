package at.ac.tuwien.aic.group4.cloudcomputing.dao;

import javax.persistence.NoResultException;

import org.springframework.stereotype.Repository;

import at.ac.tuwien.aic.group4.cloudcomputing.model.Customer;

@Repository
public class CustomerDAO extends GenericDAO<Customer> {
	
	public CustomerDAO() {
		super(Customer.class);
	}
	
	public Customer findByName(String name) {
		try {
			return (Customer) getEntityManager()
					.createQuery("SELECT c FROM Customer c WHERE c.name = :name")
					.setParameter("name", name)
					.getSingleResult();
		}
		catch(NoResultException e) {
			return null;
		}
	}
}
