package com.mateusferrari.user_service.controller;

import com.mateusferrari.user_service.entity.User;
import com.mateusferrari.user_service.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    //Create
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user){
        User saved = userService.createUser(user);
        return ResponseEntity.created(URI.create("/users/" + saved.getId())).body(saved);
    }

    //Read All
    @GetMapping
    public ResponseEntity<List<User>> getUsers(){
        return ResponseEntity.ok(userService.getUsers());
    }
    //Read 1
    @GetMapping("/{uuid}")
    public ResponseEntity<User> getUserById(@PathVariable UUID uuid){
        return ResponseEntity.ok(userService.getUserById(uuid));
    }

    //Update
    @PutMapping("/{uuid}")
    public ResponseEntity<User> updateUser(@PathVariable UUID uuid, @RequestBody User user){
        return ResponseEntity.ok(userService.updateUser(uuid, user));
    }
    //Delete
    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID uuid){
        userService.deleteUser(uuid);
        return ResponseEntity.noContent().build();
    }
}
