package com.techelevator.tenmo.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class Exceptions {

    //----User
        //User Not found
        //Create new User fail
            //Message: UserName Already Exists

            //Create account fail
                // Message: "Account creation failed. Please try registering again."
                // transaction rollback removes user entry that was generated

    //----Account
        //Account not found
        //Balance may not be negative
            //bean Validation not negative

    //----Transfer
        //Transferid not found
            //Message: Transfer not found for this account.
                //EXCEPTION: TransferIdNotFound

        //No transfer History available
            //Message: "No Transfer History Exists for your account"
                //EXCEPTION: TransferHistoryNotEstablished

        //--Creating a transfer
            //Sender AccountId cannot match ReceiverId
                //Message: "Sender AccountId cannot match ReceiverId"
                    //EXCEPTION: TransferInvalidToAccount

            //Transfer Amount is less than or equal current balance in sender account
                //Message: "Transfer declined. Insufficient balance in your account"
                    //EXCEPTION: TransferBalanceError

            //Transfer Amount must be greater 0.01
                //Message: "Minimum transfer amount is 0.01. Please enter a valid amount."
                    //EXCEPTION: Bean Validation thrown

            //Cannot send money to an invalid account
                //throws account not found
                    //EXCEPTION: AccountNotFound

        //Update a status:
            //Ability to access other people's transfers
                //Message: "Transfer not found for this account"
                    //EXCEPTION: TransferIdNotFound

            //Ability to change only pending transfers
                //Message: "Transfer request finalized. No status changes permitted"
                    //EXCEPTION: TransferStatusClosed

            //Balance from sender doesn't have enough funds when approved
                //Message: "Transfer was unsuccessful."
                //Set transfer status inside database to "Unsuccessful"
                    //EXCEPTION: TransferUnsuccessful

    //----Requests
        //RequestId not found
        //No Request History available
            //Message: "No Request History Exists for your account"

        //--Creating a Request
            //Requester AccountId cannot match Requestee Id
                //Message: "Requester AccountId cannot match Requestee Id"
            //Request Amount must be greater 0.01
                //Message: "Minimum request amount is 0.01. Please enter a valid amount."
            //Cannot request money from an invalid account
                //throws account not found

        //Update a status:
            //Ability to access other people's requests
                //Message: "Request does not exist for this account"
            //Ability to change only "pending" request
                //Message: "Request finalized. No status changes permitted"
            //Balance from requestee doesn't have enough funds when approved
                //Message: "Request failed. Transfer was unsuccessful."
                //Set request status inside database to "Unsuccessful"
                //No transfer was created inside the transfer table.



}
