package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;

import java.util.List;

public interface TransferDao {

    Transfer getTransferById(long transferId);

    List<Transfer> getTransfersFromUserId(long userId);

    List<Transfer> getTransfersToUserId(long userId);

    List<Transfer> getPendingTransfers(long userId);

    Transfer createTransfer(Transfer newTransfer);

    boolean updateStatus(String status, Transfer updatedTransfer);



}
