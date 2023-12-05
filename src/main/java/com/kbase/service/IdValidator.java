package com.kbase.service;


import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.kbase.model.Application;
import com.kbase.model.Department;
import com.kbase.model.Files;
import com.kbase.model.Function;
import com.kbase.model.Menu;
import com.kbase.repository.ApplicationRepository;
import com.kbase.repository.DepartmentRepository;
import com.kbase.repository.FilesRepository;
import com.kbase.repository.FunctionRepository;
import com.kbase.repository.MenuRepository;

@Service
public class IdValidator {
    private DepartmentRepository departmentRepository;
    private FunctionRepository functionRepository;
    private ApplicationRepository applicationRepository;
    private MenuRepository menuRepository;
    private FilesRepository filesRepository;

    public IdValidator(
            DepartmentRepository departmentRepository,
            FunctionRepository functionRepository,
            ApplicationRepository applicationRepository,
            MenuRepository menuRepository,
            FilesRepository filesRepository
    ) {
        this.departmentRepository = departmentRepository;
        this.functionRepository = functionRepository;
        this.applicationRepository = applicationRepository;
        this.menuRepository = menuRepository;
        this.filesRepository = filesRepository;
    }

    public boolean isIdUnique(int id) {
        List<Integer> existingIds = new ArrayList<>();

        // Check Department table
        List<Department> departments = departmentRepository.findAll();
        for (Department department : departments) {
            existingIds.add(department.getDepartmentId());
        }

        // Check Function table
        List<Function> functions = functionRepository.findAll();
        for (Function function : functions) {
            existingIds.add(function.getFunctionId());
        }

        // Check Application table
        List<Application> applications = applicationRepository.findAll();
        for (Application application : applications) {
            existingIds.add(application.getApplicationId());
        }

        // Check Menu table
        List<Menu> menus = menuRepository.findAll();
        for (Menu menu : menus) {
            existingIds.add(menu.getMenuId());
        }

        // Check Files table
        List<Files> files = filesRepository.findAll();
        for (Files file : files) {
            existingIds.add(file.getFileId());
        }

        // Check if ID exists in the existing IDs list
        return !existingIds.contains(id);
    }
}
