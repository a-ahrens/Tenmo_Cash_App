package com.techelevator.tenmo.controller;



import com.techelevator.tenmo.Exceptions.Request.RequestNotFoundException;
import com.techelevator.tenmo.Exceptions.Request.RequestSameAccountException;
import com.techelevator.tenmo.Exceptions.Transfer.*;
import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.RequestDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@RestController
public class AccountController {

    private AccountDao accountDao;
    private TransferDao transferDao;
    private UserDao userDao;
    private RequestDao requestDao;

    public AccountController(AccountDao accountDao, TransferDao transferDao, RequestDao requestDao, UserDao userDao) {
        this.accountDao = accountDao;
        this.transferDao = transferDao;
        this.requestDao = requestDao;
        this.userDao = userDao;
    }

    //Get list of all users including user
    @RequestMapping(path = "/user/all", method=RequestMethod.GET)
    public List<User> findAll()
    {
        return userDao.findAll();
    }

    //Get list of all other users
    @RequestMapping(path = "/user", method = RequestMethod.GET)
    public List<User> findOtherUsers(Principal principal) {
        String userName = principal.getName();
    long userId = userDao.findIdByUsername(userName);

    return userDao.findOtherUsers(userId);
}

    //Get all user account info
    @RequestMapping(path = "/account", method = RequestMethod.GET)
    public Account getUserAccountInfo(Principal principal){
        String userName = principal.getName();
        long userId = userDao.findIdByUsername(userName);
        Account userAccount = accountDao.getAccountByUserId(userId);

        return userAccount;
    }

    //Get user account balance specifically
    @RequestMapping(path="/account/balance", method=RequestMethod.GET)
    public BigDecimal getBalance(Principal principal){
        Account userAccount = getUserAccount(principal);
         return userAccount.getBalance();
    }

    //Get a transfer history for User (collects all sent and received)
    @RequestMapping( path = "/transfer", method = RequestMethod.GET)
    public List<Transfer> getTransfers(Principal principal){
        long accountId = getUserAccountId(principal);
        return transferDao.getTransferHistory(accountId);
    }

    //Get list of all transfers sent by user
    @RequestMapping( path = "/transfer/sent", method = RequestMethod.GET)
    public List<Transfer> getTransfersSent(Principal principal){
        long accountId = getUserAccountId(principal);
        return transferDao.getSentTransfersByAccountId(accountId);
    }

    //get list of all transfers received by user
    @RequestMapping( path = "/transfer/received", method = RequestMethod.GET)
    public List<Transfer> getTransfersReceived(Principal principal){
        long accountId = getUserAccountId(principal);
        return transferDao.getReceivedTransfersByAccountId(accountId);
    }

    //Get list of all pending transfers for user account
    @RequestMapping(path = "/transfer/pending", method = RequestMethod.GET)
    public List<Transfer> list(Principal principal) {
        long accountId = getUserAccountId(principal);
        return transferDao.getPendingTransfers(accountId);
    }

    //Get a transfer by transferID
    @RequestMapping( path = "/transfer/id", method = RequestMethod.GET)
    public Transfer getTransferById(@RequestBody TransferId transferId) throws TransferIdNotFoundException{
        return transferDao.getTransferById(transferId.getTransferId());
    }

    @RequestMapping( path = "/transfer/approve", method = RequestMethod.PUT)
    public Transfer approveTransfer(@RequestBody TransferId transferId, Principal principal)
            throws TransferClosedException, TransferIdNotFoundException, TransferUnsuccessfulException {

        Transfer approvedTransfer= transferDao.getTransferById(transferId.getTransferId());
        Account userAccount = getUserAccount(principal);

        //confirm user has authority to approve
        long accountId = userAccount.getAccountId();
        if(accountId != approvedTransfer.getToAccount()){
            throw new TransferIdNotFoundException();
        }

        //confirm Transfer entry is "pending"
        if(!approvedTransfer.getStatus().equals("Pending")){
            throw new TransferClosedException();
        }

        //Confirm balance is >= transferAmount
        if(userAccount.getBalance().compareTo(approvedTransfer.getTransferAmount()) < 0){
            approvedTransfer.setStatus("Unsuccessful");
            transferDao.updateStatus(approvedTransfer);
            throw new TransferUnsuccessfulException();
        }

        //Process Transfer Approval
        approvedTransfer.setStatus("Approved");
        accountDao.subtractFromAccountBalance(approvedTransfer.getFromAccount(), approvedTransfer.getTransferAmount());
        accountDao.addToAccountBalance(approvedTransfer.getToAccount(), approvedTransfer.getTransferAmount());
        transferDao.updateStatus(approvedTransfer);

        return approvedTransfer;
    }

    @RequestMapping( path = "/transfer/decline", method = RequestMethod.PUT)
    public Transfer declineTransfer(@RequestBody TransferId transferId, Principal principal)
                                                    throws TransferClosedException, TransferIdNotFoundException {

        Transfer declineTransfer= transferDao.getTransferById(transferId.getTransferId());

        //confirm user has authority to decline
        long accountId = getUserAccountId(principal);
        if(accountId != declineTransfer.getToAccount()){
            throw new TransferIdNotFoundException();
        }

        //confirm transfer entry is "Pending"
        if(!declineTransfer.getStatus().equals("Pending")){
            throw new TransferClosedException();
        }

        declineTransfer.setStatus("Declined");
        transferDao.updateStatus(declineTransfer);
        return declineTransfer;
    }

    @RequestMapping( path = "/transfer/cancel", method = RequestMethod.PUT)
    public Transfer cancelTransfer(@RequestBody TransferId transferId, Principal principal)
            throws TransferClosedException, TransferIdNotFoundException {

        Transfer cancelTransfer= transferDao.getTransferById(transferId.getTransferId());

        //confirm user has authority to cancel
        long accountId = getUserAccountId(principal);
        if(accountId != cancelTransfer.getFromAccount()){
            throw new TransferIdNotFoundException();
        }

        //confirm transfer entry is "Pending"
        if(!cancelTransfer.getStatus().equals("Pending")){
            throw new TransferClosedException();
        }

        cancelTransfer.setStatus("Canceled");
        transferDao.updateStatus(cancelTransfer);
        return cancelTransfer;
    }


    // Create a new transfer
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping( path = "/transfer", method = RequestMethod.POST)
    public Transfer addTransfer(@Valid @RequestBody TransferRequest transferRequest, Principal principal)
                                    throws TransferUnsuccessfulException, TransferSameAccountException, TransferIdNotFoundException{

        Account userAccount = getUserAccount(principal);

        //Confirm balance is >= transferAmount
        if(userAccount.getBalance().compareTo(transferRequest.getTransferAmount()) < 0){
            throw new TransferUnsuccessfulException();
        }
        //Confirm the sender isnt the receiver
        if(userAccount.getAccountId() == transferRequest.getToAccount()){
            throw new TransferSameAccountException();
        }

        return transferDao.createTransfer(userAccount.getAccountId() ,transferRequest);
    }

    //Create a new request
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping( path = "/request", method = RequestMethod.POST)
    public Request addRequest(@Valid @RequestBody RequestTransfer requestTransfer, Principal principal)
            throws RequestSameAccountException, RequestNotFoundException {

        Account userAccount = getUserAccount(principal);

        //Confirm the sender isnt the receiver
        if(userAccount.getAccountId() == requestTransfer.getRequestee()){
            throw new RequestSameAccountException();
        }
        return requestDao.createRequest(userAccount.getAccountId(), requestTransfer);
    }


    public Account getUserAccount(Principal principal){
        String userName = principal.getName();
        long userId = userDao.findIdByUsername(userName);
        return accountDao.getAccountByUserId(userId);
    }

    public Long getUserAccountId(Principal principal){
        String userName = principal.getName();
        long userId = userDao.findIdByUsername(userName);
        Account userAccount = accountDao.getAccountByUserId(userId);
        return userAccount.getAccountId();
    }


}
