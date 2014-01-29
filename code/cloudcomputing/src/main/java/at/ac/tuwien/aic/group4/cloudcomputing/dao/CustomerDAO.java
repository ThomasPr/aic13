package at.ac.tuwien.aic.group4.cloudcomputing.dao;

import org.springframework.stereotype.Repository;

import at.ac.tuwien.aic.group4.cloudcomputing.model.Customer;

@Repository
public class CustomerDAO extends GenericDAO<Customer> {
	
	public CustomerDAO() {
		super(Customer.class);
	}
	
	public Customer findByName(String name) {
		return (Customer) getCurrentSession()
				.createQuery("FROM Customer WHERE LOWER(name) = LOWER(:name)")
				.setParameter("name", name)
				.uniqueResult();
	}

}
