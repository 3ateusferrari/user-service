package com.mateusferrari.user_service.service;

import com.mateusferrari.user_service.entity.User;
import com.mateusferrari.user_service.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(User user){
        return userRepository.save(user);
    }

    public List<User> getUsers(){
        return userRepository.findAll();
    }

    public User getUserById(UUID uuid){
        return userRepository.findById(uuid).orElseThrow(() -> new RuntimeException("User " + uuid + " not found"));
    }

    public User updateUser(UUID uuid, User user){
        return userRepository.findById(uuid).map(foundedUser -> {
            foundedUser.setEmail(user.getEmail());
            foundedUser.setPassword(user.getPassword());
            return userRepository.save(foundedUser);
        }).orElseThrow(() -> new RuntimeException("User " + uuid + " not found"));
   }

   public void deleteUser(UUID uuid){
        if(userRepository.existsById(uuid)){
            userRepository.deleteById(uuid);
        }else {
            throw new RuntimeException("User " + uuid + " not found");
        }
   }
}
