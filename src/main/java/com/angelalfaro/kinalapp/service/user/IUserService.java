package com.angelalfaro.kinalapp.service.user;

import com.angelalfaro.kinalapp.entity.User;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Optional;

public interface IUserService {

    /*
    * Method to list all the Users in DB
    * */
    List<User> listAllUsers();

    /*
     * This method will return all Users with the given state
     * */
    List<User> listAllByState(int state);

    /*
    * Method to save a User in the DB
    * */
    User saveUser(User user);

    /*
    * Method to find a user by the codeUser
    * */
    Optional<User> findByCodeUser(Long codeUser);

    /*
    * Method to update an eistant User in the DB
    * */
    @PreAuthorize("hasRole('ADMIN')")
    User updateUser(Long codeUser, User user);

    /*
    * Method to delete an existant User by the codeUser
    * */
    @PreAuthorize("hasRole('ADMIN')")
    void deleteUser(Long codeUser);

    /*
    * Return true if one User exists with the given codeUser
    * */
    boolean existsByCodeUser(Long codeUser);

}
