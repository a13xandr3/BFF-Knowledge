package br.com.teixeiraesteves.bff.adapters.inbound.web;

import br.com.teixeiraesteves.bff.adapters.outbound.srv.FilesSrvClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/files")
@Tag(name = "Arquivos (BFF)", description = "Endpoints de arquivos expostos pelo BFF e encaminhados ao SRV")
public class FilesBffController {

    private final FilesSrvClient client;

    public FilesBffController(FilesSrvClient client) {
        this.client = client;
    }

    @GetMapping("/{id}/raw")
    @Operation(summary = "Download de arquivo bruto (via BFF)")
    public ResponseEntity<byte[]> downloadRaw(
            @PathVariable Long id,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) {

        var response = client.downloadRaw(id, authorization);
        HttpHeaders headers = new HttpHeaders();
        headers.putAll(response.getHeaders());
        return new ResponseEntity<>(response.getBody(), headers, response.getStatusCode());
    }
}
