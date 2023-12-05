package com.kbase.model;

import jakarta.persistence.*;

@Entity
@Table(name = "department")
public class Department {
    @Id
    @Column(name = "department_id")
    private int id;

    @Column(name = "department_name")
    private String departmentName;

	public int getDepartmentId() {
		return id;
	}

	public void setDepartmentId(int departmentId) {
		this.id = departmentId;
	}

	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}
}

