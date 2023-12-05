package com.kbase.model;

import jakarta.persistence.*;

@Entity
@Table(name = "menu")
public class Menu {
    @Id
    @Column(name = "menu_id")
    private int id;

    @Column(name = "menu_name")
    private String menuName;
    


    @Column(name = "description")
    private String description;

	@ManyToOne
    @JoinColumn(name = "application_id")
    private Application application;

	public int getMenuId() {
		return id;
	}

	public void setMenuId(int menuId) {
		this.id = menuId;
	}

	public String getMenuName() {
		return menuName;
	}

	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}

	public Application getApplication() {
		return application;
	}

	public void setApplication(Application application) {
		this.application = application;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}

