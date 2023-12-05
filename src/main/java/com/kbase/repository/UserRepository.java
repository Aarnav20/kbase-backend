package com.kbase.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kbase.model.User;

import jakarta.transaction.Transactional;

public interface UserRepository extends JpaRepository<User, Integer>{
	
	@Query("SELECT u FROM User u WHERE u.username = :username")
	public User getUserByUsername(@Param("username") String username);
	
	
	@Query("SELECT u FROM User u WHERE u.username LIKE %:username%")
    public List<User> getUserBySearch(@Param("username") String username);
	
	
	@Transactional
	@Modifying
    @Query("DELETE FROM User u WHERE u.id = :userId")
    void deleteById(@Param("userId") Long userId);
	
}
