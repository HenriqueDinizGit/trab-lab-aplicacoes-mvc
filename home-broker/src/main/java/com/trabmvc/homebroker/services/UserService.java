package com.trabmvc.homebroker.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.trabmvc.homebroker.models.User;
import com.trabmvc.homebroker.repositories.UserRepository;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    public User findById(Long id) {
        Optional<User> user = this.userRepository.findById(id);
        return user.orElseThrow(() -> new RuntimeException("Usuario nao encontrado"));
    }

    @Transactional
    public User createUser(User obj) {
        obj.setId(null);
        obj = this.userRepository.save(obj);
        return obj;
    }
    
    @Transactional
    public User updateUser(User obj) {
        User outro = findById(obj.getId());
        outro.setPassword(obj.getPassword());
        return this.userRepository.save(outro);
    }

    public void deleteUser(Long id) {
        findById(id);
        try {
            this.userRepository.deleteById(id);
        } catch (Exception e){
            throw new RuntimeException();
        }
    }
}
