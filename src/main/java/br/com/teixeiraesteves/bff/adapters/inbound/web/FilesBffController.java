package br.com.teixeiraesteves.bff.adapters.inbound.web;

import br.com.teixeiraesteves.bff.adapters.outbound.srv.FilesSrvClient;
import br.com.teixeiraesteves.bff.domain.model.FileData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/files")
@Tag(name = "Arquivos (BFF)", description = "Endpoints de arquivos expostos pelo BFF e encaminhados ao SRV")
public class FilesBffController {

    private final FilesSrvClient client;

    public FilesBffController(FilesSrvClient client) {
        this.client = client;
    }

    /**
     * Upload de arquivo binário via BFF.
     * Recebe exatamente o mesmo contrato do Front e repassa ao SRV.
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload de arquivo bruto (via BFF)")
    public ResponseEntity<FilesSrvClient.FileSavedResponse> upload(
            @RequestPart("file") MultipartFile file,
            @RequestParam String contentEncoding,
            @RequestParam String hashSha256Hex,
            @RequestParam long originalSizeBytes,
            @RequestParam(required = false) Long gzipSizeBytes,
            @RequestParam(required = false) String mimeType,
            @RequestParam(required = false) String filename,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization
    ) throws Exception {

        var response = client.upload(
                file,
                contentEncoding,
                hashSha256Hex,
                originalSizeBytes,
                gzipSizeBytes,
                mimeType,
                filename,
                authorization
        );

        return ResponseEntity
                .status(response.getStatusCode())
                .headers(response.getHeaders()) // preserva Content-Type, etc.
                .body(response.getBody());
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

    @GetMapping("/{id}")
    @Operation(summary = "Snapshot de metadados do arquivo (via BFF)")
    public ResponseEntity<FileData> snapshot(
            @PathVariable Long id,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) {

        var response = client.snapshot(id, authorization);
        return ResponseEntity.status(response.getStatusCode())
                .headers(response.getHeaders())
                .body(response.getBody());
    }

    @GetMapping(value = "/{id}/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @Operation(summary = "Download do arquivo original (via BFF)")
    public ResponseEntity<InputStreamResource> download(
            @PathVariable Long id,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) {

        var response = client.downloadOriginal(id, authorization);

        HttpHeaders headers = new HttpHeaders();
        headers.putAll(response.getHeaders());

        byte[] body = response.getBody() != null ? response.getBody() : new byte[0];
        InputStreamResource resource = new InputStreamResource(new java.io.ByteArrayInputStream(body));

        return new ResponseEntity<>(resource, headers, response.getStatusCode());
    }

    @GetMapping("/{id}/download-gzip")
    @Operation(summary = "Download do arquivo gzipado (via BFF)")
    public ResponseEntity<byte[]> downloadGzip(
            @PathVariable Long id,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) {

        var response = client.downloadGzip(id, authorization);
        HttpHeaders headers = new HttpHeaders();
        headers.putAll(response.getHeaders());
        return new ResponseEntity<>(response.getBody(), headers, response.getStatusCode());
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Exclusão lógica/física do arquivo (via BFF)")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) {

        var response = client.delete(id, authorization);
        return ResponseEntity.status(response.getStatusCode())
                .headers(response.getHeaders())
                .build();
    }

    @PostMapping(path = "/batch/snapshots",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Snapshots em lote (POST via BFF)")
    public ResponseEntity<List<FileData>> batchSnapshotsPost(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Lista de IDs e flag para incluir base64 no payload"
            )
            @Valid @RequestBody br.com.teixeiraesteves.bff.dto.BatchSnapshotsRequest body,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) {

        var response = client.batchSnapshotsPost(body, authorization);
        return ResponseEntity.status(response.getStatusCode())
                .headers(response.getHeaders())
                .body(response.getBody());
    }

    @GetMapping("/batch/snapshots")
    @Operation(summary = "Snapshots em lote via query string (via BFF)")
    public ResponseEntity<List<FileData>> batchSnapshotsGet(
            @RequestParam List<Long> ids,
            @RequestParam(defaultValue = "false") boolean includeBase64,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) {

        var response = client.batchSnapshotsGet(ids, includeBase64, authorization);
        return ResponseEntity.status(response.getStatusCode())
                .headers(response.getHeaders())
                .body(response.getBody());
    }

}