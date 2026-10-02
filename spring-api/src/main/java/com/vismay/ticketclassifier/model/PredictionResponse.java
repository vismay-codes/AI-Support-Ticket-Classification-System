package com.vismay.ticketclassifier.model;

public record PredictionResponse(String ticket, String category, Double confidence) {
}
