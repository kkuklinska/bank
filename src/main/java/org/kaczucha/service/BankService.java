package org.kaczucha.service;

import org.kaczucha.Client;
import org.kaczucha.repository.ClientRepository;

import java.util.NoSuchElementException;
import java.util.Objects;

public class BankService {
    private final ClientRepository clientRepository;

    public BankService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public void save(Client client) {
        clientRepository.save(client);
    }

    public void delete(Client client) {
        String email = null;

        if ( client.getBalance() == 0) {
            clientRepository.delete(client);
        } else {
        throw new IllegalArgumentException("It is not possible to delete client with funds in account");

    }
        if(client.getEmail()!= null){
            clientRepository.delete(client);
        } else {
            throw new IllegalArgumentException("It is not possible to delete client with empty email");
        }
        if(client.getEmail().equals(email)){
            clientRepository.delete(client);
        } else {
            throw new NoSuchElementException("It is not possible to delete client with incorrect email");

        }
    }


    public Client findByEmail(String email) {
        return clientRepository.findByEmail(email);
    }

    public void transfer(
            String fromEmail,
            String toEmail,
            double amount
    ) {
        validateAmount(amount);
        if (fromEmail.equals(toEmail)) {
            throw new IllegalArgumentException("fromEmail and toEmail cant be equal!");
        }
        Client fromClient = findByEmail(fromEmail);
        Client toClient = findByEmail(toEmail);
        if (fromClient.getBalance() - amount >= 0) {
            fromClient.setBalance(fromClient.getBalance() - amount);
            toClient.setBalance(toClient.getBalance() + amount);
        } else {
            throw new NoSufficientFundsException("Not enough funds!");
        }
    }

//    public void withdraw(
//            final String email,
//            final double amount) {
//        validateAmount(amount);
//        if (Objects.isNull(email)) {
//            throw new IllegalArgumentException("Email cant be null!");
//        }
//        final String lowerCaseEmail = email.toLowerCase();
//        final Client client = findByEmail(lowerCaseEmail);
//        if (amount > client.getBalance()) {
//            throw new NoSufficientFundsException("Balance must be higher or equal then amount!");
//        }
//        final double newBalance = client.getBalance() - amount;
//        client.setBalance(newBalance);
//
//    }

    private void validateAmount(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive!");
        }
    }

    public void  withdraw(
            final String email,
            final int amount) {
        if(amount<=0){
        throw new IllegalArgumentException("Amount have to be positive");
        }
        if(Objects.isNull(email)){
            throw new IllegalArgumentException("Email cant be null");
        }
        final String lowerCaseEmail = email.toLowerCase();

        final Client client = findByEmail(lowerCaseEmail);
        if(amount> client.getBalance()){
            throw new NoSufficientFundsException("Balance must be higher or equal than amount");
        }
        final double newBalance = client.getBalance()- amount;
        client.setBalance(newBalance);

    }
}
