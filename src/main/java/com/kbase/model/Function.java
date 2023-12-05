package com.kbase.model;

import jakarta.persistence.*;

@Entity
@Table(name = "function")
public class Function {
    @Id
    @Column(name = "function_id")
    private int id;

    @Column(name = "function_name")
    private String functionName;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

	public int getFunctionId() {
		return id;
	}

	public void setFunctionId(int functionId) {
		this.id = functionId;
	}

	public String getFunctionName() {
		return functionName;
	}

	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}

	public Department getDepartment() {
		return department;
	}

	public void setDepartment(Department department) {
		this.department = department;
	}
}

