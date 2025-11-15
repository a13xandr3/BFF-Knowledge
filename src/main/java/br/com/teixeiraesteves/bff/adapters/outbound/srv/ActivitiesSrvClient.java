package br.com.teixeiraesteves.bff.adapters.outbound.srv;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ActivitiesSrvClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public ActivitiesSrvClient(RestTemplate restTemplate,
                               @Value("${srv.base-url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    private HttpHeaders headers(String authorization) {
        HttpHeaders headers = new HttpHeaders();
        if (authorization != null && !authorization.isBlank()) {
            headers.set(HttpHeaders.AUTHORIZATION, authorization);
        }
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    public ResponseEntity<Object> list(String authorization) {
        HttpEntity<Void> entity = new HttpEntity<>(headers(authorization));
        return restTemplate.exchange(
                baseUrl + "/api/atividade",
                HttpMethod.GET,
                entity,
                Object.class
        );
    }

    public ResponseEntity<Object> getById(Long id, String authorization) {
        HttpEntity<Void> entity = new HttpEntity<>(headers(authorization));
        return restTemplate.exchange(
                baseUrl + "/api/atividade/" + id,
                HttpMethod.GET,
                entity,
                Object.class
        );
    }

    public ResponseEntity<Object> create(Object body, String authorization) {
        HttpEntity<Object> entity = new HttpEntity<>(body, headers(authorization));
        return restTemplate.exchange(
                baseUrl + "/api/atividade",
                HttpMethod.POST,
                entity,
                Object.class
        );
    }

    public ResponseEntity<Object> update(Long id, Object body, String authorization) {
        HttpEntity<Object> entity = new HttpEntity<>(body, headers(authorization));
        return restTemplate.exchange(
                baseUrl + "/api/atividade/" + id,
                HttpMethod.PUT,
                entity,
                Object.class
        );
    }

    public ResponseEntity<Void> delete(Long id, String authorization) {
        HttpEntity<Void> entity = new HttpEntity<>(headers(authorization));
        return restTemplate.exchange(
                baseUrl + "/api/atividade/" + id,
                HttpMethod.DELETE,
                entity,
                Void.class
        );
    }
}
