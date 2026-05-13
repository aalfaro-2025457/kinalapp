package com.angelalfaro.kinalapp.service.user;

import com.angelalfaro.kinalapp.entity.User;
import com.angelalfaro.kinalapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements IUserService{

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<User> listAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> listAllByState(int state) {
        return userRepository.findByStateUser(state, Pageable.unpaged())
                .toList();
    }

    @Override
    @Transactional
    public User saveUser(User user) {

        validateUser(user);
        if(user.getStateUser() == 0)
            user.setStateUser(1);
        if(user.getRolUser() == null || user.getRolUser().trim().isEmpty())
            user.setRolUser("USER");

        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByCodeUser(Long codeUser) {
        return userRepository.findById(codeUser);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public User updateUser(Long codeUser, User user) {

        if(!userRepository.existsById(codeUser)){
            throw new RuntimeException("El usuario no ha sido encontrado");
        }

        //This instance will used to avoid have empty attributes
        //if you don't send new attributes
        Optional<User> u = userRepository.findById(codeUser);

        user.setCodeUser(codeUser);

        //Set the last username if you don't send a new username
        user.setUsernameUser(user.getUsernameUser() == null ||
                user.getUsernameUser().trim().isEmpty()
                ? user.getUsernameUser() : u.get().getUsernameUser());

        //Set the last email if you don't send a new email
        user.setEmailUser(user.getEmailUser() == null ||
                user.getEmailUser().trim().isEmpty()
                ? user.getEmailUser() : u.get().getEmailUser());

        //Set the last password if you don't send a new password
        user.setPasswordUser(user.getPasswordUser() == null ||
                user.getPasswordUser().trim().isEmpty()
                ? user.getPasswordUser() : u.get().getPasswordUser());

        //Set the last rol if you don't send a new rol
        user.setRolUser(user.getRolUser() == null ||
                user.getRolUser().trim().isEmpty()
                ? user.getRolUser() : u.get().getRolUser());

        //Set the last state if you don't send a new state
        user.setStateUser(user.getStateUser() <= 0
                ? user.getStateUser() : u.get().getStateUser());

        validateUser(user);

        return userRepository.save(user);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(Long codeUser) {

        if (!userRepository.existsById(codeUser)){
            throw new RuntimeException("El usuario no ha sido encontrado");
        }

        userRepository.deleteById(codeUser);

    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByCodeUser(Long codeUser) {
        return userRepository.existsById(codeUser);
    }

    public void validateUser(User u){

        if( u.getUsernameUser() == null || u.getUsernameUser().trim().isEmpty()){
            throw new IllegalArgumentException("Nombre de Usuario es un dato Obligatorio");
        }
        if(u.getEmailUser() == null || u.getEmailUser().trim().isEmpty()){
            throw new IllegalArgumentException("Email es un dato obligatorio");
        }
        if(u.getPasswordUser() == null || u.getPasswordUser().trim().isEmpty()){
            throw new IllegalArgumentException("La contraseña es obligatoria");
        }

    }

    public User login(String username, String password) {
        Optional<User> user = userRepository.findByUsernameUser(username);
        // Basic check: verify user exists and password matches exactly
        if (user.isPresent() && user.get().getPasswordUser().trim().equals(password.trim())) {
            return user.get();
        }
        return null;
    }

    public User registerUser(User user) {
        // Business logic to save a new user
        return userRepository.save(user);
    }

    public boolean existsByUsername(String username) {
        // Checks if the username is already in the database
        return userRepository.findByUsernameUser(username).isPresent();
    }
}
