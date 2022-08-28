package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.Exceptions.Transfer.TransferIdNotFoundException;
import com.techelevator.tenmo.model.Request.Request;
import com.techelevator.tenmo.model.Transfer.Transfer;
import com.techelevator.tenmo.model.Transfer.TransferRequest;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransferDao implements TransferDao {

    private JdbcTemplate jdbcTemplate;
    public JdbcTransferDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Transfer getTransferById(long transferId) throws TransferIdNotFoundException{
        Transfer transfer = null;
        String sql = "SELECT transfer_id, from_account, to_account, transfer_amount, " +
                     "time_stamp, status, description " +
                     "FROM transfer " +
                     "WHERE transfer_id = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, transferId);
        if (results.next()) {
            return mapRowToTransfer(results);
        }
        throw new TransferIdNotFoundException();
    }

    @Override
    public List<Transfer> getTransferHistory(long accountId) {
        List<Transfer> transfers = new ArrayList<>();
        String sql = "SELECT transfer_id, from_account, to_account, transfer_amount, " +
                     "time_stamp, status, description " +
                     "FROM transfer " +
                     "WHERE from_account = ? OR to_account = ?; ";

        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, accountId, accountId);
        while (results.next()) {
            transfers.add(mapRowToTransfer(results));
        }
        return transfers;
    }


    @Override
    public List<Transfer> getSentTransfersByAccountId(long accountId) {
        List<Transfer> sentTransfers = new ArrayList<>();
        String sql = "SELECT transfer_id, from_account, to_account, transfer_amount, " +
                     "time_stamp, status, description " +
                     "FROM transfer " +
                     "WHERE from_account = ?; ";

        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, accountId);
        while (results.next()) {
            sentTransfers.add(mapRowToTransfer(results));
        }
        return sentTransfers;
    }


    @Override
    public List<Transfer> getReceivedTransfersByAccountId(long accountId) {
        List<Transfer> receivedTransfers = new ArrayList<>();
        String sql = "SELECT transfer_id, from_account, to_account, transfer_amount, " +
                     "time_stamp, status, description " +
                     "FROM transfer " +
                     "WHERE to_account = ?; ";

        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, accountId);
        while (results.next()) {
            receivedTransfers.add(mapRowToTransfer(results));
        }
        return receivedTransfers;
    }


    @Override
    public List<Transfer> getPendingTransfers(long accountId) {
        List<Transfer> pendingTransfers = new ArrayList<>();
        String sql = "SELECT transfer_id, from_account, to_account, transfer_amount, " +
                     "time_stamp, status, description " +
                     "FROM transfer " +
                     "WHERE (from_account = ? OR to_account = ?) AND status LIKE 'Pending'" +
                     "ORDER BY from_account"  ;

        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, accountId, accountId);
        while (results.next()) {
            pendingTransfers.add(mapRowToTransfer(results));
        }
        return pendingTransfers;
    }

    @Override
    public Transfer createTransfer(long fromAccountId ,TransferRequest newTransfer) throws TransferIdNotFoundException{
        String sql = "INSERT INTO transfer(from_account, to_account, " +
                     "transfer_amount, description) VALUES (?, ?, ?, ?) " +
                     "RETURNING transfer_id;";
        Long transferId = 0L;
        try {
            transferId = jdbcTemplate.queryForObject(sql, Long.class, fromAccountId, newTransfer.getToAccount(),
                    newTransfer.getTransferAmount(), newTransfer.getDescription());
        } catch (DataAccessException e) {
            System.out.println("DataAccessException");
        }
        return getTransferById(transferId);
    }

    @Override
    public Transfer createTransferFromRequest(Request newTransfer) throws TransferIdNotFoundException{
        String sql = "INSERT INTO transfer(from_account, to_account, " +
                     "transfer_amount, status, description) VALUES (?, ?, ?, ?, ?) " +
                     "RETURNING transfer_id;";
        Long transferId = 0L;
        try {
            transferId = jdbcTemplate.queryForObject(sql, Long.class, newTransfer.getRequestee(), newTransfer.getRequester(),
                    newTransfer.getRequestedAmount(), newTransfer.getStatus(), newTransfer.getDescription());
        } catch (DataAccessException e) {
            System.out.println("DataAccessException");
        }
        return getTransferById(transferId);
    }


    @Override
    public boolean updateStatus(Transfer updatedTransfer) throws TransferIdNotFoundException {
        String sql = "UPDATE transfer " +
                     "SET status = ? " +
                     "WHERE transfer_id = ?";
        if (getTransferById(updatedTransfer.getTransferId()) != null) {
            jdbcTemplate.update(sql, updatedTransfer.getStatus(), updatedTransfer.getTransferId());
            return true;
        }
        throw new TransferIdNotFoundException();
    }

    private Transfer mapRowToTransfer(SqlRowSet rs) {
        Transfer transfer = new Transfer();
        transfer.setTransferId(rs.getLong("transfer_id"));
        transfer.setFromAccount(rs.getLong("from_account"));
        transfer.setToAccount(rs.getLong("to_account"));
        transfer.setTransferAmount(rs.getBigDecimal("transfer_amount"));
        transfer.setTimeStamp(rs.getTimestamp("time_stamp").toLocalDateTime());
        transfer.setStatus(rs.getString("status"));
        transfer.setDescription(rs.getString("description"));

        return transfer;

    }




}
