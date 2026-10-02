package com.vismay.ticketclassifier.service;

import com.vismay.ticketclassifier.model.PredictionResponse;
import com.vismay.ticketclassifier.model.TicketResponse;
import com.vismay.ticketclassifier.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class TicketService {
    private final RestClient restClient;
    private final TicketRepository repository;

    public TicketService(
            RestClient.Builder builder,
            TicketRepository repository,
            @Value("${ml.service.url:http://127.0.0.1:5000}") String mlServiceUrl) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(3000);
        requestFactory.setReadTimeout(10000);
        this.restClient = builder
                .baseUrl(mlServiceUrl)
                .requestFactory(requestFactory)
                .build();
        this.repository = repository;
    }

    public TicketResponse classifyAndSave(String ticket) {
        PredictionResponse prediction = restClient.post()
                .uri("/predict")
                .body(new PredictionRequest(ticket))
                .retrieve()
                .body(PredictionResponse.class);

        if (prediction == null || prediction.category() == null) {
            throw new IllegalStateException("ML service returned no prediction");
        }

        double confidence = prediction.confidence() == null ? 0.0 : prediction.confidence();
        return repository.save(ticket, prediction.category(), confidence);
    }

    public List<TicketResponse> getAll() {
        return repository.findAll();
    }

    public long count() {
        return repository.count();
    }

    private record PredictionRequest(String ticket) {
    }
}
