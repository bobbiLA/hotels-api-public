package com.jexige.hotelsapi.model.client;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ClientService {

    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public Page<Client> findAll(Pageable pageable) {
        return clientRepository.findAll(pageable);
    }

    public Optional<Client> findById(long id) {
        return clientRepository.findById(id);
    }

    public Client save(Client newClient) {
        return clientRepository.save(newClient);
    }

    public Client update(Client newClient, long id) throws ClientNotFoundException {
        final Optional<Client> optionalClient = clientRepository.findById(id);
        if (optionalClient.isEmpty()) {
            throw new ClientNotFoundException(id);
        } else {
            return save(newClient);
        }
    }

    public void deleteById(long id) throws ClientNotFoundException {
        final Optional<Client> opt = findById(id);
        if (opt.isEmpty()) {
            throw new ClientNotFoundException(id);
        } else {
            clientRepository.deleteById(id);
        }
    }
}
