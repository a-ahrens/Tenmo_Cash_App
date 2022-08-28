package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.Exceptions.Request.RequestNotFoundException;
import com.techelevator.tenmo.model.Request.Request;
import com.techelevator.tenmo.model.Request.RequestTransfer;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcRequestDao implements RequestDao{

    private JdbcTemplate jdbcTemplate;
    public JdbcRequestDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Request getRequestById(long requestId) {
        Request request = null;
        String sql = "SELECT request_id, requester, requestee, requested_amount, " +
                     "time_stamp, status, description " +
                     "FROM request " +
                     "WHERE request_id = ?;";

        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, requestId);
        if (results.next()) {
            request = mapRowToRequest(results);
        }
        return request;
    }

    @Override
    public List<Request> getRequestHistory(long accountId) {
        List<Request> requests = new ArrayList<>();
        String sql = "SELECT request_id, requester, requestee, requested_amount, " +
                     "time_stamp, status, description " +
                     "FROM request " +
                     "WHERE requester = ? OR requestee = ?;";

        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, accountId, accountId);
        while(results.next()) {
            requests.add(mapRowToRequest(results));
        }
        return requests;
    }

    @Override
    public List<Request> getSentRequestsByAccountId(long accountId) {
        List<Request> requests = new ArrayList<>();
        String sql = "SELECT request_id, requester, requestee, requested_amount, " +
                     "time_stamp, status, description " +
                     "FROM request " +
                     "WHERE requester = ?;";

        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, accountId);
        while(results.next()) {
            requests.add(mapRowToRequest(results));
        }
        return requests;
    }

    @Override
    public List<Request> getReceivedRequestsByAccountId(long accountId) {
        List<Request> requests = new ArrayList<>();
        String sql = "SELECT request_id, requester, requestee, requested_amount, " +
                     "time_stamp, status, description " +
                     "FROM request " +
                     "WHERE requestee = ?;";

        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, accountId);
        while(results.next()) {
            requests.add(mapRowToRequest(results));
        }
        return requests;
    }

    @Override
    public List<Request> getPendingRequests(long accountId) {
        List<Request> requests = new ArrayList<>();
        String sql = "SELECT request_id, requester, requestee, requested_amount, " +
                "time_stamp, status, description " +
                "FROM request " +
                "WHERE (requester = ? OR requestee = ? ) AND status LIKE 'Pending' " +
                "ORDER BY requester";

        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, accountId, accountId);
        while(results.next()) {
            requests.add(mapRowToRequest(results));
        }
        return requests;
    }

    @Override
    public Request createRequest(long requesterAccountId, RequestTransfer newRequest) {
        String sql = "INSERT INTO request (requester, requestee, requested_amount, description) " +
                     "VALUES (?, ?, ?, ?) " +
                     "RETURNING request_id;";
        Long requestId = 0L;
        try {
            requestId = jdbcTemplate.queryForObject(sql, Long.class, requesterAccountId, newRequest.getRequestee(),
                    newRequest.getRequestedAmount(), newRequest.getDescription());
        } catch (DataAccessException e) {
            System.out.println("DataAccessException");
        }
        return getRequestById(requestId);
    }

    @Override
    public boolean updateStatus(Request updatedRequest) throws RequestNotFoundException {
        String sql = "UPDATE request " +
                     "SET status = ? " +
                     "WHERE request_id = ?;";
        if (getRequestById(updatedRequest.getRequestId()) != null) {
            jdbcTemplate.update(sql, updatedRequest.getStatus(), updatedRequest.getRequestId());
            return true;
        }
        throw new RequestNotFoundException();
    }

    private Request mapRowToRequest(SqlRowSet rs) {
        Request request = new Request();
        request.setRequestId(rs.getLong("request_id"));
        request.setRequester(rs.getLong("requester"));
        request.setRequestee(rs.getLong("requestee"));
        request.setRequestedAmount(rs.getBigDecimal("requested_amount"));
        request.setTimeStamp(rs.getTimestamp("time_stamp").toLocalDateTime());
        request.setStatus(rs.getString("status"));
        request.setDescription(rs.getString("description"));

        return request;

    }

}
