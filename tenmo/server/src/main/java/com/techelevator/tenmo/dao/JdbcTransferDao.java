package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferRequest;
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
    public Transfer getTransferById(long transferId) {
        Transfer transfer = null;
        String sql = "SELECT transfer_id, from_account, to_account, transfer_amount, " +
                "time_stamp, status " +
                "FROM transfer " +
                "WHERE transfer_id = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, transferId);
        if (results.next()) {
            transfer = mapRowToTransfer(results);
        }
        return transfer;
    }

    @Override
    public List<Transfer> getTransferHistory(long accountId) {
        List<Transfer> transfers = new ArrayList<>();
        String sql = "SELECT transfer_id, from_account, to_account, transfer_amount, " +
                "time_stamp, status " +
                "FROM transfer " +
                "WHERE from_account = ? OR to_account = ?; ";

        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, accountId, accountId);
        while (results.next()) {
            Transfer transfer = mapRowToTransfer(results);
            transfers.add(transfer);
        }
        return transfers;
    }


    @Override
    public List<Transfer> getSentTransfersByAccountId(long accountId) {
        List<Transfer> sentTransfers = new ArrayList<>();
        String sql = "SELECT transfer_id, from_account, to_account, transfer_amount, " +
                "time_stamp, status " +
                "FROM transfer " +
                "WHERE from_account = ?; ";

        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, accountId);
        while (results.next()) {
            Transfer transfer = mapRowToTransfer(results);
            sentTransfers.add(transfer);
        }
        return sentTransfers;
    }


    @Override
    public List<Transfer> getReceivedTransfersByAccountId(long accountId) {
        List<Transfer> receivedTransfers = new ArrayList<>();
        String sql = "SELECT transfer_id, from_account, to_account, transfer_amount, " +
                "time_stamp, status " +
                "FROM transfer " +
                "WHERE to_account = ?; ";

        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, accountId);
        while (results.next()) {
            Transfer transfer = mapRowToTransfer(results);
            receivedTransfers.add(transfer);
        }
        return receivedTransfers;
    }


    @Override
    public List<Transfer> getPendingTransfers(long accountId) {
        List<Transfer> pendingTransfers = new ArrayList<>();
        String sql = "SELECT transfer_id, from_account, to_account, transfer_amount, " +
                "time_stamp, status " +
                "FROM transfer " +
                "WHERE (from_account = ? OR to_account = ?) AND status LIKE 'Pending'" +
                "ORDER BY from_account"  ;

        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, accountId, accountId);
        while (results.next()) {
            Transfer transfer = mapRowToTransfer(results);
            pendingTransfers.add(transfer);
        }
        return pendingTransfers;
    }

    @Override
    public Transfer createTransfer(long fromAccountId ,TransferRequest newTransfer) {
        String sql = "INSERT INTO transfer(from_account, to_account," +
                "transfer_amount) VALUES (?, ?, ?) " +
                "RETURNING transfer_id;";
        Long transferId = 0L;
        try {
            transferId = jdbcTemplate.queryForObject(sql, Long.class, fromAccountId,
                    newTransfer.getToAccount(), newTransfer.getTransferAmount());
        } catch (DataAccessException e) {
            System.out.println("DataAccessException");
        }
        return getTransferById(transferId);
    }

    @Override
    public boolean updateStatus(Transfer updatedTransfer) {
        String sql = "UPDATE transfer " +
                "SET status = ? " +
                "WHERE transfer_id = ?";
        if (getTransferById(updatedTransfer.getTransferId()) != null) {
            jdbcTemplate.update(sql, updatedTransfer.getStatus(), updatedTransfer.getTransferId());
            return true;
        }

        return false;
    }

    private Transfer mapRowToTransfer(SqlRowSet rs) {
        Transfer transfer = new Transfer();
        transfer.setTransferId(rs.getLong("transfer_id"));
        transfer.setFromAccount(rs.getLong("from_account"));
        transfer.setToAccount(rs.getLong("to_account"));
        transfer.setTransferAmount(rs.getBigDecimal("transfer_amount"));
        transfer.setTimeStamp(rs.getTimestamp("time_stamp").toLocalDateTime());
        transfer.setStatus(rs.getString("status"));
return transfer;

    }




}
