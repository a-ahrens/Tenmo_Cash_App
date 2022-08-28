package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.Exceptions.Transfer.*;
import com.techelevator.tenmo.Exceptions.Request.*;
import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.RequestDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.*;
import com.techelevator.tenmo.model.Account.Account;
import com.techelevator.tenmo.model.Account.Deposit;
import com.techelevator.tenmo.model.Request.Request;
import com.techelevator.tenmo.model.Request.RequestId;
import com.techelevator.tenmo.model.Request.RequestTransfer;
import com.techelevator.tenmo.model.Transfer.Transfer;
import com.techelevator.tenmo.model.Transfer.TransferId;
import com.techelevator.tenmo.model.Transfer.TransferRequest;
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
    public List<UserAccount> findAll()
    {
        return userDao.findAll();
    }

    //Get list of all other users
    @RequestMapping(path = "/user/other", method = RequestMethod.GET)
    public List<UserAccount> findOtherUsers(Principal principal) {
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

    //Add money to user account balance
    @RequestMapping(path="/account/deposit", method=RequestMethod.PUT)
    public BigDecimal depositMoneyIntoAccount(@RequestBody Deposit deposit, Principal principal){
        Account userAccount = getUserAccount(principal);
        accountDao.addToAccountBalance(userAccount.getAccountId(), deposit.getAmount());

        return accountDao.getBalance(userAccount.getAccountId());
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
    public List<Transfer> listPendingTransfers(Principal principal) {
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

        //Confirm sender balance is >= transferAmount
        if(accountDao.getBalance(approvedTransfer.getFromAccount()).compareTo(approvedTransfer.getTransferAmount()) < 0){
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





    //***********************
    //REQUESTS
    //***********************


    //Get a request history for User (collects all sent and received)
    @RequestMapping( path = "/request", method = RequestMethod.GET)
    public List<Request> getRequests(Principal principal){
        long accountId = getUserAccountId(principal);
        return requestDao.getRequestHistory(accountId);
    }

    //Get list of all requests sent by user
    @RequestMapping( path = "/request/sent", method = RequestMethod.GET)
    public List<Request> getRequestsSent(Principal principal){
        long accountId = getUserAccountId(principal);
        return requestDao.getSentRequestsByAccountId(accountId);
    }

    //get list of all requests received by user
    @RequestMapping( path = "/request/received", method = RequestMethod.GET)
    public List<Request> getRequestsReceived(Principal principal){
        long accountId = getUserAccountId(principal);
        return requestDao.getReceivedRequestsByAccountId(accountId);
    }

    //Get a request by requestID
    @RequestMapping( path = "/request/id", method = RequestMethod.GET)
    public Request getRequestById(@RequestBody RequestId requestId) throws RequestNotFoundException{
        return requestDao.getRequestById(requestId.getRequestId());
    }

    //Get list of all pending requests for user account
    @RequestMapping(path = "/request/pending", method = RequestMethod.GET)
    public List<Request> listPendingRequests(Principal principal) {
        long accountId = getUserAccountId(principal);
        return requestDao.getPendingRequests(accountId);
    }

    @RequestMapping( path = "/request/approve", method = RequestMethod.PUT)
    public Transfer approveRequest(@RequestBody RequestId requestId, Principal principal)
            throws RequestClosedException, RequestNotFoundException, TransferUnsuccessfulException, TransferIdNotFoundException {

        Request approvedRequest = requestDao.getRequestById(requestId.getRequestId());
        Account userAccount = getUserAccount(principal);

        //confirm user has authority to approve
        long accountId = userAccount.getAccountId();
        if(accountId != approvedRequest.getRequestee()){
            throw new RequestNotFoundException();
        }

        //confirm request entry is "pending"
        if(!approvedRequest.getStatus().equals("Pending")){
            throw new RequestClosedException();
        }

        //Confirm requestee balance is >= transferAmount
        if(userAccount.getBalance().compareTo(approvedRequest.getRequestedAmount()) < 0){
            approvedRequest.setStatus("Unsuccessful");
            requestDao.updateStatus(approvedRequest);
            throw new TransferUnsuccessfulException();
        }

        //Process Request Approval
        approvedRequest.setStatus("Approved");
        accountDao.subtractFromAccountBalance(approvedRequest.getRequestee(), approvedRequest.getRequestedAmount());
        accountDao.addToAccountBalance(approvedRequest.getRequester(), approvedRequest.getRequestedAmount());
        requestDao.updateStatus(approvedRequest);

        return transferDao.createTransferFromRequest(approvedRequest);
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

    //Decline a request
    @RequestMapping( path = "/request/decline", method = RequestMethod.PUT)
    public Request declineRequest(@RequestBody RequestId requestId, Principal principal)
            throws RequestClosedException, RequestNotFoundException {

        Request declineRequest = requestDao.getRequestById(requestId.getRequestId());

        //confirm user has authority to decline
        long accountId = getUserAccountId(principal);
        if(accountId != declineRequest.getRequestee()){
            throw new RequestNotFoundException();
        }

        //confirm transfer entry is "Pending"
        if(!declineRequest.getStatus().equals("Pending")){
            throw new RequestClosedException();
        }

        declineRequest.setStatus("Declined");
        requestDao.updateStatus(declineRequest);
        return declineRequest;
    }


    //Cancel a request
    @RequestMapping( path = "/request/cancel", method = RequestMethod.PUT)
    public Request cancelRequest(@RequestBody RequestId requestId, Principal principal)
            throws RequestClosedException, RequestNotFoundException {

        Request cancelRequest = requestDao.getRequestById(requestId.getRequestId());

        //confirm user has authority to cancel
        long accountId = getUserAccountId(principal);
        if(accountId != cancelRequest.getRequester()){
            throw new RequestNotFoundException();
        }

        //confirm transfer entry is "Pending"
        if(!cancelRequest.getStatus().equals("Pending")){
            throw new RequestClosedException();
        }

        cancelRequest.setStatus("Canceled");
        requestDao.updateStatus(cancelRequest);
        return cancelRequest;
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
