package com.techelevator.tenmo.controller;


import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferId;
import com.techelevator.tenmo.model.TransferRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
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

    //Get a transfer history for User (collects all sent and received)
    @RequestMapping( path = "/transfer", method = RequestMethod.GET)
    public List<Transfer> getTransfers(Principal principal){
        List<Transfer> transfers = new ArrayList<>();

        String userName = principal.getName();
        long userId = userDao.findIdByUsername(userName);

        Account userAccount = accountDao.getAccountByUserId(userId);
        long accountId = userAccount.getAccountId();

        transfers = transferDao.getTransferHistory(accountId);

        return transfers;

    }

    @RequestMapping( path = "/transfer/sent", method = RequestMethod.GET)
    public List<Transfer> getTransfersSent(Principal principal){
        List<Transfer> transfers = new ArrayList<>();

        String userName = principal.getName();
        long userId = userDao.findIdByUsername(userName);

        Account userAccount = accountDao.getAccountByUserId(userId);
        long accountId = userAccount.getAccountId();

        transfers = transferDao.getSentTransfersByAccountId(accountId);

        return transfers;

    }

    @RequestMapping( path = "/transfer/received", method = RequestMethod.GET)
    public List<Transfer> getTransfersReceived(Principal principal){
        List<Transfer> transfers = new ArrayList<>();

        String userName = principal.getName();
        long userId = userDao.findIdByUsername(userName);

        Account userAccount = accountDao.getAccountByUserId(userId);
        long accountId = userAccount.getAccountId();

        transfers = transferDao.getReceivedTransfersByAccountId(accountId);

        return transfers;
    }

    //Get a list of all pending transfers for user account
    @RequestMapping(path = "/transfer/pending", method = RequestMethod.GET)
    public List<Transfer> list(Principal principal) {
        List<Transfer> pendingTransfers = new ArrayList<>();

        String userName = principal.getName();
        long userId = userDao.findIdByUsername(userName);

        Account userAccount = accountDao.getAccountByUserId(userId);
        long accountId = userAccount.getAccountId();

        pendingTransfers = transferDao.getPendingTransfers(accountId);

        return pendingTransfers;
    }

    //Get a transfer by transferID
    @RequestMapping( path = "/transfer/id", method = RequestMethod.GET)
    public Transfer getTransferById(@RequestBody TransferId transferId){
        return transferDao.getTransferById(transferId.getTransferId());
    }

    // Create a new transfer
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping( path = "/transfer", method = RequestMethod.POST)
    public Transfer addTransfer(@Valid @RequestBody TransferRequest transferRequest, Principal principal)  {

        String userName = principal.getName();
        long userId = userDao.findIdByUsername(userName);
        Account userAccount = accountDao.getAccountByUserId(userId);

        if(userAccount.getBalance().compareTo(transferRequest.getTransferAmount()) >= 0){
            accountDao.subtractFromAccountBalance(userAccount.getAccountId(), transferRequest.getTransferAmount());
            accountDao.addToAccountBalance(transferRequest.getToAccount(), transferRequest.getTransferAmount());
            return transferDao.createTransfer(userAccount.getAccountId() ,transferRequest);
        }

        //check value in current from account
            //confirm value fromAccount.getBalance >= transferRequest.getTransfer_Amount
            //confirm from_account =! to_account
            //transfer > 0;

    return null;
    }

//







}
