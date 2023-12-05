package com.kbase.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kbase.model.Application;
import com.kbase.model.Function;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Integer> {
    Application findByApplicationName(String applicationName);

	List<Application> findByFunctionId(int functionId);

	Application findByApplicationNameAndFunction(String applicationName, Function function);

	Application findById(int applicationId);
}
