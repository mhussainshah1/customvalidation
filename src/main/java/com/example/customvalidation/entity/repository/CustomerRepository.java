package com.example.customvalidation.entity.repository;

import com.example.customvalidation.entity.Customer;
import org.springframework.data.repository.CrudRepository;

public interface CustomerRepository extends CrudRepository<Customer, Long> {
}
