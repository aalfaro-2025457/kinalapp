package com.angelalfaro.kinalapp.service.client;

import com.angelalfaro.kinalapp.entity.Client;

import java.util.List;
import java.util.Optional;

import org.springframework.security.access.prepost.PreAuthorize;

public interface IClientService {

    /*
    * Interface: It's a contract that specifies the methods any customer service must have.
     * It doesn't include implementation, only method definitions.
    * */

    //Method to return a list of all clients
    List<Client> listAll();
    /*
    * List<Client> return a list of objects of the client entity
    * */

    List<Client> listAllByState();

    //Method to save a Client in the database
    Client save(Client c);
    //Parammeters: A Client Object with the data to save

    //Optional - Container tha can or can't have a value
    //avoid the NullPointerException error
    Optional<Client> findByDPI(String dpi);

    //Method to update a Client
    @PreAuthorize("hasRole('ADMIN')")
    Client update(String dpi, Client c);
    /*
    * Parammeters:
    *   dpi: DPI of the Client to update
    *   c: is an object with the new data
    *
    * Return an object with the updated data
    * */

    /*
    * Method of type void to delete a Client
    *   void: don't return any value
    * Delete a Client by the DPI
    * */
    @PreAuthorize("hasRole('ADMIN')")
    void delete(String dpi);

    /*
    * Method of type boolean
    * return true if Client exist and false id Client don't exist
    * */
    boolean existByDPI(String dpi);

}
