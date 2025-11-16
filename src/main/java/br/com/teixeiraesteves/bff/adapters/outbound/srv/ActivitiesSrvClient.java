package br.com.teixeiraesteves.bff.adapters.outbound.srv;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

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

    public ResponseEntity<Object> list(
            String authorization,
            int page,
            int limit,
            List<String> excessao,
            List<String> categoria,
            List<String> tag,
            String categoriaTerm
    ) {
        HttpEntity<Void> entity = new HttpEntity<>(headers(authorization));

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(baseUrl + "/api/atividade")
                .queryParam("page", page)
                .queryParam("limit", limit);

        if (excessao != null) {
            excessao.forEach(e -> builder.queryParam("excessao", e));
        }
        if (categoria != null) {
            categoria.forEach(c -> builder.queryParam("categoria", c));
        }
        if (tag != null) {
            tag.forEach(t -> builder.queryParam("tag", t));
        }
        if (categoriaTerm != null && !categoriaTerm.isBlank()) {
            builder.queryParam("categoriaTerm", categoriaTerm);
        }

        String url = builder.toUriString();

        return restTemplate.exchange(
                url,
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

    public ResponseEntity<Object> listCategorias(
            String authorization,
            List<String> excessao
    ) {
        HttpEntity<Void> entity = new HttpEntity<>(headers(authorization));

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(baseUrl + "/api/atividade/categorias");

        if (excessao != null) {
            excessao.forEach(e -> builder.queryParam("excessao", e));
        }

        String url = builder.toUriString();

        return restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                Object.class
        );
    }
}
