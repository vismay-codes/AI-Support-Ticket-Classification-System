package com.vismay.ticketclassifier.model;

import jakarta.validation.constraints.NotBlank;

public record TicketRequest(@NotBlank(message = "ticket is required") String ticket) {
}
