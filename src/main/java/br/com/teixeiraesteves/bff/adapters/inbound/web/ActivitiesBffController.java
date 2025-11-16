package br.com.teixeiraesteves.bff.adapters.inbound.web;

import br.com.teixeiraesteves.bff.adapters.outbound.srv.ActivitiesSrvClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/atividade")
@Tag(
        name = "Atividades (BFF)",
        description = "Endpoints de atividades expostos pelo BFF e encaminhados ao SRV"
)
@SecurityRequirement(name = "bearerAuth") // aplicado a todos os métodos da classe
public class ActivitiesBffController {

    private final ActivitiesSrvClient client;

    public ActivitiesBffController(ActivitiesSrvClient client) {
        this.client = client;
    }

    @GetMapping
    @Operation(summary = "Lista atividades (via BFF)")
    public ResponseEntity<?> list(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(name = "excessao",      required = false) List<String> excessao,
            @RequestParam(name = "categoria",     required = false) List<String> categoria,
            @RequestParam(name = "tag",           required = false) List<String> tag,
            @RequestParam(name = "categoriaTerm", required = false) String categoriaTerm
    ) {
        var response = client.list(
                authorization,
                page,
                limit,
                excessao,
                categoria,
                tag,
                categoriaTerm
        );
        return ResponseEntity
                .status(response.getStatusCode())
                .body(response.getBody());
    }

    @GetMapping("/categorias")
    @Operation(summary = "Lista categorias (via BFF)")
    public ResponseEntity<?> getCategorias(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
            @RequestParam(name = "excessao", required = false) List<String> excessao
    ) {
        var response = client.listCategorias(authorization, excessao);
        return ResponseEntity
                .status(response.getStatusCode())
                .body(response.getBody());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca atividade por id (via BFF)")
    public ResponseEntity<?> getById(
            @PathVariable Long id,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) {

        var response = client.getById(id, authorization);
        return ResponseEntity
                .status(response.getStatusCode())
                .body(response.getBody());
    }

    @PostMapping
    @Operation(summary = "Cria nova atividade (via BFF)")
    public ResponseEntity<?> create(
            @RequestBody Object body,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) {

        var response = client.create(body, authorization);
        return ResponseEntity
                .status(response.getStatusCode())
                .body(response.getBody());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza atividade (via BFF)")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody Object body,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) {

        var response = client.update(id, body, authorization);
        return ResponseEntity
                .status(response.getStatusCode())
                .body(response.getBody());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove atividade (via BFF)")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) {

        var response = client.delete(id, authorization);
        return ResponseEntity
                .status(response.getStatusCode())
                .build();
    }

}