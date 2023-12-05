package com.kbase.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kbase.model.Department;
import com.kbase.model.Function;

@Repository
public interface FunctionRepository extends JpaRepository<Function, Integer> {
    Function findByFunctionName(String functionName);

	List<Function> findByDepartmentId(int departmentId);

	Function findByFunctionNameAndDepartment(String functionName, Department department);

	Function findById(int functionId);
}

