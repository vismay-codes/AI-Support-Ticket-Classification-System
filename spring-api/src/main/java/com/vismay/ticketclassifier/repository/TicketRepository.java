package com.vismay.ticketclassifier.repository;

import com.vismay.ticketclassifier.model.TicketResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public class TicketRepository {
    private final JdbcTemplate jdbcTemplate;

    public TicketRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public TicketResponse save(String ticket, String category, double confidence) {
        String id = UUID.randomUUID().toString();
        LocalDateTime createdAt = LocalDateTime.now();

        jdbcTemplate.update(
                "INSERT INTO SUPPORT_TICKETS " +
                        "(TICKET_ID, TICKET_TEXT, CATEGORY, CONFIDENCE, CREATED_AT) VALUES (?, ?, ?, ?, ?)",
                id, ticket, category, confidence, Timestamp.valueOf(createdAt));

        return new TicketResponse(id, ticket, category, confidence, createdAt);
    }

    public List<TicketResponse> findAll() {
        String sql = "SELECT TICKET_ID, TICKET_TEXT, CATEGORY, CONFIDENCE, CREATED_AT " +
                "FROM SUPPORT_TICKETS ORDER BY CREATED_AT DESC";

        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new TicketResponse(
                        rs.getString("TICKET_ID"),
                        rs.getString("TICKET_TEXT"),
                        rs.getString("CATEGORY"),
                        rs.getDouble("CONFIDENCE"),
                        rs.getTimestamp("CREATED_AT").toLocalDateTime()));
    }

    public long count() {
        Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM SUPPORT_TICKETS", Long.class);
        return count == null ? 0L : count;
    }
}
