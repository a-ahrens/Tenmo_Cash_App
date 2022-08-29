package com.techelevator.dao;

import com.fasterxml.jackson.databind.ser.Serializers;
import com.techelevator.tenmo.Exceptions.Account.CreateAccountFailException;
import com.techelevator.tenmo.Exceptions.Transfer.TransferIdNotFoundException;
import com.techelevator.tenmo.dao.JdbcTransferDao;
import com.techelevator.tenmo.dao.JdbcUserDao;

import com.techelevator.tenmo.model.Account.Account;

import com.techelevator.tenmo.model.Transfer.Transfer;

import com.techelevator.tenmo.model.Transfer.TransferRequest;
import com.techelevator.tenmo.model.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class JdbcTransferTests extends BaseDaoTests {


    private static final Transfer TRANSFER_1 = new Transfer(3001, 2001, 2002, BigDecimal.valueOf(1000), LocalDateTime.now(), "APPROVED", "Description.");
    private static final Transfer TRANSFER_2 = new Transfer(3002, 2003, 2004, BigDecimal.valueOf(1500), LocalDateTime.now(), "PENDING", "Description.");
    private static final Transfer TRANSFER_3 = new Transfer(3003, 2005, 2006, BigDecimal.valueOf(50), LocalDateTime.now(), "DENIED", "Description.");
    private static final Transfer TRANSFER_4 = new Transfer(3004, 2007, 2008, BigDecimal.valueOf(250), LocalDateTime.now(), "UNSUCCESSFUL", "Description.");


    private static final Account ACCOUNT_1 = new Account(2001, 1001, BigDecimal.valueOf(1000.00));
    private static final Account ACCOUNT_2 = new Account(2002, 1002, BigDecimal.valueOf(500.00));
    private static final Account ACCOUNT_3 = new Account(2003, 1003, BigDecimal.valueOf(1500.00));

    private JdbcTransferDao sut;
private Transfer transferTest;
    @Before
    public void setup() {
        JdbcTemplate jdbcTemplate= new JdbcTemplate(dataSource);
        sut = new JdbcTransferDao(jdbcTemplate);
      //  transferTest = new Transfer(2000, 40, "d");

    }


    //Test if transfer is returned given id
    //Test if ID is not null

    @Test
    public void find_transfer_with_transferId() throws TransferIdNotFoundException {
        Transfer transfer = sut.getTransferById(3001);
        assertTransfersMatch("getTransfer returned wrong or partial data", TRANSFER_1, transfer);
        transfer = sut.getTransferById(3002);
        assertTransfersMatch("getTransfer returned wrong or partial data", TRANSFER_2, transfer);
        transfer = sut.getTransferById(3003);
        assertTransfersMatch("getTransfer returned wrong or partial data", TRANSFER_3, transfer);
        transfer = sut.getTransferById(3004);
        assertTransfersMatch("getTransfer returned wrong or partial data", TRANSFER_4, transfer);

    }

    @Test
    public void getTransfer_returns_null_when_transferId_not_found() throws TransferIdNotFoundException {
        Transfer transfer = sut.getTransferById(99);
        Assert.assertNull("getTransfer failed to return null for id not in database", transfer);
    }

    //Test if transfer history is returned
    //Transfer history is null

    @Test
    public void get_transfer_history() {
        List<Transfer> transfers = sut.getTransferHistory(3000);
        Assert.assertNotNull(transfers);
        Assert.assertEquals(3, transfers.size());
        Assert.assertEquals(TRANSFER_1, transfers.get(0));
        Assert.assertEquals(TRANSFER_2, transfers.get(1));
        Assert.assertEquals(TRANSFER_3, transfers.get(2));
    }

    @Test
    public void transferId_returns_null_when_id_not_found() throws TransferIdNotFoundException {
        Transfer transfer = sut.getTransferById(99);
        Assert.assertNull(transfer);
    }


    //Test if sent* transfers are returned
    //Test it shows expected details

    @Test
    public void get_sent_transfers_by_transferId(){
    List<Transfer> sentTransfers = sut.getSentTransfersByAccountId(3000);
        Assert.assertNotNull(sentTransfers);
        Assert.assertEquals(3, sentTransfers.size());
        Assert.assertEquals(TRANSFER_1, sentTransfers.get(0));
        Assert.assertEquals(TRANSFER_2, sentTransfers.get(1));
        Assert.assertEquals(TRANSFER_3, sentTransfers.get(2));
}

        @Test
        public void sent_transfers_show_expected_values_by_ID() {
    List<Transfer> sentTransfers = sut.getSentTransfersByAccountId(2000);

    Assert.assertEquals(3, sentTransfers.size());
    assertTransfersMatch("SentTransfers returned wrong or partial data", TRANSFER_1, sentTransfers.get(0));
    assertTransfersMatch("SentTransfers returned wrong or partial data", TRANSFER_2, sentTransfers.get(1));
    assertTransfersMatch("SentTransfers returned wrong or partial data", TRANSFER_3, sentTransfers.get(2));

    sentTransfers = sut.getSentTransfersByAccountId(2003);
    Assert.assertEquals(1, sentTransfers.size());
    assertTransfersMatch("Timesheets returned wrong or partial data", TRANSFER_1, sentTransfers.get(3001));
}

    //Test received* transfers are returned
    //Test it shows expected details

    @Test
    public void get_received_transfers_by_transferId(){
        List<Transfer> receivedTransfers = sut.getReceivedTransfersByAccountId(3000);
        Assert.assertNotNull(receivedTransfers);
        Assert.assertEquals(3, receivedTransfers.size());
        Assert.assertEquals(TRANSFER_1, receivedTransfers.get(0));
        Assert.assertEquals(TRANSFER_2, receivedTransfers.get(1));
        Assert.assertEquals(TRANSFER_3, receivedTransfers.get(2));
    }

    @Test
    public void received_transfers_show_expected_values_by_ID() {
        List<Transfer> receivedTransfers = sut.getReceivedTransfersByAccountId(2000);

        Assert.assertEquals(3, receivedTransfers.size());
        assertTransfersMatch("ReceivedTransfers returned wrong or partial data", TRANSFER_1, receivedTransfers.get(0));
        assertTransfersMatch("ReceivedTransfers returned wrong or partial data", TRANSFER_2, receivedTransfers.get(1));
        assertTransfersMatch("ReceivedTransfers returned wrong or partial data", TRANSFER_3, receivedTransfers.get(2));

        receivedTransfers = sut.getReceivedTransfersByAccountId(2003);
        Assert.assertEquals(1, receivedTransfers.size());
        assertTransfersMatch("ReceivedTransfers returned wrong or partial data", TRANSFER_1, receivedTransfers.get(3001));
    }

    //Test pending* transfers are returned
    //Test if account ID is used
@Test
            public void pending_transfers_show_expected_values() {
    List<Transfer> pendingTransfers = sut.getPendingTransfers(3000);
    Assert.assertNotNull(pendingTransfers);
    Assert.assertEquals(3, pendingTransfers.size());
    Assert.assertEquals(TRANSFER_1, pendingTransfers.get(0));
    Assert.assertEquals(TRANSFER_2, pendingTransfers.get(1));
    Assert.assertEquals(TRANSFER_3, pendingTransfers.get(2));
    pendingTransfers = sut.getPendingTransfers(2004);
    Assert.assertEquals(1, pendingTransfers.size());
    assertTransfersMatch("PendingTransfers returned wrong or partial data", TRANSFER_1, pendingTransfers.get(3001));
}

    @Test
    public void get_pending_transfers_by_accountId(){
        List<Transfer> pendingTransfers = sut.getPendingTransfers(2003);
        Assert.assertNotNull(pendingTransfers);
        Assert.assertEquals(1, pendingTransfers.size());
        Assert.assertEquals(TRANSFER_2, pendingTransfers.get(1));

    }



    //Test if transfer is created
    //Test if fromAccountId is used

    @Test
    public void create_transfer_created_transfer() {
//        Transfer transfer = sut.createTransfer(2000);
//
//        Assert.assertNotNull("createTimesheet returned null", createdTimesheet);
//
//        Integer newId = createdTimesheet.getTimesheetId();
//        Assert.assertTrue("createProject failed to return a project with an id", newId > 0);
//
//        testTimeSheet.setTimesheetId(newId);
//        assertTimesheetsMatch("createProject returned project with wrong or partial data", testTimeSheet, createdTimesheet);
//
//    }
//        @Test
//        public void transfer_not_created_returns_null() {
//            Transfer createdTransfer = new Transfer(3001, 2001, 2002, BigDecimal.valueOf(1000), LocalDateTime.now(), "APPROVED", "Description.");
//            Assert.assertNull(createdTransfer);
//
//        }


        //Test if transfer is initiated





        //Test if transferId is found
        //Test if status is null





    }

    private void assertTransfersMatch(String s, Transfer expected, Transfer actual) {
        Assert.assertEquals(expected.getTransferId(), actual.getTransferId());
        Assert.assertEquals(expected.getFromAccount(), actual.getFromAccount());
        Assert.assertEquals(expected.getToAccount(), actual.getToAccount());
        Assert.assertEquals(expected.getTransferAmount(), actual.getTransferAmount());
        Assert.assertEquals(expected.getTimeStamp(), actual.getTimeStamp());
        Assert.assertEquals(expected.getStatus(), actual.getStatus());


    }




}
