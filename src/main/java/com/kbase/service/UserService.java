package com.kbase.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import com.kbase.model.Role;
import com.kbase.model.User;
import com.kbase.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class UserService {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	public String addUser(String username, String password, Set<Role> roles) {
	    User user = new User();
	    user.setUsername(username);
	    user.setRoles(roles);
	    user.setPassword(passwordEncoder.encode(password)); // Use the "password" parameter here
	    userRepository.save(user);
	    return "User successfully added to the system";
	}
	
	public List<User> getAllUser() {
		return userRepository.findAll();
	}
	
	public List<User> getUserBySearch(String username){
		return userRepository.getUserBySearch(username);
	}
	
	public String roleChange(String roleName, int userId) {
        Role newRole = Role.valueOf(roleName);

        User user = userRepository.findById(userId).orElse(null);

        if (user != null) {
            Set<Role> newRoles = new HashSet<>();
            newRoles.add(newRole);
            
            user.setRoles(newRoles);
            
            userRepository.save(user);

            return "User's role updated successfully.";
        } else {
            return "User not found with ID: " + userId;
        }
    }
	
	@Transactional
	public void deleteUserById(Long userId) {
        userRepository.deleteById(userId);
    }

	public String forgotPass(String username, String passWord) {
		System.out.println("sdffs");
		User user=userRepository.getUserByUsername(username);
		if(user!=null)
		{
			user.setPassword(passwordEncoder.encode(passWord));
			userRepository.save(user);
		return "Password Reset Successfully";
	
	  }
		else
		{
			 return "User not found with Username: " + username;
		}

		
	}

}
