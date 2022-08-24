package com.raiden.cloudstorage.services;

import com.raiden.cloudstorage.entities.User;
import com.raiden.cloudstorage.exceptions.UsernameTakenException;
import com.raiden.cloudstorage.repositories.UserRepository;
import com.raiden.cloudstorage.utils.JwtUtil;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final FolderService folderService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder encoder;
    public void addUser(User user){
        Optional<User> userCheck = userRepository.findByUsername(user.getUsername());

        if(userCheck.isPresent())
            throw new UsernameTakenException();

        roleService.assignDefaultRole(user);
        user.setPassword(encoder.encode(user.getPassword()));

        userRepository.save(user);
        folderService.createMainFolder(user);
    }

    public User getUserById(String id){
        return userRepository
                .findById(id)
                .orElseThrow();
    }

    public User getUserByToken(String bearer){
        String token = bearer.substring(7);
        String username = jwtUtil.extractUsername(token);
        return this.getUserByUsername(username);
    }

    public User getUserByUsername(String username) {
        return userRepository
                .findByUsername(username)
                .orElseThrow();
    }
}
