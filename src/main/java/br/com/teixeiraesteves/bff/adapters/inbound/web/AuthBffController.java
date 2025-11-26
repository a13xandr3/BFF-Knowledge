package br.com.teixeiraesteves.bff.adapters.inbound.web;

import br.com.teixeiraesteves.bff.adapters.outbound.srv.AuthSrvClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(
        name = "Auth (BFF)",
        description = "Endpoints de autenticação expostos pelo BFF e encaminhados ao SRV"
)
public class AuthBffController {

    private final AuthSrvClient authSrvClient;

    public AuthBffController(AuthSrvClient authSrvClient) {
        this.authSrvClient = authSrvClient;
    }

    @PostMapping("/login")
    @Operation(summary = "Realiza login (via BFF)")
    public ResponseEntity<AuthSrvClient.LoginResponse> login(
            @RequestBody AuthSrvClient.LoginRequest body
    ) {
        var response = authSrvClient.login(body);
        return ResponseEntity
                .status(response.getStatusCode())
                .body(response.getBody());
    }

    @PostMapping("/revalidate")
    public ResponseEntity<AuthSrvClient.TokenRefreshResponse> refresh(
            @RequestBody AuthSrvClient.TokenRefreshRequest body) {
        var response = authSrvClient.refresh(body);
        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }

}
