package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferRequest;

import java.util.List;

public interface TransferDao {

    Transfer getTransferById(long transferId);

    List<Transfer> getTransferHistory(long userId);

    List<Transfer> getSentTransfersByAccountId(long accountId);

    List<Transfer> getReceivedTransfersByAccountId(long accountId);

    List<Transfer> getPendingTransfers(long accountId);

    Transfer createTransfer(long fromAccountId ,TransferRequest newTransfer);

    boolean updateStatus(Transfer updatedTransfer);



}
