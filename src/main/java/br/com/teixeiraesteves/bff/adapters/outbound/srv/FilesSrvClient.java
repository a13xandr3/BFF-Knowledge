package br.com.teixeiraesteves.bff.adapters.outbound.srv;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

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

    public ResponseEntity<FileSavedResponse> upload(
            MultipartFile file,
            String contentEncoding,
            String hashSha256Hex,
            long originalSizeBytes,
            Long gzipSizeBytes,
            String mimeType,
            String filename,
            String authorization
    ) throws Exception {

        // Prepara recurso do arquivo (mantendo nome)
        String resolvedFilename = (filename != null && !filename.isBlank())
                ? filename
                : (file.getOriginalFilename() != null ? file.getOriginalFilename() : "file.bin");

        ByteArrayResource fileResource = new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return resolvedFilename;
            }
        };

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", fileResource);
        body.add("contentEncoding", contentEncoding);
        body.add("hashSha256Hex", hashSha256Hex);
        body.add("originalSizeBytes", String.valueOf(originalSizeBytes));

        if (gzipSizeBytes != null) {
            body.add("gzipSizeBytes", String.valueOf(gzipSizeBytes));
        }
        if (mimeType != null && !mimeType.isBlank()) {
            body.add("mimeType", mimeType);
        }
        if (filename != null && !filename.isBlank()) {
            body.add("filename", filename);
        }

        HttpEntity<MultiValueMap<String, Object>> entity =
                new HttpEntity<>(body, headers(authorization));

        return restTemplate.exchange(
                baseUrl + "/api/files",
                HttpMethod.POST,
                entity,
                FileSavedResponse.class
        );
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

    // DTO igual ao do SRV (pode ser record ou class)
    public record FileSavedResponse(Long id, String hashSha256Hex) {}
}
