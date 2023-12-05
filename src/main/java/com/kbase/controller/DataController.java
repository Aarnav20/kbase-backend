package com.kbase.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kbase.model.Application;
import com.kbase.model.Department;
import com.kbase.model.Files;
import com.kbase.model.Function;
import com.kbase.model.Menu;
import com.kbase.model.Role;
import com.kbase.model.User;
import com.kbase.repository.ApplicationRepository;
import com.kbase.repository.DepartmentRepository;
import com.kbase.repository.FilesRepository;
import com.kbase.repository.FunctionRepository;
import com.kbase.repository.MenuRepository;
import com.kbase.service.DataService;
import com.kbase.service.UserService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import jakarta.persistence.QueryHint;
import jakarta.persistence.TypedQuery;


@RestController
public class DataController {
	private DepartmentRepository departmentRepository;
	private FunctionRepository functionRepository;
	private ApplicationRepository applicationRepository;
	private MenuRepository menuRepository;
	private FilesRepository filesRepository;

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private DataService dataService;
	
	@Autowired
	private UserService userService;

	@Autowired
	public DataController(DepartmentRepository departmentRepository, FunctionRepository functionRepository,
			ApplicationRepository applicationRepository, MenuRepository menuRepository,
			FilesRepository filesRepository) {
		this.departmentRepository = departmentRepository;
		this.functionRepository = functionRepository;
		this.applicationRepository = applicationRepository;
		this.menuRepository = menuRepository;
		this.filesRepository = filesRepository;
	}

	@GetMapping("/search")
	@PreAuthorize("hasAnyAuthority('USER', 'EDITOR','ADMIN')")
	public List<Files> performSearch(
			@RequestParam(required = false) String departmentName,
			@RequestParam(required = false) String functionName,
			@RequestParam(required = false) String applicationName,
			@RequestParam(required = false) String menuName,
			@RequestParam(required = false) String fileName,
			@RequestParam(required = false) String author,
			@RequestParam(required = false) String description,
			@RequestParam(required = false, defaultValue = "false") boolean uniqueMenuId) {
		
		return dataService.performSearch(departmentName, functionName, 
				applicationName, menuName, fileName, author, description, uniqueMenuId);
	}
	
	
	@GetMapping("/search/menu/{menuId}")
	@PreAuthorize("hasAnyAuthority('USER', 'EDITOR','ADMIN')")
	public List<Files> performSearchByMenuId(@PathVariable Long menuId) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Files> query = cb.createQuery(Files.class);
		Root<Files> root = query.from(Files.class);

		Join<Files, Menu> menuJoin = root.join("menu"); // Join with the "menu" entity

		List<Predicate> predicates = new ArrayList<>();

		// Add predicate for menu ID
		predicates.add(cb.equal(menuJoin.get("id"), menuId));

		// Combine predicates with AND condition
		if (!predicates.isEmpty()) {
			query.where(cb.and(predicates.toArray(new Predicate[0])));
		}

		List<Files> searchResults = entityManager.createQuery(query).getResultList();

		return searchResults;
	}
	
	
	@GetMapping("/search/{id}")
	@PreAuthorize("hasAnyAuthority('USER', 'EDITOR','ADMIN')")
	public List<Files> performSearchById(@PathVariable Long id) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Files> query = cb.createQuery(Files.class);
		Root<Files> root = query.from(Files.class);

		List<Predicate> predicates = new ArrayList<>();

		// Add predicate for fileId
		predicates.add(cb.equal(root.get("id"), id));

		// Combine predicates with AND condition
		if (!predicates.isEmpty()) {
			query.where(cb.and(predicates.toArray(new Predicate[0])));
		}

		List<Files> searchResults = entityManager.createQuery(query).getResultList();

		return searchResults;
	}

	@PostMapping("/add")
	@PreAuthorize("hasAnyAuthority('EDITOR','ADMIN')")
	public ResponseEntity<String> addData(
			@RequestParam(required = false) String departmentName,
			@RequestParam(required = false) String functionName,
			@RequestParam(required = false) String applicationName,
			@RequestParam(required = false) String menuName,
			@RequestParam(required = false) String fileName,
			@RequestParam(required = false) String description,
			@RequestParam(required = false) String author,
			@RequestParam(required = false) List<MultipartFile> file) {

		dataService.addData(departmentName, functionName, applicationName, menuName, fileName, description,author,file);

		return ResponseEntity.ok("Data added successfully.");
	}
	
	
	@GetMapping("search/deps")
	@PreAuthorize("hasAnyAuthority('USER', 'EDITOR','ADMIN')")
	public List<Object> getFieldValues() {
		String queryString = "SELECT " + "departmentName" + " FROM " + "Department";

		Query query = entityManager.createQuery(queryString);
		List<Object> fieldValues = query.getResultList();

		return fieldValues;
	}

	@GetMapping("search/func")
	@PreAuthorize("hasAnyAuthority('USER', 'EDITOR','ADMIN')")
	public List<Object> getFieldFunc() {
		String queryString = "SELECT " + "functionName" + " FROM " + "Function";

		Query query = entityManager.createQuery(queryString);
		List<Object> fieldValues = query.getResultList();

		return fieldValues;
	}

	@GetMapping("search/apps")
	@PreAuthorize("hasAnyAuthority('USER', 'EDITOR','ADMIN')")
	public List<Object> getFieldApp() {
		String queryString = "SELECT " + "applicationName" + " FROM " + "Application";

		Query query = entityManager.createQuery(queryString);
		List<Object> fieldValues = query.getResultList();

		return fieldValues;
	}

	@GetMapping("search/menu")
	@PreAuthorize("hasAnyAuthority('USER', 'EDITOR','ADMIN')")
	public List<Object> getFieldMenu() {
		String queryString = "SELECT DISTINCT " + "menuName" + " FROM " + "Menu";

		Query query = entityManager.createQuery(queryString);
		List<Object> fieldValues = query.getResultList();

		return fieldValues;
	}

	private static final String FILE_DIRECTORY = "D:\\files"; // Directory where the files are stored


	@GetMapping("/files/{filename:.+}/menu/{menuId}")
	@PreAuthorize("hasAnyAuthority('USER', 'EDITOR','ADMIN')")
	public ResponseEntity<Resource> downloadFile(@PathVariable String filename, @PathVariable String menuId) {
	    String menuFolderPath = Paths.get(FILE_DIRECTORY, menuId).toString();
	    Path filePath = Paths.get(menuFolderPath).resolve(filename);

	    try {
	        Resource resource = new UrlResource(filePath.toUri());

	        if (resource.exists()) {
	            String contentType = determineContentType(filename);

	            return ResponseEntity.ok()
	                    .contentType(MediaType.parseMediaType(contentType))
	                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
	                    .body(resource);
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }

	    return ResponseEntity.notFound().build();
	}


	private String determineContentType(String filename) {
		// You can customize this method to determine the appropriate content type based on the file extension
		if (filename.endsWith(".txt")) {
			return "text/plain";
		} else if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) {
			return "image/jpeg";
		} else if (filename.endsWith(".png")) {
			return "image/png";
		} else if (filename.endsWith(".mp3")) {
			return "audio/mpeg";
		} else if (filename.endsWith(".mp4")) {
			return "video/mp4";
		}

		// Default content type if the file type is unknown
		return "application/octet-stream";
	}
	
	@PostMapping("update/{menuId}")
	@PreAuthorize("hasAnyAuthority('EDITOR','ADMIN')")
    public ResponseEntity<String> updateMenu(
            @PathVariable int menuId,
            @RequestParam(required = false) String departmentName,
            @RequestParam(required = false) String functionName,
            @RequestParam(required = false) String applicationName,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) List<MultipartFile> filesToAdd,
            @RequestParam(required = false) List<Integer> fileIdsToDelete
    ) {
        try {
            dataService.updateMenu(menuId, departmentName, functionName, applicationName, author, description,
                    filesToAdd, fileIdsToDelete);
            return ResponseEntity.ok("Menu updated successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update menu: " + e.getMessage());
        }
    }
	
	@PostMapping("/login/create")
	public ResponseEntity<?> addNewUser(
	        @RequestParam(required = true) String userName,
	        @RequestParam(required = true) String passWord
	) {
	    // Check if a user with the given username already exists
	    ResponseEntity<List<User>> response = getUserBySearch(userName);
	    if (response.getStatusCode() == HttpStatus.OK) {
	        List<User> users = response.getBody();
	        if (!users.isEmpty()) {
	            // A user with the username already exists, return an error message
	            return ResponseEntity.status(HttpStatus.CONFLICT).body("A user with the username already exists. Please choose another username.");
	        }
	    } else {
	        // Error occurred while checking, return an error response
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while checking for existing user.");
	    }

	    // If no user exists with the given username, proceed to add the new user
	    Set<Role> roles = new HashSet<>();
	    roles.add(Role.valueOf("USER")); // Default role USER set for every user
	    String result = userService.addUser(userName, passWord, roles);
	    return ResponseEntity.ok(result);
	}

	
	@GetMapping("/allUsers")
	@PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUser();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }
    
    @GetMapping("/user")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<User>> getUserBySearch(@RequestParam(required=true) String username) {
    	List<User> users = userService.getUserBySearch(username);
    	return new ResponseEntity<>(users, HttpStatus.OK);
    }
    
	
    @PostMapping("/rolechange")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String roleChange(@RequestParam(required=true) String roleName, @RequestParam(required=true) int userId) {
    	return userService.roleChange(roleName, userId);
    }
    //Added by Saurabh
    @PostMapping("/forgotPass")
   public String forgotPass(@RequestParam(required=true) String username, @RequestParam(required = true) String passWord) {
    	
    	String response= userService.forgotPass(username, passWord);
    
    	return response;
    }
    
    @PostMapping("/deleteUser")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> deleteUser(@RequestParam Long userId) {
        try {
            userService.deleteUserById(userId);
            return ResponseEntity.ok("User deleted successfully.");
        } catch (EmptyResultDataAccessException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with ID: " + userId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete user.");
        }
    }
    
    
	
}