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

    public ResponseEntity<LoginResponse> login(LoginRequest body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<LoginRequest> entity = new HttpEntity<>(body, headers);
        try {
            return restTemplate.exchange(
                    baseUrl + "/api/auth/login",
                    HttpMethod.POST,
                    entity,
                    LoginResponse.class
            );
        } catch (HttpStatusCodeException ex) {
            return ResponseEntity.status(ex.getStatusCode())
                    .headers(safeHeaders(ex))
                    .build();
        }
    }

    public ResponseEntity<LoginResponse> verify2FA(TwoFactorVerifyRequest body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<TwoFactorVerifyRequest> entity = new HttpEntity<>(body, headers);
        try {
            return restTemplate.exchange(
                    baseUrl + "/api/auth/2fa/verify",
                    HttpMethod.POST,
                    entity,
                    LoginResponse.class
            );
        } catch (HttpStatusCodeException ex) {
            return ResponseEntity.status(ex.getStatusCode())
                    .headers(safeHeaders(ex))
                    .build();
        }
    }

    public ResponseEntity<TokenRefreshResponse> revalidate(TokenRefreshRequest body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<TokenRefreshRequest> entity = new HttpEntity<>(body, headers);
        try {
            return restTemplate.exchange(
                    baseUrl + "/api/auth/revalidate", // alinhado ao SRV
                    HttpMethod.POST,
                    entity,
                    TokenRefreshResponse.class
            );
        } catch (HttpStatusCodeException ex) {
            return ResponseEntity.status(ex.getStatusCode())
                    .headers(safeHeaders(ex))
                    .build();
        }
    }

    private static HttpHeaders safeHeaders(HttpStatusCodeException ex) {
        HttpHeaders h = ex.getResponseHeaders();
        return (h != null ? h : new HttpHeaders());
    }

    // ===== DTOs alinhados ao SRV =====
    // /api/auth/login
    public record LoginRequest(String username, String password, String totp) {}

    public record LoginResponse(String status, String token) {}

    // /api/auth/2fa/verify  -> { "username": "...", "code": "123456" }
    public record TwoFactorVerifyRequest(String username, String code) {}

    // /api/auth/revalidate -> { "token": "<jwt>" }
    public record TokenRefreshRequest(String token) {}

    public record TokenRefreshResponse(String token) {}
}
