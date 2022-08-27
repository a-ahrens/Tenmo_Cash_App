package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.Exceptions.Transfer.TransferIdNotFoundException;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferRequest;

import java.util.List;

public interface TransferDao {

    Transfer getTransferById(long transferId) throws TransferIdNotFoundException;

    List<Transfer> getTransferHistory(long userId);

    List<Transfer> getSentTransfersByAccountId(long accountId);

    List<Transfer> getReceivedTransfersByAccountId(long accountId);

    List<Transfer> getPendingTransfers(long accountId);

    Transfer createTransfer(long fromAccountId ,TransferRequest newTransfer) throws TransferIdNotFoundException;

    boolean updateStatus(Transfer updatedTransfer) throws TransferIdNotFoundException;



}
