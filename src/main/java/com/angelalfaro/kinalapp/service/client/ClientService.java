package com.angelalfaro.kinalapp.service.client;

import com.angelalfaro.kinalapp.entity.Client;
import com.angelalfaro.kinalapp.repository.ClientRepository;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/*
* Anotation that register a Bean like a Bean of Spring
* that have the business logic
* */
@Service
/*
* Default all the methods of this class will transactions
* A transaction is tha can happen or no something
* */
@Transactional
public class ClientService implements IClientService {


    /*
    * private: only is accesible inside the same class
    * final: can't change
    * ClientRepository: the repository to acces to the database
    * injection of the dependecie, Spring give us the repository
    * */
    private final ClientRepository clientRepository;

    /*
    * Constructor exec when an object was created
    * Spring give the repository automaticaly
    * */
    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
        // assign the repository to our class variable
    }


    // indicates we are implementing a interface method
    @Override
    // Optimize the query, only reads, to don't block database
    @Transactional(readOnly = true)
    public List<Client> listAll() {
        return clientRepository.findAll();
        // findAll: is a method of spring that do the select * from Clients
        // this method is from JPARepository
    }

    // indicates we are implementing a interface method
    @Override
    // Optimize the query, only reads, to don't block database
    @Transactional(readOnly = true)
    public List<Client> listAllByState() {
        return clientRepository.findAll()
                .stream()
                .filter(c -> c.getState() != 0)
                .collect(Collectors.toList());
        // .stream(): allows to manipulate collections
        // .filter(): filter a collection by the condition
        // .collect(): converts the collection to a list
    }

    @Override
    public Client save(Client c) {
        /*
        * Method to save, create a new Client
        * there we will put the business logic before save the data
        * First validate the date
        * */

        validateClient(c);
        if (c.getState() == 0)
            c.setState(1);
        return clientRepository.save(c);

    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Client> findByDPI(String dpi) {

        //find a Client by DPI
        return clientRepository.findById(dpi);
        //Optional avoid NullPointerException
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public Client update(String dpi, Client c) {

        //Method to update a existent client
        if (!clientRepository.existsById(dpi)){
            throw new RuntimeException("El cliente no ha sido encontrado");
            // if not exist, throw an exception (controlled error)
        }

        c.setDPIClient(dpi);
        //DPI of the object will be of the url and not of the JSON
        validateClient(c);

        return clientRepository.save(c);
        /*
        * When we call .save(), this not is only to save but also update
        * If data exist (update) if not do an insert but before verify if exist
        * */

    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(String dpi) {
        //delete a client
        if (!clientRepository.existsById(dpi)){
            throw new RuntimeException("El cliente no ha sido encontrado");
        }

        clientRepository.deleteById(dpi);

    }

    @Override
    @Transactional(readOnly = true)
    public boolean existByDPI(String dpi) {
        //Verify if exist a client
        return clientRepository.existsById(dpi);
    }

    // private method, only will use in this class
    private void validateClient(Client c){
        /*
        * Business validations
        * this method is private beacause it's somthing internal of the service
        * */
        if (c.getDPIClient() == null || c.getDPIClient().trim().isEmpty()){
            //If the DPI == null || is empty then of remove spaces
            //throw an exception as a message
            throw new IllegalArgumentException("DPI es un dato obligatorio");
        }
        if (c.getNameClient() == null || c.getNameClient().trim().isEmpty()){
            throw new IllegalArgumentException("El nombre es un dato obligatorio");
        }
        if (c.getLastNameClient() == null || c.getLastNameClient().trim().isEmpty()){
            throw new IllegalArgumentException("El apellido es un dato requerido");
        }

    }
}
