package com.football.dev.footballapp.controllers;

import com.football.dev.footballapp.dto.UserEntityDto;
import com.football.dev.footballapp.models.Club;
import com.football.dev.footballapp.models.UserEntity;
import com.football.dev.footballapp.security.JWTGenerator;
import com.football.dev.footballapp.services.UserService;
import com.football.dev.footballapp.services.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/")
@CrossOrigin("http://localhost:4200")
public class UserController {
    private final UserService userService;
    private final JWTGenerator jwtGenerator;


    public UserController(UserService userService, JWTGenerator jwtGenerator){
        this.userService=userService;
        this.jwtGenerator = jwtGenerator;
    }
    @GetMapping("users")
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserEntity> getUsersWithUserRole() {
        return userService.getUsersByRoleAndIsDeleted("USER", false);
    }

    /*public List<UserEntity> getAllUsers(@RequestParam Long currentUserId) {
        return userService.getAllUsers().stream()
                .filter(user -> !user.getId().equals(currentUserId))
                .collect(Collectors.toList());
    }*/
    @PutMapping("users/{userId}/status")
    public void updateUserStatus(@PathVariable Long userId, @RequestParam boolean enabled) {
        userService.updateUserStatus(userId, enabled);
    }
    @DeleteMapping("users/delete/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
    }

    @PostMapping("users/save")
    public ResponseEntity<Void> saveUser(@RequestBody UserEntityDto userDto) {
        userService.saveUser(userDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("users/update/{userId}")
    public ResponseEntity<String> updateUser(@PathVariable Long userId, @RequestBody UserEntityDto updatedUserDto, HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        String token = authorizationHeader.substring(7);

        if (!jwtGenerator.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
        userService.updateUser(userId, updatedUserDto);
        return ResponseEntity.ok().build();
    }
    @GetMapping("users/{userId}")
    public UserEntity getUserProfile(@PathVariable Long userId) {
        return userService.getUserProfile(userId);
    }

}

