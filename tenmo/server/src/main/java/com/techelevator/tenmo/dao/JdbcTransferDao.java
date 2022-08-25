package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.util.ArrayList;
import java.util.List;

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
                "WHERE transfer_id= ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, transferId);
        if (results.next()) {
            transfer = mapRowToTransfer(results);
        }
        return transfer;
    }

    @Override
    public List<Transfer> getTransfersFromUserId(long userId) {
        List<Transfer> transfers = new ArrayList<>();
        String sql = "SELECT transfer_id, from_account, to_account, transfer_amount, " +
                "time_stamp, status " +
                "FROM transfer " +
                "JOIN account ON account.account_id= transfer.from_account " +
                "JOIN tenmo_user ON tenmo_user.user_id= account.user_id " +
                "WHERE tenmo_user.user_id= ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
        while (results.next()) {
            Transfer transfer = mapRowToTransfer(results);
            transfers.add(transfer);
        }
        return transfers;
    }


    @Override
    public List<Transfer> getTransfersToUserId(long userId) {
        List<Transfer> transfers = new ArrayList<>();
        String sql = "SELECT transfer_id, from_account, to_account, transfer_amount, " +
                "time_stamp, status " +
                "FROM transfer " +
                "JOIN account ON account.account_id= transfer.to_account " +
                "JOIN tenmo_user ON tenmo_user.user_id= account.user_id " +
                "WHERE tenmo_user.user_id= ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
        while (results.next()) {
            Transfer transfer = mapRowToTransfer(results);
            transfers.add(transfer);
        }
        return transfers;
    }


    @Override
    public List<Transfer> getPendingTransfers(long userId) {
        List<Transfer> pendingTransfers = new ArrayList<>();
        String sql = "SELECT transfer_id, from_account, to_account, transfer_amount, " +
                "time_stamp, status " +
                "FROM transfer " +
                "JOIN account ON account.account_id= transfer.to_account " +
                "JOIN tenmo_user ON tenmo_user.user_id= account.user_id " +
                "WHERE tenmo_user.user_id= ?  AND status LIKE 'pending' ;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
        while (results.next()) {
            Transfer transfer = mapRowToTransfer(results);
            pendingTransfers.add(transfer);
        }
        return pendingTransfers;
    }

    @Override
    public Transfer createTransfer(Transfer newTransfer) {
        String sql = "INSERT INTO transfer(from_account, to_account," +
                "transfer_amount, time_stamp, status) VALUES (?, ?, ?, ?, ?) " +
                "RETURNING transfer_id;";
        Long transferId = new Long(0);
        try {
            transferId = jdbcTemplate.queryForObject(sql, Long.class, newTransfer.getFromAccount(),
                    newTransfer.getToAccount(), newTransfer.getTransferAmount(),
                    newTransfer.getTimeStamp(), newTransfer.getStatus());
        } catch (DataAccessException e) {
            System.out.println("DataAccessException");
        }
        return getTransferById(transferId);
    }

    @Override
    public boolean updateStatus(String status, Transfer updatedTransfer) {
        String sql = "UPDATE transfer " +
                "SET status = ? " +
                "WHERE transfer_id = ?";
        if (getTransferById(updatedTransfer.getTransferId()) != null) {
            jdbcTemplate.update(sql, status, updatedTransfer.getTransferId());
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
        transfer.setTimeStamp(rs.getTimestamp("timestamp").toLocalDateTime());
        transfer.setStatus(rs.getString("status"));
return transfer;

    }




}
