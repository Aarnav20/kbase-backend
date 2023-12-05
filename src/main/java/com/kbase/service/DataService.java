package com.kbase.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;


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

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

@Service
public class DataService {

	@Autowired
	private DepartmentRepository departmentRepository;

	@Autowired
	private FunctionRepository functionRepository;

	@Autowired
	private ApplicationRepository applicationRepository;

	@Autowired
	private MenuRepository menuRepository;
	
	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private FilesRepository filesRepository;

	private IdValidator idValidator= new IdValidator(departmentRepository, functionRepository, applicationRepository, menuRepository, filesRepository);

	public DataService(IdValidator idValidator) {
		this.idValidator = idValidator;
	}
	
	private Random random = new Random();

	private void createFolder(String folderPath) {
		File folder = new File(folderPath);
		if (!folder.exists()) {
			folder.mkdirs();
		}
	}
	
	//----------------------------------------------------------------------------------------------------------------------
	
	public List<Files> performSearch(
			@RequestParam(required = false) String departmentName,
			@RequestParam(required = false) String functionName,
			@RequestParam(required = false) String applicationName,
			@RequestParam(required = false) String menuName,
			@RequestParam(required = false) String fileName,
			@RequestParam(required = false) String author,
			@RequestParam(required = false) String description,
			@RequestParam(required = false, defaultValue = "false") boolean uniqueMenuId) {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Files> query = cb.createQuery(Files.class);
		Root<Files> root = query.from(Files.class);

		List<Predicate> predicates = new ArrayList<>();
		

		// Add predicates based on the provided entries
	    if (StringUtils.hasText(departmentName)) {
	        Join<Files, Menu> menuJoin = root.join("menu");
	        Join<Menu, Application> applicationJoin = menuJoin.join("application");
	        Join<Application, Function> functionJoin = applicationJoin.join("function");
	        Join<Function, Department> departmentJoin = functionJoin.join("department");
	        predicates.add(cb.equal(departmentJoin.get("departmentName"), departmentName));
	    }

	    if (StringUtils.hasText(functionName)) {
	        Join<Files, Menu> menuJoin = root.join("menu");
	        Join<Menu, Application> applicationJoin = menuJoin.join("application");
	        Join<Application, Function> functionJoin = applicationJoin.join("function");
	        predicates.add(cb.equal(functionJoin.get("functionName"), functionName));
	    }

	    if (StringUtils.hasText(applicationName)) {
	        Join<Files, Menu> menuJoin = root.join("menu");
	        Join<Menu, Application> applicationJoin = menuJoin.join("application");
	        predicates.add(cb.equal(applicationJoin.get("applicationName"), applicationName));
	    }

	    if (StringUtils.hasText(menuName)) {
	        Join<Files, Menu> menuJoin = root.join("menu");
	        predicates.add(cb.equal(menuJoin.get("menuName"), menuName));
	    }

	    if (StringUtils.hasText(author)) {
	        predicates.add(cb.like(cb.lower(root.get("author")), "%" + author.toLowerCase() + "%"));
	    }

	    if (StringUtils.hasText(description)) {
	        Join<Files, Menu> menuJoin = root.join("menu");
	        predicates.add(cb.like(cb.lower(menuJoin.get("description")), "%" + description.toLowerCase() + "%"));
	    }

	    if (!predicates.isEmpty()) {
			query.where(cb.and(predicates.toArray(new Predicate[0])));
		}

		List<Files> searchResults = entityManager.createQuery(query).getResultList();
		
		if (uniqueMenuId) {
			searchResults = filterUniqueMenuIds(searchResults);
		}
		
		return searchResults;
	}
	
	private List<Files> filterUniqueMenuIds(List<Files> searchResults) {
        Set<Menu> uniqueMenus = new HashSet<>();
        List<Files> filteredResults = new ArrayList<>();

        for (Files file : searchResults) {
            Menu menu = file.getMenu();
            if (!uniqueMenus.contains(menu)) {
                uniqueMenus.add(menu);
                filteredResults.add(file);
            }
        }

        return filteredResults;
    }
	
	
	//----------------------------------------------------------------------------------------------------------------------

	public void addData(String departmentName, String functionName, String applicationName, String menuName,
			String fileName, String description, String author, List<MultipartFile> files) {
		Department department = getOrCreateDepartment(departmentName);
		Function function = getOrCreateFunction(functionName, department);
		Application application = getOrCreateApplication(applicationName, function);
		int menuId = generateUniqueID();
		Menu menu = createMenu(menuId, menuName, application,description);

		String storageLocation = "D:\\files";

		String menuFolderName = String.valueOf(menuId);
		String menuFolderPath = Paths.get(storageLocation, menuFolderName).toString();
		createFolder(menuFolderPath);

		if (files!=null) {
			for (MultipartFile unit : files) {
				int fileId = generateUniqueID();
				addFile(unit, fileId, fileName, menu, author, menuFolderPath);
			}
		}
	}

	private int generateUniqueID() {
		int id;
		boolean isUnique;
		do {
			id = generateRandomID();
			isUnique = checkIDUniqueness(id);
		} while (!isUnique);
		return id;
	}

	private int generateRandomID() {
		return 100_000_000 + random.nextInt(900_000_000);
	}

	private boolean checkIDUniqueness(int id) {
		boolean isUnique = idValidator.isIdUnique(id);
		return isUnique;
	}

	private Department getOrCreateDepartment(String departmentName) {
		Department department = departmentRepository.findByDepartmentName(departmentName);
		if (department == null) {
			int departmentId = generateUniqueID();
			department = new Department();
			department.setDepartmentId(departmentId);
			department.setDepartmentName(departmentName);
			departmentRepository.save(department);
		}
		return department;
	}

	private Function getOrCreateFunction(String functionName, Department department) {
		Function function = functionRepository.findByFunctionName(functionName);
		if (function == null) {
			function = new Function();
			int functionId = generateUniqueID();
			function.setFunctionId(functionId);
			function.setFunctionName(functionName);
			function.setDepartment(department);
			functionRepository.save(function);
		}
		return function;
	}

	private Application getOrCreateApplication(String applicationName, Function function) {
		Application application = applicationRepository.findByApplicationName(applicationName);
		if (application == null) {
			application = new Application();
			int applicationId = generateUniqueID();
			application.setApplicationId(applicationId);
			application.setApplicationName(applicationName);
			application.setFunction(function);
			applicationRepository.save(application);
		}
		return application;
	}

	private Menu createMenu(int menuId, String menuName, Application application, String description) {
		Menu menu = new Menu();
		menu.setMenuId(menuId);
		menu.setMenuName(menuName);
		menu.setApplication(application);
		menu.setDescription(description);
		menuRepository.save(menu);
		return menu;
	}

	private void addFile(MultipartFile file, int fileId, String fileName, Menu menu, String author, String menuFolderPath) {
		Date date = new Date();

		try {
			Files fileEntity = new Files();
			String originalFileName = file.getOriginalFilename();
			fileEntity.setFileId(fileId);
			fileEntity.setFileName(originalFileName);
			fileEntity.setMenu(menu);
			fileEntity.setAuthor(author);
			fileEntity.setMakeDate(date);
			Path filePath = Paths.get(menuFolderPath, originalFileName);
			java.nio.file.Files.copy(file.getInputStream(), filePath);

			// Save the file path to the database
			fileEntity.setFilePath(filePath.toString());

			filesRepository.save(fileEntity);
		} catch (IOException e) {
			// Handle the error
		}
	}
	
	private void deleteFilesUpdated(List<Integer> fileIdsToDelete, Menu menu) {
	    if (fileIdsToDelete != null) {  // Add null check
	        String storageLocation = "D:\\files";
	        String menuFolderPath = Paths.get(storageLocation, String.valueOf(menu.getMenuId())).toString();

	        for (Integer fileId : fileIdsToDelete) {
	            Optional<Files> optionalFile = filesRepository.findById(fileId);
	            optionalFile.ifPresent(file -> {
	                String filePath = file.getFilePath();
	                try {
	                    java.nio.file.Files.deleteIfExists(Paths.get(filePath));
	                    filesRepository.delete(file); // Delete file from repository
	                } catch (IOException e) {
	                    // Handle the error
	                    e.printStackTrace();
	                }
	            });
	        }
	    }
	}


	private void addFilesUpdated(List<MultipartFile> filesToAdd, Menu menu,String Author) {
	    if (filesToAdd != null) {
	    	String storageLocation = "D:\\files";
		    String menuFolderPath = Paths.get(storageLocation, String.valueOf(menu.getMenuId())).toString();

		    for (MultipartFile file : filesToAdd) {
		        try {
		            String originalFileName = file.getOriginalFilename();
		            Path filePath = Paths.get(menuFolderPath, originalFileName);
		            java.nio.file.Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

		            Files newFile = new Files();
		            int fileId = generateUniqueID();
		            newFile.setAuthor(Author);
		            newFile.setFileId(fileId);
		            newFile.setFileName(originalFileName);
		            newFile.setFilePath(filePath.toString());
		            newFile.setMenu(menu);
		            newFile.setUpdateDate(new Date());
		            filesRepository.save(newFile);
		        } catch (IOException e) {
		            // Handle the error
		            e.printStackTrace();
		        }
		    }
	    }
	}
	
	public void updateMenu(int menuId, String departmentName, String functionName,
			String applicationName, String author, String description,
			List<MultipartFile> filesToAdd, List<Integer> fileIdsToDelete) {
		Menu menu = menuRepository.findById(menuId);
		if (menu !=  null) {

			Department department = menu.getApplication().getFunction().getDepartment();
			Function function = menu.getApplication().getFunction();
			Application application = menu.getApplication();

			if (StringUtils.hasText(departmentName)) {
				department = getOrCreateDepartment(departmentName);
				departmentRepository.save(department);
			}

			if (StringUtils.hasText(functionName)) {
				function = getOrCreateFunction(functionName,department);
				functionRepository.save(function);
			}

			if (StringUtils.hasText(applicationName)) {
				application = getOrCreateApplication(applicationName,function);
				applicationRepository.save(application);
			}
			
			if (StringUtils.hasText(description)) {
				menu.setDescription(description);
			}

			// Delete files based on the provided file IDs
			deleteFilesUpdated(fileIdsToDelete,menu);
			// Add new files
			addFilesUpdated(filesToAdd, menu,author);

			menuRepository.save(menu);
		} else {
			// Handle the case when menu ID is not found
		}
	}

}

