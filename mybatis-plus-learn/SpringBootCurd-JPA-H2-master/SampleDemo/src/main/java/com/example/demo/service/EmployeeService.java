package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.jpa.EmployeeEntity;
import com.example.demo.repository.EmployeeRepository;

@Service
public class EmployeeService {

	@Autowired
	EmployeeRepository employeeRepository;

	public List<EmployeeEntity> getAllEmployees() {
		List<EmployeeEntity> empList = employeeRepository.findAll();
		if (empList.size() > 0) {
			return empList;
		} else {
			return new ArrayList<EmployeeEntity>();
		}

	}

	public EmployeeEntity getEmployeeById(Long Id) {
		Optional<EmployeeEntity> empId = employeeRepository.findById(Id);
		if (empId.isPresent()) {
			return empId.get();
		} else {

		}
		return null;
	}

	public long getEmployeeByFirstName(String firstName) {
		return employeeRepository.getEmployeeByFirstName(firstName);
	}
	
	public EmployeeEntity createOrUpdateEmployee(EmployeeEntity entity) 
    {
        Optional<EmployeeEntity> employee = employeeRepository.findById(entity.getId());
         
        if(employee.isPresent()) 
        {
            EmployeeEntity newEntity = employee.get();
            newEntity.setEmail(entity.getEmail());
            newEntity.setFirstName(entity.getFirstName());
            newEntity.setLastName(entity.getLastName());
 
            newEntity = employeeRepository.save(newEntity);
            return newEntity;
        } else {
            entity = employeeRepository.save(entity);
            return entity;
        }
    }
	
	public List<EmployeeEntity> saveAllEmployees(List<EmployeeEntity> entityList) {
		return employeeRepository.saveAll(entityList);
	}

	public void deleteById(Long Id) {
		Optional<EmployeeEntity> entity = employeeRepository.findById(Id);
		if (entity.isPresent()) {
			employeeRepository.deleteById(Id);
		} else {

		}

	}

	@Transactional
	public int updateEmployeeByFirstName(String firstName,String lastName) {
		return employeeRepository.updateEmployeeByFirstName(firstName, lastName);
	}

}
