package br.com.teixeiraesteves.bff.adapters.outbound.srv;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class FilesSrvClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public FilesSrvClient(RestTemplate restTemplate,
                          @Value("${srv.base-url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    private HttpHeaders headers(String authorization) {
        HttpHeaders headers = new HttpHeaders();
        if (authorization != null && !authorization.isBlank()) {
            headers.set(HttpHeaders.AUTHORIZATION, authorization);
        }
        return headers;
    }

    public ResponseEntity<byte[]> downloadRaw(Long id, String authorization) {
        HttpEntity<Void> entity = new HttpEntity<>(headers(authorization));

        ResponseEntity<byte[]> response = restTemplate.exchange(
                baseUrl + "/api/files/" + id + "/raw",
                HttpMethod.GET,
                entity,
                byte[].class
        );

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.putAll(response.getHeaders());

        return new ResponseEntity<>(response.getBody(), responseHeaders, response.getStatusCode());
    }
}
