package com.vismay.ticketclassifier.model;

import java.time.LocalDateTime;

public record TicketResponse(
        String ticketId,
        String ticket,
        String category,
        Double confidence,
        LocalDateTime createdAt) {
}
