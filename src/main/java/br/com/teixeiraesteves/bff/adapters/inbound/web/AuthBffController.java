package br.com.teixeiraesteves.bff.adapters.inbound.web;

import br.com.teixeiraesteves.bff.adapters.outbound.srv.AuthSrvClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    // 1) LOGIN -> /api/auth/login (pass-through com Set-Cookie do SRV)
    @PostMapping("/login")
    @Operation(summary = "Realiza login (via BFF, pass-through)")
    public ResponseEntity<AuthSrvClient.LoginResponse> login(
            @RequestBody AuthSrvClient.LoginRequest body
    ) {
        var response = authSrvClient.login(body); // chama SRV /api/auth/login
        return ResponseEntity
                .status(response.getStatusCode())
                .headers(response.getHeaders())   // <- PRESERVA Set-Cookie
                .body(response.getBody());
    }

    // 2) 2FA VERIFY -> /api/auth/2fa/verify (pass-through com Set-Cookie do SRV)
    @PostMapping("/2fa/verify")
    @Operation(summary = "Verifica TOTP (via BFF, pass-through)")
    public ResponseEntity<AuthSrvClient.LoginResponse> verify2FA(
            @RequestBody AuthSrvClient.TwoFactorVerifyRequest body
    ) {
        var response = authSrvClient.verify2FA(body); // chama SRV /api/auth/2fa/verify
        return ResponseEntity
                .status(response.getStatusCode())
                .headers(response.getHeaders())   // <- PRESERVA Set-Cookie (refresh)
                .body(response.getBody());
    }

    // 3) REVALIDATE -> /api/auth/revalidate (pass-through)
    @PostMapping("/revalidate")
    @Operation(summary = "Revalida token (via BFF, pass-through)")
    public ResponseEntity<AuthSrvClient.TokenRefreshResponse> revalidate(
            @RequestBody AuthSrvClient.TokenRefreshRequest body
    ) {
        var response = authSrvClient.revalidate(body); // chama SRV /api/auth/revalidate
        return ResponseEntity
                .status(response.getStatusCode())
                .headers(response.getHeaders())   // nada impede de preservar headers aqui também
                .body(response.getBody());
    }
}
