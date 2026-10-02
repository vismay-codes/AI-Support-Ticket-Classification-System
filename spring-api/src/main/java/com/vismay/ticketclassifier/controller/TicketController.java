package com.vismay.ticketclassifier.controller;

import com.vismay.ticketclassifier.model.TicketRequest;
import com.vismay.ticketclassifier.model.TicketResponse;
import com.vismay.ticketclassifier.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {
    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping("/classify")
    public ResponseEntity<TicketResponse> classify(@Valid @RequestBody TicketRequest request) {
        return ResponseEntity.ok(ticketService.classifyAndSave(request.ticket().trim()));
    }

    @GetMapping
    public ResponseEntity<List<TicketResponse>> getAll() {
        return ResponseEntity.ok(ticketService.getAll());
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> stats() {
        return ResponseEntity.ok(Map.of("totalTickets", ticketService.count()));
    }
}
