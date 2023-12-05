package com.kbase.model;

import jakarta.persistence.*;

@Entity
@Table(name = "application")
public class Application {
    @Id
    @Column(name = "application_id")
    private int id;

    @Column(name = "application_name")
    private String applicationName;

    @ManyToOne
    @JoinColumn(name = "function_id")
    private Function function;

	public int getApplicationId() {
		return id;
	}

	public void setApplicationId(int applicationId) {
		this.id = applicationId;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public Function getFunction() {
		return function;
	}

	public void setFunction(Function function) {
		this.function = function;
	}
    
}
