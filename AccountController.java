package com.bank.controller;

import com.bank.dto.AccountDTO;
import com.bank.entity.Account;
import com.bank.entity.Address;
import com.bank.service.AccountService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AccountController {

    @Autowired
    private AccountService accountService;

    // CREATE ACCOUNT
    @RequestMapping("/create-account")
    public Account createAccount(@RequestBody AccountDTO accountDTO) {

        Account account = new Account();
        account.setName(accountDTO.getName());
        account.setBalance(accountDTO.getBalance());
        account.setEmail(accountDTO.getEmail());

        if (accountDTO.getAddress() != null) {
            Address address = new Address();
            address.setStreet(accountDTO.getAddress().getStreet());
            address.setCity(accountDTO.getAddress().getCity());
            address.setState(accountDTO.getAddress().getState());
            address.setPostalCode(accountDTO.getAddress().getPostalCode());

            account.setAddress(address);
        }

        return accountService.saveAccount(account);
    }

    // GET ALL ACCOUNTS
    @RequestMapping("/accounts")
    @GetMapping
    public List<Account> getAllAccounts() {
        return accountService.getAllAccounts();
    }

    // GET ACCOUNT BY ID
    @GetMapping("/accounts/{id}")
    public Account getAccountById(@PathVariable Long id) {
        return accountService.getAccountById(id);
    }

    // UPDATE ACCOUNT
    @PutMapping("/update-account/{id}")
    public Account updateAccount(@PathVariable Long id, @RequestBody AccountDTO accountDTO) {

        Account account = accountService.getAccountById(id);

        account.setName(accountDTO.getName());
        account.setBalance(accountDTO.getBalance());
        account.setEmail(accountDTO.getEmail());

        if (accountDTO.getAddress() != null) {

            Address address = account.getAddress();

            if (address == null) {
                address = new Address();
            }

            address.setStreet(accountDTO.getAddress().getStreet());
            address.setCity(accountDTO.getAddress().getCity());
            address.setState(accountDTO.getAddress().getState());
            address.setPostalCode(accountDTO.getAddress().getPostalCode());

            account.setAddress(address);
        }

        return accountService.saveAccount(account);
    }

    // DELETE ACCOUNT
    @DeleteMapping("/{id}")
    public String deleteAccount(@PathVariable Long id) {

        accountService.deleteAccount(id);

        return "Account deleted successfully";
    }
}