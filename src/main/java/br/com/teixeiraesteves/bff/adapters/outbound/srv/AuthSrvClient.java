package br.com.teixeiraesteves.bff.adapters.outbound.srv;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@Component
public class AuthSrvClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public AuthSrvClient(RestTemplate restTemplate,
                         @Value("${srv.base-url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    public ResponseEntity<LoginResponse> login(LoginRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<LoginRequest> entity = new HttpEntity<>(request, headers);

        return restTemplate.exchange(
                baseUrl + "/api/auth/login",
                HttpMethod.POST,
                entity,
                LoginResponse.class
        );
    }

    public ResponseEntity<TokenRefreshResponse> refresh(TokenRefreshRequest request) {
        HttpHeaders h = new HttpHeaders(); h.setContentType(MediaType.APPLICATION_JSON);
        try {
            return restTemplate.exchange(
                    baseUrl + "/api/auth/revalidate",
                    HttpMethod.POST,
                    new HttpEntity<>(request, h),
                    TokenRefreshResponse.class
            );
        } catch (HttpStatusCodeException ex) {
            return ResponseEntity.status(ex.getStatusCode()).build();
        }
    }

    // DTOs alinhados ao contrato do SRV (/api/auth/login)
    public record LoginRequest(
            String username,
            String password,
            String totp
    ) {}

    public record LoginResponse(
            String status,
            String token
    ) {}

    public record TokenRefreshRequest(String token) {}

    public record TokenRefreshResponse(String token) {}
}