package com.techelevator.tenmo.controller;


import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
public class AccountController {

    private AccountDao accountDao;
    private TransferDao transferDao;
    private UserDao userDao;


    public AccountController(AccountDao accountDao, TransferDao transferDao, UserDao userDao) {
        this.accountDao = accountDao;
        this.transferDao = transferDao;
        this.userDao = userDao;
    }

    //accounts RequestMethods
    @RequestMapping(path = "/account", method = RequestMethod.GET)
    public Account getUserAccount(Principal principal){
        String userName = principal.getName();
        long userId = userDao.findIdByUsername(userName);
        Account userAccount = accountDao.getAccountByUserId(userId);

        return userAccount;

    }


    // Get a list of all pending transfers


//    @RequestMapping(path = "/transfer", method = RequestMethod.GET)
//    public List<Transfer> list() {
//        return TransferDao.getPendingTransfers();
//    }



    //Get a transfer by transferID

    @RequestMapping( path = "/transfer/{id}", method = RequestMethod.GET)
    public Transfer getTransferById(@PathVariable long id){
        return transferDao.getTransferById(id);
    }


//    //Get a transfer by userId
//
//    @RequestMapping( path = "/transfer/{id}", method = RequestMethod.GET)
//    public Transfer getTransferById(@PathVariable long id){
//        return TransferDao.getTransferById(id);
//    }


    // Create a new transfer

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping( path = "/transfer", method = RequestMethod.POST)
    public Transfer addTransfer(@RequestBody Transfer transfer)  {
        return transferDao.createTransfer(transfer);
    }

//







}
