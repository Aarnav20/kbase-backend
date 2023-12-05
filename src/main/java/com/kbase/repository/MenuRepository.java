package com.kbase.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kbase.model.Application;
import com.kbase.model.Menu;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Integer> {
    Menu findByMenuName(String menuName);

	List<Menu> findByApplicationId(int applicationId);

	Menu findByMenuNameAndApplication(String menuName, Application application);

	Menu findById(int menuId);
}

