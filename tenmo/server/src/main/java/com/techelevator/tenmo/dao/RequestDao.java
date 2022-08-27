package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.Exceptions.Request.RequestNotFoundException;
import com.techelevator.tenmo.model.Request;
import com.techelevator.tenmo.model.RequestTransfer;

import java.util.List;

public interface RequestDao {

    Request getRequestById(long requestId);

    List<Request> getRequestHistory(long accountId);

    List<Request> getSentRequestsByAccountId(long accountId);

    List<Request> getReceivedRequestsByAccountId(long accountId);

    List<Request> getPendingRequests(long accountId);

    Request createRequest(long requestorAccountId , RequestTransfer newRequest);

    boolean updateStatus(Request updatedRequest) throws RequestNotFoundException;

}
