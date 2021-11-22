package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.jpa.EmployeeEntity;

@Repository
public interface EmployeeRepository extends JpaRepository<EmployeeEntity, Long> {

	
	 @Query("select id from EmployeeEntity  t where t.firstName=:firstName")
	 public long getEmployeeByFirstName(@Param("firstName") String firstName);
	 
	 @Modifying(clearAutomatically = true)
	 @Query("update EmployeeEntity t set lastName=:lastName where t.firstName=:firstName")
	 public int updateEmployeeByFirstName(@Param("firstName") String firstName, @Param("lastName") String lastName);
}
